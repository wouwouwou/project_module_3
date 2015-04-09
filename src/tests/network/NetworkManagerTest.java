package tests.network;

import network.NetworkManager;
import network.Protocol;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

//TODO ExceptionHandling in NetworkManager. | Woeter Roeter
/**
 * Unit test for network.NetworkManager
 * @author Wouter Bos
 * @since 8-4-15
 */
public class NetworkManagerTest {
    private final static String NAME = "eennaam";

    private NetworkManager manager;

    @Before
    public void setUp() throws Exception {
        manager = new NetworkManager(NAME);
    }

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

        entry = manager.getTableEntryByDestination((byte) 12);
        assert (entry == null);
    }

    @Test
    public void testGetTableIndexByDestination() throws Exception {
        int entry = manager.getTableIndexByDestination((byte) Protocol.CLIENT_ID);
        assert (entry == 0);

        entry = manager.getTableIndexByDestination((byte)0);
        assert (entry == 3);

        entry = manager.getTableIndexByDestination((byte)12);
        assert (entry == -1);
    }

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

        assert (manager.getTableEntryByDestination((byte) 12) == null);

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

        assert (manager.getTableEntryByDestination((byte) 12) == null);
    }

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

        assert (manager.getTableEntryByDestination((byte) 12) == null);

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

        assert (manager.getTableEntryByDestination((byte) 8) == null);

        assert (manager.getTableEntryByDestination((byte) 12) == null);
    }

    @Test
    public void testNextSequenceNum() throws Exception {

    }

    @Test
    public void testGetLastTableDrop() throws Exception {

    }

    @Test
    public void testGetIncomingPacketHandler() throws Exception {

    }

    @Test
    public void testGetGroup() throws Exception {

    }

    @Test
    public void testSetDiscoverySequenceNum() throws Exception {

    }

    @Test
    public void testGetDiscoverySequenceNum() throws Exception {

    }

    @Test
    public void testGetRoutingTable() throws Exception {

    }

    @Test
    public void testConstructPacket() throws Exception {

    }

    @Test
    public void testConstructACK() throws Exception {

    }

    @Test
    public void testGetOutgoingPacketHandler() throws Exception {

    }
}