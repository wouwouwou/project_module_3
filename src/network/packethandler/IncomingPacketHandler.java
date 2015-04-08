package network.packethandler;

import network.NetworkManager;
import network.PacketListener;
import network.Protocol;
import network.packet.Packet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.ArrayList;

/**
 * @author Gerben Meijer
 * @since 7-4-15.
 */
public class IncomingPacketHandler extends PacketHandler {

    // Fields
    private ArrayList<PacketListener> listeners;
    private byte[] buffer;
    private NetworkManager networkManager;

    // Constructor(s)
    /**
     * Constructs a new IncomingPacketHandler, this is done by the NetworkHandler.
     * The IncomingPacketHandler is then started in a new Thread
     * @param socket
     * @param buffersize
     */
    public IncomingPacketHandler(MulticastSocket socket, NetworkManager networkManager, int buffersize){
        super(socket);
        this.buffer = new byte[buffersize];
        this.listeners = new ArrayList<>();
        this.networkManager = networkManager;
    }

    public void addListener(PacketListener listener){
        listeners.add(listener);
    }

    public void removeListener(PacketListener listener){
        listeners.remove(listener);
    }

    public ArrayList<PacketListener> getListeners(){
        return listeners;
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
                handle(recv.getData());
                packet = new Packet(recv.getData());
                for(PacketListener listener: listeners){
                    listener.onReceive(packet);
                }
            } catch (IOException | Packet.InvalidPacketException e) {
                e.printStackTrace();
            }
        }
    }

    public static void printArray(Object[] objects){
        String out = "[";
        for (Object b: objects){
            out += b + " ";
        }
        out += "]";
        System.out.println(out);
    }

    public static void printArray(byte[] objects){
        String out = "[";
        for (Object b: objects){
            out += b + " ";
        }
        out += "]";
        System.out.println(out);
    }

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

    public void handleDiscovery(byte[] packet){
        short seq = (short) ((Packet.fixSign(packet[2]) << 8) + Packet.fixSign(packet[3]));
        byte length = packet[1];
        boolean forward = false;
        IncomingPacketHandler.printArray(packet);
        System.out.printf("Discovery Packet: {seq: %s, length: %s}\n", seq, length);

        if(seq > networkManager.getDiscoverySequenceNum()){
            System.out.println("Dropping tables");
            //D-D-D-D-D-Drop that bass, ehh... table ;D
            networkManager.dropTable();

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

    }
}
