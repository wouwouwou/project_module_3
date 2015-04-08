package network;

import network.packet.Packet;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * @author Gerben Meijer
 * @since 7-4-15
 */
public class NetworkManager {
    // -----<=>-----< Fields >-----<=>----- \\

    private MulticastSocket socket;
    private IncomingPacketHandler incomingPacketHandler;
    private OutgoingPacketHandler outgoingPacketHandler;
    private InetAddress group;
    private ArrayList<Byte> routingTable;
    private short discoverySequenceNum = 0;

    // -----<=>-----< Main >-----<=>----- \\

    /**
     * Main method, to be changed
     * @param args
     */
    public static void main(String[] args){
        NetworkManager networkManager = new NetworkManager();
    }

    // -----<=>-----< Constructor >-----<=>----- \\

    /**
     * Builds a network manager
     * <p>
     *     Initializes the network manager.
     *      - Sets the multicast group address
     *      - Sets up the multicast group socket via the multicast group port
     *      - Sets the Protocol.CLIENT_ID
     *      - Joins the multicast group and sets up its handlers
     * </p>
     */
    public NetworkManager() {
        //Get the group address
        try {
            group = InetAddress.getByName(Protocol.GROUP_ADDRESS);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        //Create the Multicast socket
        try {
            socket = new MulticastSocket(Protocol.GROUP_PORT);

            //Join the multicast group
            socket.joinGroup(group);

            //Get our client ID and set Protocol.CLIENT_ID
            Protocol.CLIENT_ID = this.getClientId();
            System.out.println("Init with client id: " + Protocol.CLIENT_ID);


            //Create and start the IncomingPacketHandler
            incomingPacketHandler = new IncomingPacketHandler(socket, this, 1000);

            //Create and start the OutgoingPacketHandler
            outgoingPacketHandler = new OutgoingPacketHandler(socket, this);


            /**
             DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(),
             group, 6789);
             s.send(hi);
             // get their responses!
             byte[] buf = new byte[1000];
             DatagramPacket recv = new DatagramPacket(buf, buf.length);
             for (int i = 0; i < 2; i++) {
             s.receive(recv);
             System.out.println(new String(recv.getData()));
             }

             // OK, I'm done talking - leave the group...
             s.leaveGroup(group);
             **/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // -----<=>-----< Methods >-----<=>----- \\

    /**
     * Sends a packet via the broadcast socket
     * @param packet Packet
     */
    public void send(Packet packet){

        outgoingPacketHandler.send(packet, group);

    }

    // -----<=>-----< Queries >-----<=>----- \\

    /**
     * Returns the client id, taken from the ip address
     * <p>
     *     Builds an Enumeration of all NetworkInterfaces, checks their ip for the local address.
     *     The last number of the local address is your ID.
     * </p>
     * @return client id
     */
    public int getClientId(){
        InetAddress addr = null;
        try {
            Enumeration<InetAddress> addrs = NetworkInterface.getNetworkInterfaces().nextElement().getInetAddresses();
            //TODO Even naar kijken. Tweede deel (achter && ) returnt altijd true? | Woeter
            while (addrs.hasMoreElements() && (addr == null || addr.getAddress()[0] != 192)){
                addr = addrs.nextElement();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        if(addr == null){
            return 0;
        }
        return addr.getAddress()[3];
    }

    // Adds or replaces a table entry
    public void addTableEntry(byte[] entry){
        if(entry.length == 3){
            int index = getTableIndexByDestination(entry[0]);
            if(index == -1) {
                routingTable.add(entry[0]);
                routingTable.add(entry[1]);
                routingTable.add(entry[2]);
            } else {
                routingTable.set(index, entry[0]);
                routingTable.set(index + 1, entry[1]);
                routingTable.set(index + 2, entry[2]);
            }
        }
    }

    public byte[] getTableEntryByDestination(byte destination){
        for(int i = 0; i < routingTable.size(); i += 3){
            if(routingTable.get(i) == destination){
                return new byte[]{routingTable.get(i), routingTable.get(i+1), routingTable.get(i+2)};
            }

        }
        return null;
    }

    public int getTableIndexByDestination(byte destination){
        for(int i = 0; i < routingTable.size(); i += 3){
            if(routingTable.get(i) == destination){
                return i;
            }

        }
        return -1;
    }

    public void dropTable(){
        //Clear the table and
        routingTable.clear();
        routingTable.add((byte) Protocol.CLIENT_ID);
        routingTable.add((byte) 0);
        routingTable.add((byte) Protocol.CLIENT_ID);
    }

    public void sendTable(){
        byte[] packet = new byte[Protocol.DISCOVERY_HEADER_LENGTH + routingTable.size()];

        packet[0] = Protocol.DISCOVERY_PACKET;

        packet[1] = (byte) routingTable.size();

        packet[2] = (byte) (discoverySequenceNum >> 8);

        packet[3] = (byte) discoverySequenceNum;

        System.arraycopy(routingTable.toArray(), 0, packet, Protocol.DISCOVERY_HEADER_LENGTH, routingTable.size());


        try {
            socket.send(new DatagramPacket(packet, packet.length, group, Protocol.GROUP_PORT));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // -----<=>-----< Getters & Setters >-----<=>----- \\

    public IncomingPacketHandler getIncomingPacketHandler() {
        return incomingPacketHandler;
    }

    public InetAddress getGroup() {
        return group;
    }

    public void setDiscoverySequenceNum(short sequenceNum){
        discoverySequenceNum = sequenceNum;
    }

    public short getDiscoverySequenceNum(){
        return discoverySequenceNum;
    }

}
