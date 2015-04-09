package network;

import exceptions.network.InvalidPacketException;
import network.packet.Packet;
import network.packethandler.IncomingPacketHandler;
import network.packethandler.OutgoingPacketHandler;

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
    private int sequenceNum;
    private long lastTableDrop = 0;

    // -----<=>-----< Main >-----<=>----- \\
    /**
     * Main method, to be changed
     * @param args
     */
    //TODO Main method, to be changed
    public static void main(String[] args){
        NetworkManager networkManager = new NetworkManager();
    }

    // -----<=>-----< Constructor(s) >-----<=>----- \\
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
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        //Create the routingTable
        routingTable = new ArrayList<>();

        //Create the Multicast socket
        try {
            socket = new MulticastSocket(Protocol.GROUP_PORT);

            //Join the multicast group
            socket.joinGroup(group);

            //Get our client ID and set Protocol.CLIENT_ID
            Protocol.CLIENT_ID = this.getClientId();
            System.out.println("Init with client id: " + Protocol.CLIENT_ID);
            sequenceNum = (Protocol.CLIENT_ID << 24);


            //Create and start the IncomingPacketHandler
            incomingPacketHandler = new IncomingPacketHandler(this, 1000);

            //Create and start the OutgoingPacketHandler
            outgoingPacketHandler = new OutgoingPacketHandler(this);

            //Fill the table (by dropping it) and send it
            dropTable();
            sendTable();

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
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    // -----<=>-----< Methods >-----<=>----- \\
    /**
     * Sends a packet via the broadcast socket
     * @param packet Packet
     */
    public void send(Packet packet){
        outgoingPacketHandler.send(packet);
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
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        if(addr == null){
            return 0;
        }
        return addr.getAddress()[3];
    }

    // Adds or replaces a table entry

    /**
     * Adds an routingEntry to the routingTable
     * <p>
     *     This method adds or sets the given entry to the routingTable
     *     If this destination does not exist yet, it will be added
     *     If this destination does exist, it will be updated.
     * </p>
     * @param entry byte[destination, cost, next_hop]
     */
    public void addTableEntry(byte[] entry){
        System.out.println("Adding table entry");
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
                IncomingPacketHandler.printArray(routingTable.toArray());
            }
        }
    }

    /**
     * Gives the byte[] where this destination occurs
     * <p>
     *     Loops over the routingTable for the entry which matches the given destination
     *     Gives the byte[destination, cost, next_hop] where the match occurs
     * </p>
     * @param destination
     * @return byte[destination, cost, next_hop]
     */
    public byte[] getTableEntryByDestination(byte destination){
        for(int i = 0; i < routingTable.size(); i += 3){
            if(routingTable.get(i) == destination){
                return new byte[]{routingTable.get(i), routingTable.get(i+1), routingTable.get(i+2)};
            }

        }
        return null;
    }

    /**
     * Gives the index where this destination occurs
     * <p>
     *     Loops over the routingTable for the entry which matches the given destination
     *     Gives the index of this entry
     * </p>
     * @param destination byte
     * @return  int index in the routingTable.
     *          -1 if destination not found
     */
    public int getTableIndexByDestination(byte destination){
        for(int i = 0; i < routingTable.size(); i += 3){
            if(routingTable.get(i) == destination){
                return i;
            }

        }
        return -1;
    }

    /**
     * Clears the routingTable and reinitializes it
     * <p>
     *     clears the routingTable and adds the routingEntry to this client
     * </p>
     */
    public void dropTable(){
        //Clear the table and
        lastTableDrop = System.currentTimeMillis();
        routingTable.clear();
        // Add yourself to the routingTable
        routingTable.add((byte) Protocol.CLIENT_ID);
        routingTable.add((byte) 0);
        routingTable.add((byte) Protocol.CLIENT_ID);
        // Add the broadcast routingEntry to the routingTable
        routingTable.add((byte) 0);
        routingTable.add((byte) 0);
        routingTable.add((byte) 0); //TODO next_hop needs to be changed later/somewhere else
    }

    /**
     * Broadcasts a Protocol.DISCOVERY_PACKET
     * <p>
     *     Constructs a Protocol.DISCOVERY_PACKET type packet that broadcasts the current routingTable
     * </p>
     */
    public void sendTable(){
        System.out.println("Sending table");
        byte[] packet = new byte[Protocol.DISCOVERY_HEADER_LENGTH + routingTable.size()];

        packet[0] = Protocol.DISCOVERY_PACKET;

        packet[1] = (byte) routingTable.size();

        packet[2] = (byte) (discoverySequenceNum << 8);

        packet[3] = (byte) discoverySequenceNum;

        byte[] table = new byte[routingTable.size()];

        for(int i = 0; i < routingTable.size(); i+= 3){
            table[i] = routingTable.get(i);
            table[i + 1] = routingTable.get(i+1);
            table[i + 2] = (byte) Protocol.CLIENT_ID;
        }

        System.arraycopy(table, 0, packet, Protocol.DISCOVERY_HEADER_LENGTH, routingTable.size());

        IncomingPacketHandler.printArray(routingTable.toArray());
        try {
            socket.send(new DatagramPacket(packet, packet.length, group, Protocol.GROUP_PORT));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Increments the sequenceNumber
     * <p>
     *      Increments the sequenceNumber with one, and prints the old sequenceNumber to the standard out and returns the new sequenceNumber
     * </p>
     * @return int sequenceNumber + 1
     */
    public int nextSequenceNum(){
        System.out.println(sequenceNum);
        sequenceNum += 1;
        return sequenceNum;
    }

    // -----<=>-----< Getters & Setters >-----<=>----- \\

    public long getLastTableDrop(){
        return lastTableDrop;
    }

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

    public Object[] getRoutingTable() {
        return routingTable.toArray();
    }

    /**
     * Constructs a packet with given variables
     * @param destination   byte    destination for this packets, as a Protocol.CLIENT_ID
     * @param dataType      byte    represents the type of packet
     * @param data
     * @return
     * @throws IOException
     */
    public Packet constructPacket(byte destination, byte dataType, byte[] data) throws IOException {
        Packet packet = new Packet();
        packet.setDataType(dataType);
        packet.setData(data);
        packet.setDestination(destination);
        packet.setSource((byte) Protocol.CLIENT_ID);
        packet.setType(Protocol.COMMUNICATION_PACKET);
        packet.setSequenceNumber(nextSequenceNum());
        byte[] route = getTableEntryByDestination(destination);
        if(route == null){
            throw new IOException(String.format("Destination %s unreachable.", destination));
        }
        packet.setNextHop(route[2]);
        packet.setFlags(Protocol.Flags.DATA);
        return packet;
    }

    public Packet constructACK(Packet packet) throws InvalidPacketException {
        packet = new Packet(packet.toBytes());
        packet.setData(new byte[0]);
        packet.setDestination(packet.getSource());
        packet.setSource((byte) Protocol.CLIENT_ID);
        packet.setType(Protocol.COMMUNICATION_PACKET);
        byte[] route = getTableEntryByDestination(packet.getDestination());
        if(route == null){
            throw new InvalidPacketException();
        }
        packet.setNextHop(route[2]);
        packet.setFlags(Protocol.Flags.ACK);
        return packet;
    }

    /**
     * Constructs a broadcast Ping packet
     * <p>
     *     Uses constructPacket(byte destination, byte dataType, byte[] data) to make a broadcasted ping packet
     *     With destination 0 and Protocol.dataType.
     * </p>
     * @return packet a ping Packet with a destination 0
     * @see #constructPacket(byte, byte, byte[])
     */
    public Packet constructPing() {
        Packet ping = null;
        try {
            ping = constructPacket((byte) 0, (byte) 1, new byte[]{});
        } catch (IOException e) {
            e.printStackTrace();
        }
        //TODO manipulate flags - Tim;  Not needed - Gerben
        return ping;
    }

    public OutgoingPacketHandler getOutgoingPacketHandler() {
        return outgoingPacketHandler;
    }

    public MulticastSocket getSocket() {
        return socket;
    }
}
