package network.packethandler;

import exceptions.network.InvalidPacketException;
import network.AckListener;
import network.NetworkManager;
import network.DataListener;
import network.Protocol;
import network.packet.Packet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;

/**
 * @author Gerben Meijer
 * @since 7-4-15.
 */
public class IncomingPacketHandler extends PacketHandler {

    // Fields
    private ArrayList<DataListener> dataListeners;
    private ArrayList<AckListener> ackListeners;
    private byte[] buffer;

    // Constructor(s)
    /**
     * Constructs a new IncomingPacketHandler, this is done by the NetworkHandler.
     * The IncomingPacketHandler is then started in a new Thread
     * @param buffersize
     */
    public IncomingPacketHandler(NetworkManager networkManager, int buffersize){
        super(networkManager);
        this.buffer = new byte[buffersize];
        this.dataListeners = new ArrayList<>();
        this.ackListeners = new ArrayList<>();
        this.networkManager = networkManager;
    }

    public void addDataListener(DataListener listener){
        dataListeners.add(listener);
    }

    public void addAckListener(AckListener listener){
        ackListeners.add(listener);
    }

    public void removeDataListener(DataListener listener){
        dataListeners.remove(listener);
    }

    public void removeAckListener(AckListener listener){
        ackListeners.remove(listener);
    }

    public ArrayList<DataListener> getDataListeners(){
        return dataListeners;
    }

    public byte[] getBuffer(){
        return buffer;
    }

    @Override
    public void run() {
        DatagramPacket recv = new DatagramPacket(buffer, buffer.length);
        Packet packet;
        while(true){
            try {
                socket.receive(recv);
                IncomingPacketHandler.printArray(recv.getData());
                handle(recv.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Prints an array to the standard out
     * @param objects Object[] Array to be printed
     */
    public static void printArray(Object[] objects){
        String out = "[";
        for (Object b: objects){
            out += b + " ";
        }
        out += "]";
        System.out.println(out);
    }

    /**
     * Prints an byte[] to the standard out
     * @param objects byte[] to be printed
     */
    public static void printArray(byte[] objects){
        String out = "[";
        for (Object b: objects){
            out += b + " ";
        }
        out += "]";
        System.out.println(out);
    }

    /**
     * Handles an incoming packet
     * <p>
     *     Discern which type the incoming packet is, and calls the appropriate handler method.
     * </p>
     * @param packet byte[] A received packet
     */
    public void handle(byte[] packet){
        switch (packet[0]){
            case Protocol.DISCOVERY_PACKET:
                System.out.println("New Discovery Packet recieved");
                handleDiscovery(packet);
                break;
            case Protocol.COMMUNICATION_PACKET:
                handleCommunication(packet);
                break;
            case Protocol.NULL_PACKET:
                //Do nothing?
                break;
        }
    }

    /**
     * Handler for packets of the Protocol.DISCOVERY_PACKET type
     * <p>
     *     Reads a discovery packet, editing the forwarding table if necessary or dropping it
     * </p>
     * @param packet byte[] The packet to be handled
     */
    public void handleDiscovery(byte[] packet){
        short seq = (short) ((Protocol.fixSign(packet[2]) << 8) + Protocol.fixSign(packet[3]));
        byte length = packet[1];
        boolean forward = false;
        IncomingPacketHandler.printArray(packet);
        System.out.printf("Discovery Packet: {seq: %s, length: %s}\n", seq, length);

        if(seq > networkManager.getDiscoverySequenceNum()){
            System.out.println("Dropping tables");
            //D-D-D-D-D-Drop that bass, ehh... table ;D
            networkManager.dropTable();
            networkManager.setDiscoverySequenceNum(seq);

            //This an update, so we have to forward
            forward = true;

            //Add all the new entries
            for(int i = Protocol.DISCOVERY_HEADER_LENGTH; i < length; i+=3){
                networkManager.addTableEntry(new byte[]{packet[i], (byte) (packet[i+1] + 1), packet[i+2]});
            }


        } else if (seq == networkManager.getDiscoverySequenceNum()){
            System.out.println("Adding entries (if available)");
            //If this is just an addition to the existing table

            for(int i = Protocol.DISCOVERY_HEADER_LENGTH; i < Protocol.DISCOVERY_HEADER_LENGTH + length; i+=3){

                //if the cost if the new entry is lower, use it and forward it
                if(networkManager.getTableEntryByDestination(packet[i]) == null || packet[i+1] > networkManager.getTableEntryByDestination(packet[i])[1] + 1) {
                    networkManager.addTableEntry(new byte[]{packet[i], (byte) (packet[i + 1] + 1), packet[i + 2]});
                    forward = true;
                }

            }
        }

        //Forward this packet if we have to
        if(forward){
            networkManager.sendTable();
            printArray(networkManager.getRoutingTable());
        }



    }

    public void handleCommunication(byte[] packet){
        if(packet[3] == Protocol.CLIENT_ID){
            if (packet[8] == Protocol.Flags.DATA){

                try {
                    Packet p = new Packet(packet);
                    Packet ack = networkManager.constructACK(p);
                    networkManager.send(ack);
                    notifyDataListeners(p);
                } catch (InvalidPacketException e) {
                    e.printStackTrace();
                }


            } else if(packet[8] == Protocol.Flags.ACK){

                try {
                    Packet original = networkManager.getOutgoingPacketHandler().handleACK(new Packet(packet));
                    notifyAckListeners(original);
                } catch (InvalidPacketException e) {
                    e.printStackTrace();
                }
            }
        } else if(packet[11] == Protocol.CLIENT_ID){

        }

    }

    private void notifyDataListeners(Packet packet) {
        if(packet != null) {
            for (DataListener listener : dataListeners) {
                listener.onReceive(packet);
            }
        }
    }

    private void notifyAckListeners(Packet packet) {
        if(packet != null) {
            for (AckListener listener : ackListeners) {
                listener.onAck(packet);
            }
        }
    }
}
