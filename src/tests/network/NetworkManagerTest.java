package tests.network;

import network.NetworkManager;
import network.Protocol;
import network.packet.Packet;
import network.packethandler.IncomingPacketHandler;
import network.packethandler.OutgoingPacketHandler;
import org.junit.Before;
import org.junit.Test;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * @author Wouter Bos
 * @since 8-4-15
 * Unit test for network.NetworkManager
 */
public class NetworkManagerTest {

    // -----<=>-----< Fields >-----<=>----- \\
    private final static String NAME = "eennaam";
    private NetworkManager manager;

    @Before
    public void setUp() throws Exception {
        manager = new NetworkManager(NAME);
    }

    /**
     * Tests the getTableEntryByDestination() method
     * @throws Exception When the getter does not work.
     */
    @Test
    public void testGetTableEntryByDestination() throws Exception {
        byte[] entry = manager.getTableEntryByDestination((byte) Protocol.CLIENT_ID);
        assert (entry.length == 3);
        assert (entry[0] == Protocol.CLIENT_ID);
        assert (entry[1] == 0);
        assert (entry[2] == Protocol.CLIENT_ID);

        entry = manager.getTableEntryByDestination((byte)0);
        assert (entry.length == 3);
        assert (entry[0] == 0);
        assert (entry[1] == 0);
        assert (entry[2] == 0);

        assert (manager.getTableEntryByDestination((byte) 12) == null);
    }

    /**
     * Tests the getTableIndexByDestination() method
     * @throws Exception When the getter does not work.
     */
    @Test
    public void testGetTableIndexByDestination() throws Exception {
        int entry = manager.getTableIndexByDestination((byte) Protocol.CLIENT_ID);
        assert (entry == 0);

        entry = manager.getTableIndexByDestination((byte)0);
        assert (entry == 3);

        assert (manager.getTableIndexByDestination((byte) 12) == -1);
    }

    /**
     * Tests the putTableEntry() method
     * @throws Exception When the entry has not been put in the routingtable properly.
     */
    @Test
    public void testPutTableEntry() throws Exception {
        byte[] entry = {8, 4, 8};
        manager.putTableEntry(entry);

        assert (manager.getTableIndexByDestination((byte) Protocol.CLIENT_ID) == 0);
        assert (manager.getTableIndexByDestination((byte)0) == 3);
        assert (manager.getTableIndexByDestination((byte)8) == 6);
        assert (manager.getTableIndexByDestination((byte)12) == -1);

        assert (manager.getTableEntryByDestination((byte) Protocol.CLIENT_ID).length == 3);
        assert (manager.getTableEntryByDestination((byte) Protocol.CLIENT_ID)[0] == Protocol.CLIENT_ID);
        assert (manager.getTableEntryByDestination((byte) Protocol.CLIENT_ID)[1] == 0);
        assert (manager.getTableEntryByDestination((byte) Protocol.CLIENT_ID)[2] == Protocol.CLIENT_ID);

        assert (manager.getTableEntryByDestination((byte)0).length == 3);
        assert (manager.getTableEntryByDestination((byte)0)[0] == 0);
        assert (manager.getTableEntryByDestination((byte)0)[1] == 0);
        assert (manager.getTableEntryByDestination((byte)0)[2] == 0);

        assert (manager.getTableEntryByDestination((byte)8).length == 3);
        assert (manager.getTableEntryByDestination((byte)8)[0] == 8);
        assert (manager.getTableEntryByDestination((byte)8)[1] == 4);
        assert (manager.getTableEntryByDestination((byte)8)[2] == 8);

        assert (manager.getTableEntryByDestination((byte)12) == null);

        byte[] entry2 = {8, 1, (byte)Protocol.CLIENT_ID};
        manager.putTableEntry(entry2);

        assert (manager.getTableIndexByDestination((byte) Protocol.CLIENT_ID) == 0);
        assert (manager.getTableIndexByDestination((byte)0) == 3);
        assert (manager.getTableIndexByDestination((byte)8) == 6);
        assert (manager.getTableIndexByDestination((byte)12) == -1);

        assert (manager.getTableEntryByDestination((byte) Protocol.CLIENT_ID).length == 3);
        assert (manager.getTableEntryByDestination((byte) Protocol.CLIENT_ID)[0] == Protocol.CLIENT_ID);
        assert (manager.getTableEntryByDestination((byte) Protocol.CLIENT_ID)[1] == 0);
        assert (manager.getTableEntryByDestination((byte) Protocol.CLIENT_ID)[2] == Protocol.CLIENT_ID);

        assert (manager.getTableEntryByDestination((byte)0).length == 3);
        assert (manager.getTableEntryByDestination((byte)0)[0] == 0);
        assert (manager.getTableEntryByDestination((byte)0)[1] == 0);
        assert (manager.getTableEntryByDestination((byte)0)[2] == 0);

        assert (manager.getTableEntryByDestination((byte)8).length == 3);
        assert (manager.getTableEntryByDestination((byte)8)[0] == 8);
        assert (manager.getTableEntryByDestination((byte)8)[1] == 1);
        assert (manager.getTableEntryByDestination((byte)8)[2] == Protocol.CLIENT_ID);

        assert (manager.getTableEntryByDestination((byte)12) == null);
    }

