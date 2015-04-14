package network;

import exceptions.network.ClientIdNotAvailableException;
import exceptions.network.InvalidPacketException;
import network.packet.Packet;
import network.packethandler.ClientMapPingListener;
import network.packethandler.IncomingPacketHandler;
import network.packethandler.OutgoingPacketHandler;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Gerben Meijer
 * @since 7-4-15
 * Class for managing with the ad-hoc network.
 */
public class NetworkManager {


    // -----<=>-----< Fields >-----<=>----- \\
    private final String clientName;
    private MulticastSocket socket;
    private IncomingPacketHandler incomingPacketHandler;
    private OutgoingPacketHandler outgoingPacketHandler;
    private InetAddress group;
    private final ArrayList<Byte> routingTable = new ArrayList<>();
    private ConcurrentHashMap<Byte, Byte> connectedClients;
    private short discoverySequenceNum = 0;
    private int sequenceNum;
    private long lastTableDrop = 0;

    //Add exclusions here v v v v v v v v v v v v v
    private final byte[] excluded = new byte[]{};


    // -----<=>-----< Constructor(s) >-----<=>----- \\
    /**
     * Constructs a NetworkManager
     * <p>
     *     Initializes the network manager.
     *      - Sets the multicast group address
     *      - Sets up the multicast group socket via the multicast group port
     *      - Sets the Protocol.CLIENT_ID
     *      - Joins the multicast group and sets up its handlers
     * </p>
     */
    public NetworkManager(String name) {
        this.clientName = name;
        //Get the group address
        try {
            group = InetAddress.getByName(Protocol.GROUP_ADDRESS);
        } catch (UnknownHostException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }


        //Create the connected clients map, consisting of: Byte clientId -> Byte missedPingRounds
        connectedClients = new ConcurrentHashMap<>();

        //Create the Multicast socket
        try {
            socket = new MulticastSocket(Protocol.GROUP_PORT);

            //Join the multicast group
            socket.joinGroup(group);

            //Get our client ID and set Protocol.CLIENT_ID
            try {
                Protocol.CLIENT_ID = this.getClientId();
                //Protocol.CLIENT_ID = 3;
            } catch (ClientIdNotAvailableException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
                System.err.println("Shutting down...");
                System.exit(-1);
            }
            //Protocol.CLIENT_ID = 2;
            System.out.println("Init with client id: " + Protocol.CLIENT_ID);
            sequenceNum = (Protocol.CLIENT_ID << 24);


            //Create and start the IncomingPacketHandler
            incomingPacketHandler = new IncomingPacketHandler(this, Protocol.RECEIVE_BUFFER_BYTES_SIZE);

            //Create and start the OutgoingPacketHandler
            outgoingPacketHandler = new OutgoingPacketHandler(this);

            //Fill the table (by dropping it) and send it
            dropTable();
            sendTable();

            //Add the ClientMapPingListener
            incomingPacketHandler.addDataListener(new ClientMapPingListener(this));

        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }


    // -----<=>-----< Methods >-----<=>----- \\
    /**
     * Sends a packet via the broadcast socket
     * @param packet Packet to be send.
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
     * @throws ClientIdNotAvailableException when no Id could be generated and causes the process to terminate with error code -1
     */
    private int getClientId() throws ClientIdNotAvailableException {
        InetAddress addr = null;
        try {
            Enumeration<InetAddress> addrs = NetworkInterface.getNetworkInterfaces().nextElement().getInetAddresses();
            while (addrs.hasMoreElements() && (addr == null || addr.getAddress()[0] != 192)){
                addr = addrs.nextElement();
            }
        } catch (SocketException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        if(addr == null) {
            throw new ClientIdNotAvailableException();
        }
        return addr.getAddress()[3];
    }

    /**
     * Puts a routingEntry into the routingTable
     * <p>
     *     This method adds or sets the given entry to the routingTable
     *     If this destination does not exist yet, it will be added
     *     If this destination does exist, it will be updated. Aswell as the connectedClients list (and the pings missed).
     * </p>
     * @param entry Entry to be put in the routingTable.
     * @see <a href="https://docs.google.com/spreadsheets/d/1txMKaJt0YtHc6zTXJE2hnVJPlrHriVockRcA48qDHl0/edit?usp=sharing">routingEntry</a>
     */
    public void putTableEntry(byte[] entry) {
        synchronized(routingTable) {
            if (entry.length == 3 && !shouldBeExcluded(entry)) {
                int index = getTableIndexByDestination(entry[0]);
                if (index != -1) {
                    routingTable.set(index, entry[0]);
                    routingTable.set(index + 1, entry[1]);
                    routingTable.set(index + 2, entry[2]);
                } else {
                    connectedClients.put(entry[0], (byte) 0);
                    routingTable.add(entry[0]);
                    routingTable.add(entry[1]);
                    routingTable.add(entry[2]);
                }
            }
        }
    }

    /**
     * Checks if the destination is in the routingTable and returns the entry
     * if the destination is in the routingTable.
     * <p>
     *     Loops over the routingTable for the entry which matches the given destination
     *     Gives the byte[destination, cost, next_hop] where the match occurs
     * </p>
     * @param destination The destination to be checked.
     * @return The routingTable entry if contains the destination
     */
    public byte[] getTableEntryByDestination(byte destination){
        synchronized(routingTable) {
            for (int i = 0; i < routingTable.size(); i += 3) {
                if (routingTable.get(i) == destination) {
                    return new byte[]{routingTable.get(i), routingTable.get(i + 1), routingTable.get(i + 2)};
                }
            }
            return null;
        }
    }

    /**
     * Gives the index where this destination occurs, it it occurs.
     * <p>
     *     Loops over the routingTable for the entry which matches the given destination
     *     Gives the index of this entry
     * </p>
     * @param destination destination to be checked.
     * @return index of the entry in the routingTable.
     */
    public int getTableIndexByDestination(byte destination){
        synchronized(routingTable) {
            for (int i = 0; i < routingTable.size(); i += 3) {
                if (routingTable.get(i) == destination) {
                    return i;
                }

            }
            return -1;
        }
    }

    /**
     * Clears the routingTable and re-initializes it
     * <p>
     *     clears the routingTable and adds the routingEntry to this client
     * </p>
     */
    public void dropTable(){
        synchronized(routingTable) {
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
            routingTable.add((byte) 0);
        }
    }

    /**
     * Broadcasts a Protocol.DISCOVERY_PACKET
     * <p>
     *     Constructs a Protocol.DISCOVERY_PACKET type packet that broadcasts the current routingTable
     * </p>
     */
    public void sendTable(){

        synchronized(routingTable) {

            byte[] packet = new byte[Protocol.DISCOVERY_HEADER_LENGTH + routingTable.size()];

            packet[0] = Protocol.DISCOVERY_PACKET;

            packet[1] = (byte) routingTable.size();

            packet[2] = (byte) (discoverySequenceNum << 8);

            packet[3] = (byte) discoverySequenceNum;

            byte[] table = new byte[routingTable.size()];

            for (int i = 0; i < routingTable.size(); i += 3) {
                table[i] = routingTable.get(i);
                table[i + 1] = routingTable.get(i + 1);
                table[i + 2] = (byte) Protocol.CLIENT_ID;
            }

            System.arraycopy(table, 0, packet, Protocol.DISCOVERY_HEADER_LENGTH, routingTable.size());
            try {
                socket.send(new DatagramPacket(packet, packet.length, group, Protocol.GROUP_PORT));
            } catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Increments the sequenceNumber and returns the new sequence-number.
     * <p>
     *      Increments the sequenceNumber with one, and prints the old sequenceNumber to the standard out and returns the new sequenceNumber
     * </p>
     * @return The new sequence-number.
     */
    public int nextSequenceNum(){
        sequenceNum += 1;
        return sequenceNum;
    }

    // -----<=>-----< Getters & Setters >-----<=>----- \\
    /**
     * Gets the time of the last time that the routingTable has been dropped in millis and as a long.
     * @return Time of the last routingTableDrop.
     */
    public long getLastTableDrop(){
        return lastTableDrop;
    }

    /**
     * Gets the related IncomingPacketHandler.
     * @return The related IncomingPacketHandler.
     */
    public IncomingPacketHandler getIncomingPacketHandler() {
        return incomingPacketHandler;
    }

    /**
     * Gets the group as InetAddress.
     * @return The group.
     */
    public InetAddress getGroup() {
        return group;
    }

    /**
     * Sets the DiscoverySequenceNumber.
     * @param sequenceNum The DiscoverySequenceNumber to be set.
     */
    public void setDiscoverySequenceNum(short sequenceNum){
        discoverySequenceNum = sequenceNum;
    }

    /**
     * Gets the DiscoverySequenceNumber as a short.
     * @return The DiscoverySequenceNumber.
     */
    public short getDiscoverySequenceNum(){
        return discoverySequenceNum;
    }

    /**
     * Gets the RoutingTable as an array of objects.
     * @return The routing-table.
     */
    public Object[] getRoutingTable() {
        return routingTable.toArray();
    }

    /**
     * Constructs a packet with given variables
     * <p>
     *     A more detailed version of the Packet constructor
     * </p>
     * @param destination   byte    destination for this packets, as a Protocol.CLIENT_ID
     * @param dataType      byte    represents the type of packet
     * @param data          byte[]  the data of the packet.
     * @return The constructed packet.
     */
    public Packet constructPacket(byte destination, byte dataType, byte[] data) {
        Packet packet = new Packet();
        packet.setDataType(dataType);
        packet.setData(data);
        packet.setDestination(destination);
        packet.setSource((byte) Protocol.CLIENT_ID);
        packet.setType(Protocol.COMMUNICATION_PACKET);
        packet.setSequenceNumber(nextSequenceNum());
        packet.setFlags(Protocol.Flags.DATA);
        return packet;
    }

    /**
     * Constructs a Acknowledgement packet
     * <p>
     *     Uses {@link Packet#Packet(byte[] data) Packet()} to make a default packet, then adding/replacing custom elements.
     *     Destination will be the source of the {@param packet} and the Data and Packet type will be set accordingly to our <a href="../../../Project_files/Protocol_design.pdf">protocol design</a>
     *     Also the <a href="../../../Project_files/Protocol_design.pdf">Ack flag</a> will be set and an empty data field will be supplied.
     * </p>
     * @param packet That packet that will be used to construct an acknowledgement (also that packet that will be acknowledged)
     * @return The constructed packet.
     * @throws InvalidPacketException if a malformed packet is used for construction
     */
    public Packet constructACK(Packet packet) throws InvalidPacketException {
        packet = new Packet(packet.toBytes());
        packet.setData(new byte[0]);
        packet.setDestination(packet.getSource());
        packet.setSource((byte) Protocol.CLIENT_ID);
        packet.setType(Protocol.COMMUNICATION_PACKET);
        packet.setFlags(Protocol.Flags.ACK);
        return packet;
    }

    /**
     * Constructs a broadcast Ping packet
     * <p>
     *     Uses {@link #constructPacket(byte destination, byte dataType, byte[] data) constructPacket()} to make a broadcasted ping packet.
     *     Difference being: Destination 0 and Protocol.dataType.PING. A name specified by {@link NetworkManager#getClientName() clientName}.
     * </p>
     * @return packet a ping Packet with a destination 0
     * @see #constructPacket(byte, byte, byte[]) constructPacket()
     */
    public Packet constructPing() {
        Packet ping;
        byte[] nameData = this.getClientName().getBytes();
        ping = constructPacket((byte) 0, Protocol.DataType.PING, nameData);
        return ping;
    }

    /**
     * Gets the related OutgoingPacketHandler.
     * @return The OutgoingPacketHandler.
     */
    public OutgoingPacketHandler getOutgoingPacketHandler() {
        return outgoingPacketHandler;
    }

    /**
     * Gets the MulticastSocket.
     * @return The MulticastSocket.
     */
    public MulticastSocket getSocket() {
        return socket;
    }

    /**
     * Gets the name of the Client.
     * @return The name of the Client.
     */
    public String getClientName() {
        return clientName;
    }

    /**
     * Gets all connected clients as a ConcurrentHashMap.
     * @return All connected clients.
     */
    public ConcurrentHashMap<Byte,Byte> getConnectedClients() {
        return connectedClients;
    }

    /**
     * Increases the pings missed in the connectedClients map
     * <p>
     *     Increases the pings missed on every entry in the connectedClients Map and resets DVR if changes have occured.
     *     Method will mostly be called on every #Protocol.PING_INTERVAL to increase the pings missed
     *     Every time a ping comes in or a new discovery packet comes in, the pings missed for that client will be reset.
     * </p>
     * @see Protocol#PING_INTERVAL
     */
    public void increasePingRound() {
        for(Byte key: connectedClients.keySet()){
            connectedClients.put(key, (byte) (connectedClients.get(key) + 1));
            if(connectedClients.get(key) > Protocol.MAX_MISSED_PINGROUNDS){
                dropTable();
                setDiscoverySequenceNum((short) (discoverySequenceNum + 1));
                sendTable();
                connectedClients.remove(key); }
        }
    }

    /**
     * Method for excluding clients. Is used for manually send packets
     * to a destination via another client. Returns true if
     *
     * @param entry The routingTableEntry which might be excluded.
     * @return True if the entry corresponds to one of the clients who have to be excluded.
     */
    private boolean shouldBeExcluded(byte[] entry){
        if(entry.length == 3){
            for(byte client : excluded){
                if (entry[2] == client){
                    return true;
                }
            }
            return false;
        }
        return true;
    }
}
