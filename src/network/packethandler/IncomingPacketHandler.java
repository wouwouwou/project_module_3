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
import java.util.List;

/**
 * @author Gerben Meijer
 * @since 7-4-15.
 * Handles all incoming packets.
 */
public class IncomingPacketHandler extends PacketHandler {


    // -----<=>-----< Fields >-----<=>----- \\
    private ArrayList<DataListener> dataListeners;
    private ArrayList<AckListener> ackListeners;
    private ArrayList<List<Byte>> lastPackets;
    private byte[] buffer;


    // -----<=>-----< Constructor(s) >-----<=>----- \\
    /**
     * Constructs a new IncomingPacketHandler, which is done by the NetworkHandler.
     * The IncomingPacketHandler is then started in a new Thread
     * @param buffersize The accepted buffersize of the incoming packets.
     * @param networkManager The related NetworkManager.
     */
    public IncomingPacketHandler(NetworkManager networkManager, int buffersize){
        super(networkManager);
        this.buffer = new byte[buffersize];
        this.dataListeners = new ArrayList<>();
        this.ackListeners = new ArrayList<>();
        this.networkManager = networkManager;
        lastPackets = new ArrayList<>();
    }


    // -----<=>-----< Queries & Methods >-----<=>----- \\
    /**
     * Adds a DataListener to the list of DataListeners.
     * @param listener The DataListener to be added.
     */
    public void addDataListener(DataListener listener){
        if (!dataListeners.contains(listener)) {
            dataListeners.add(listener);
        }
    }

    /**
     * Adds an AckListener to the list of AckListeners.
     * @param listener The AckListener to be added.
     */
    public void addAckListener(AckListener listener){
        ackListeners.add(listener);
    }

    /**
     * Removes a DataListener from the list of DataListeners.
     * @param listener The DataListener to be removed.
     */
    public void removeDataListener(DataListener listener){
        dataListeners.remove(listener);
    }

    /**
     * Returns true if lastPackets contains the floating key of a packet.
     * @param packet Packet to be checked.
     * @return True if the floatingkey of the packet is in lastPackets.
     */
    private boolean isDuplicate(Packet packet){
        return lastPackets.contains(packet.getFloatingKey());
    }

    /**
     * Removes an AckListener from the list of AckListeners.
     * @param listener The AckListener to be removed.
     */
    public void removeAckListener(AckListener listener){
        ackListeners.remove(listener);
    }

    /**
     * Gets the list of AckListeners.
     * @return The list of AckListeners.
     */
    public ArrayList<AckListener> getAckListeners() {
        return ackListeners;
    }

    /**
     * Gets the list of DataListeners.
     * @return The list of DataListeners.
     */
    public ArrayList<DataListener> getDataListeners(){
        return dataListeners;
    }

    /**
     * Gets the content of the buffer.
     * @return The buffer.
     */
    public byte[] getBuffer(){
        return buffer;
    }

    /**
     * Receives the packets and calls the method handle() when a packet has been received.
     */
    @Override
    public void run() {
        DatagramPacket recv = new DatagramPacket(buffer, buffer.length);
        while(true){
            try {
                socket.receive(recv);
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
    private void handle(byte[] packet){
        switch (packet[0]){
            case Protocol.DISCOVERY_PACKET:
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
    private void handleDiscovery(byte[] packet){
        short seq = (short) ((Protocol.fixSign(packet[2]) << 8) + Protocol.fixSign(packet[3]));
        byte length = packet[1];
        boolean forward = false;

        if(seq > networkManager.getDiscoverySequenceNum()){
            //D-D-D-D-D-Drop that bass, ehh... table ;D
            networkManager.dropTable();
            networkManager.setDiscoverySequenceNum(seq);

            //This an update, so we have to forward
            forward = true;

            //Add all the new entries
            for(int i = Protocol.DISCOVERY_HEADER_LENGTH; i < length; i+=3){
                networkManager.putTableEntry(new byte[]{packet[i], (byte) (packet[i + 1] + 1), packet[i + 2]});
            }

        } else if (seq == networkManager.getDiscoverySequenceNum()){
            //If this is just an addition to the existing table

            for(int i = Protocol.DISCOVERY_HEADER_LENGTH; i < Protocol.DISCOVERY_HEADER_LENGTH + length; i+=3){

                //if the cost if the new entry is lower, use it and forward it
                byte[] entry = networkManager.getTableEntryByDestination(packet[i]);

                if(entry == null || packet[i+1] + 1 < entry[1]) {
                    networkManager.putTableEntry(new byte[]{packet[i], (byte) (packet[i + 1] + 1), packet[i + 2]});
                    forward = true;
                }

            }
        }
        //Forward this packet if we have to
        if(forward){
            networkManager.sendTable();
        }
    }

    /**
     * Handles with a CommunicationPacket.
     * @param packet The CommunicationPacket to be handled.
     */
    private void handleCommunication(byte[] packet){
        if(packet[3] == Protocol.CLIENT_ID){
            if ((packet[8] & Protocol.Flags.DATA) != 0){

                try {
                    Packet p = new Packet(packet);
                    Packet ack = networkManager.constructACK(p);
                    networkManager.send(ack);
                    if(!isDuplicate(p)) {
                        notifyDataListeners(p);
                        lastPackets.add(p.getFloatingKey());
                        if (lastPackets.size() >= Protocol.MAX_PACKET_BUFFER_SIZE) {
                            lastPackets.remove(0);
                        }
                    }


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

            try {
                networkManager.send(new Packet(packet));
            } catch (InvalidPacketException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Method to notify all DataListeners.
     * @param packet Notify all DataListeners that a packet has been received.
     */
    private void notifyDataListeners(Packet packet) {
        if(packet != null) {
            for (DataListener listener : dataListeners) {
                listener.onReceive(packet);
            }
        }
    }

    /**
     * Method to notify all AckListeners.
     * @param packet Notify all AckListeners that an ACK-packet has been received.
     */
    private void notifyAckListeners(Packet packet) {
        if(packet != null) {
            for (AckListener listener : ackListeners) {
                listener.onAck(packet);
            }
        }
    }
}