    /**
     * Tests the dropTable() method
     * @throws Exception When the table is not beïng dropped properly.
     */
    @Test
    public void testDropTable() throws Exception {
        byte[] entry = {8, 4, 8};
        manager.putTableEntry(entry);

        assert (manager.getTableIndexByDestination((byte) Protocol.CLIENT_ID) == 0);
        assert (manager.getTableIndexByDestination((byte)0) == 3);
        assert (manager.getTableIndexByDestination((byte)8) == 6);
        assert (manager.getTableIndexByDestination((byte)12) == -1);

        assert (manager.getTableEntryByDestination((byte) Protocol.CLIENT_ID).length == 3);
        assert (manager.getTableEntryByDestination((byte) Protocol.CLIENT_ID)[0] == Protocol.CLIENT_ID);
        assert (manager.getTableEntryByDestination((byte) Protocol.CLIENT_ID)[1] == 0);
        assert (manager.getTableEntryByDestination((byte) Protocol.CLIENT_ID)[2] == Protocol.CLIENT_ID);

        assert (manager.getTableEntryByDestination((byte)0).length == 3);
        assert (manager.getTableEntryByDestination((byte)0)[0] == 0);
        assert (manager.getTableEntryByDestination((byte)0)[1] == 0);
        assert (manager.getTableEntryByDestination((byte)0)[2] == 0);

        assert (manager.getTableEntryByDestination((byte)8).length == 3);
        assert (manager.getTableEntryByDestination((byte)8)[0] == 8);
        assert (manager.getTableEntryByDestination((byte)8)[1] == 4);
        assert (manager.getTableEntryByDestination((byte)8)[2] == 8);

        assert (manager.getTableEntryByDestination((byte)12) == null);

        manager.dropTable();

        assert (manager.getTableIndexByDestination((byte) Protocol.CLIENT_ID) == 0);
        assert (manager.getTableIndexByDestination((byte)0) == 3);
        assert (manager.getTableIndexByDestination((byte)8) == -1);
        assert (manager.getTableIndexByDestination((byte)12) == -1);

        assert (manager.getTableEntryByDestination((byte) Protocol.CLIENT_ID).length == 3);
        assert (manager.getTableEntryByDestination((byte) Protocol.CLIENT_ID)[0] == Protocol.CLIENT_ID);
        assert (manager.getTableEntryByDestination((byte) Protocol.CLIENT_ID)[1] == 0);
        assert (manager.getTableEntryByDestination((byte) Protocol.CLIENT_ID)[2] == Protocol.CLIENT_ID);

        assert (manager.getTableEntryByDestination((byte)0).length == 3);
        assert (manager.getTableEntryByDestination((byte)0)[0] == 0);
        assert (manager.getTableEntryByDestination((byte)0)[1] == 0);
        assert (manager.getTableEntryByDestination((byte)0)[2] == 0);

        assert (manager.getTableEntryByDestination((byte)8) == null);

        assert (manager.getTableEntryByDestination((byte)12) == null);
    }

    /**
     * Tests the nextSequenceNum() method
     * @throws Exception When the sequencenumber won't ++
     */
    @Test
    public void testNextSequenceNum() throws Exception {
        int a = manager.nextSequenceNum();
        int b = manager.nextSequenceNum();
        assert (b == a + 1);
        a = manager.nextSequenceNum();
        assert (a == b + 1);
        b = manager.nextSequenceNum();
        assert (b == a + 1);
        a = manager.nextSequenceNum();
        assert (a == b + 1);
    }

