package tests.network;

import gui.controller.MessageController;
import network.AckListener;
import network.DataListener;
import network.NetworkManager;
import network.Protocol;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;

/**
 * Class for testing the OutgoingPacketHandler and the IncomingPacketHandler.
 * @author Wouter Bos
 * @since 10-4-15
 */
public class PacketHandlersTest {

    // -----<=>-----< Fields >-----<=>----- \\
    private static final String NAME = "WoeterRoeter";
    private NetworkManager manager;
    private DataListener messageController;
    private AckListener ackListener;

    @Before
    public void setUp() throws Exception {
        manager = new NetworkManager(NAME);
        messageController = new MessageController(manager);
        ackListener = new MessageController(manager);
    }

    /**
     * Runs multiple tests for the IncomingPacketHandler class.
     * Multiple getters, adders removers etc. are beïng tested here.
     * @throws Exception When one of the tested methods does not work properly.
     */
    @Test
    public void testIncomingPacketHandler() throws Exception {
        testGetDataListeners();
        testRemoveDataListeners();
        testAddDataListener();
        testGetAckListeners();
        testRemoveAckListener();
        testAddAckListener();
        testGetBuffer();
    }

    private void testGetBuffer() {
        assert (manager.getIncomingPacketHandler().getBuffer().length == Protocol.RECEIVE_BUFFER_BYTES_SIZE);
    }

    private void testRemoveAckListener() {
        manager.getIncomingPacketHandler().removeAckListener(ackListener);
        assert !(manager.getIncomingPacketHandler().getAckListeners().contains(ackListener));
    }

    private void testAddAckListener() {
        manager.getIncomingPacketHandler().addAckListener(ackListener);
        assert (manager.getIncomingPacketHandler().getAckListeners().contains(ackListener));
    }

    private void testGetAckListeners() {
        ArrayList<AckListener> ackListeners = manager.getIncomingPacketHandler().getAckListeners();
        assert (ackListeners.equals(manager.getIncomingPacketHandler().getAckListeners()));
    }

    private void testAddDataListener() {
        manager.getIncomingPacketHandler().addDataListener(messageController);
        assert (manager.getIncomingPacketHandler().getDataListeners().contains(messageController));
    }

    private void testRemoveDataListeners() {
        manager.getIncomingPacketHandler().addDataListener(messageController);
        manager.getIncomingPacketHandler().removeDataListener(messageController);
        assert !(manager.getIncomingPacketHandler().getDataListeners().contains(messageController));
    }

    private void testGetDataListeners() {
        ArrayList<DataListener> datalisteners = manager.getIncomingPacketHandler().getDataListeners();
        assert (datalisteners.contains(messageController));
    }

    /**
     * Runs a test for the OutgoingPacketHandler class.
     * @throws Exception When one of the tested methods does not work properly.
     */
    @Test
    public void testOutgoingPacketHandler() throws Exception {
        testGetLastPingSend();
    }

    private void testGetLastPingSend() {
        assert (manager.getOutgoingPacketHandler().getLastPingSend() <= System.currentTimeMillis());
    }

}