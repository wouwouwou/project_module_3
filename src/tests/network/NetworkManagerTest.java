package tests.network;

import network.NetworkManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit test for network.NetworkManager
 * @author Wouter Bos
 * @since 8-4-15
 */
public class NetworkManagerTest {

    private NetworkManager manager;

    @Before
    public void setUp() throws Exception {
        manager = new NetworkManager();
    }

    //TODO ExceptionHandling in NetworkManager.getClientId().
    @Test
    public void testGetClientId() throws Exception {
        int id = manager.getClientId();
    }

    @Test
    public void testAddTableEntry() throws Exception {

    }

    @Test
    public void testGetTableEntryByDestination() throws Exception {

    }

    @Test
    public void testGetTableIndexByDestination() throws Exception {

    }

    @Test
    public void testDropTable() throws Exception {

    }

    @Test
    public void testSendTable() throws Exception {

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