    /**
     * Tests the getLastTableDrop() method
     * @throws Exception When the getter does not work.
     */
    @Test
    public void testGetLastTableDrop() throws Exception {
        long first = manager.getLastTableDrop();
        manager.dropTable();
        long second = manager.getLastTableDrop();
        manager.dropTable();
        long third = manager.getLastTableDrop();
        manager.dropTable();
        long fourth = manager.getLastTableDrop();
        assert (first <= second && second <= third && third <= fourth);
    }

    /**
     * Tests the getIncomingPacketHandler() method
     * @throws Exception When the getter does not work.
     */
    @Test
    public void testGetIncomingPacketHandler() throws Exception {
        IncomingPacketHandler handler = manager.getIncomingPacketHandler();
        assert (handler != null);
    }

    /**
     * Tests the getGroup() method
     * @throws Exception When the getter does not work.
     */
    @Test
    public void testGetGroup() throws Exception {
        InetAddress address = manager.getGroup();
        assert(address.getHostName().equals(Protocol.GROUP_ADDRESS));
    }

    /**
     * Tests the discoverySequenceNum() method
     * @throws Exception When the getter or the setter does not work.
     */
    @Test
    public void testDiscoverySequenceNum() throws Exception {
        short a = 20;
        manager.setDiscoverySequenceNum(a);
        assert (manager.getDiscoverySequenceNum() == a);
    }

    /**
     * Tests the getRoutingTable() method
     * @throws Exception When the getter does not work.
     */
    @Test
    public void testGetRoutingTable() throws Exception {
        Object[] routingtable = manager.getRoutingTable();
        assert (routingtable.length > 0);
    }

    /**
     * Tests the constructPacket() method
     * @throws Exception When the packet is not beïng constructed properly.
     */
    @Test
    public void testConstructPacket() throws Exception {
        byte[] data = {7, 8, 9};
        byte datatype = 4;
        byte destination = 5;
        Packet packet = manager.constructPacket(destination, datatype, data);
        assert (packet.getDataType() == datatype);
        assert (Arrays.equals(packet.getData(), data));
        assert (packet.getDestination() == destination);
        assert (packet.getSource() == Protocol.CLIENT_ID);
        assert (packet.getType() == Protocol.COMMUNICATION_PACKET);
        assert (packet.getFlags() == Protocol.Flags.DATA);
    }

    /**
     * Tests the constructACK() method
     * @throws Exception When the ACK is not beïng constructed properly.
     */
    @Test
    public void testConstructACK() throws Exception {
        byte[] data = {7, 8, 9};
        byte datatype = 4;
        byte destination = 5;
        Packet packet = manager.constructPacket(destination, datatype, data);
        Packet ack = manager.constructACK(packet);

        assert (ack.getData().length == 0);
        assert (ack.getDestination() == packet.getSource());
        assert (ack.getSource() == Protocol.CLIENT_ID);
        assert (ack.getType() == Protocol.COMMUNICATION_PACKET);
        assert (ack.getFlags() == Protocol.Flags.ACK);
    }

    /**
     * Tests the getOutGoingPacketHandler() method
     * @throws Exception When the getter does not work.
     */
    @Test
    public void testGetOutgoingPacketHandler() throws Exception {
        OutgoingPacketHandler handler = manager.getOutgoingPacketHandler();
        assert (handler != null);
    }

    /**
     * Tests the getTableEntryByDestination() method
     * @throws Exception When the ping is not beïng constructed properly.
     */
    @Test
    public void testConstructPing() throws Exception {
        Packet ping = manager.constructPing();
        assert (ping.getDataType() == Protocol.DataType.PING);
        assert (Arrays.equals(ping.getData(), manager.getClientName().getBytes()));
        assert (ping.getDestination() == 0);
        assert (ping.getSource() == Protocol.CLIENT_ID);
        assert (ping.getType() == Protocol.COMMUNICATION_PACKET);
        assert (ping.getFlags() == Protocol.Flags.DATA);
    }

    /**
     * Tests the getClientName() method
     * @throws Exception When the getter does not work.
     */
    @Test
    public void testGetClientName() throws Exception{
        assert (manager.getClientName().equals(NAME));
    }

    /**
     * Tests the getSocket() method
     * @throws Exception When the getter does not work.
     */
    @Test
    public void testGetSocket() throws Exception {
        assert (manager.getSocket() != null);
    }
}