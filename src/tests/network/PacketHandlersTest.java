package tests.network;

import gui.controller.MessageController;
import network.AckListener;
import network.DataListener;
import network.NetworkManager;
import network.Protocol;
import network.packet.Packet;
import network.packethandler.IncomingPacketHandler;
import network.packethandler.OutgoingPacketHandler;
import org.junit.Before;
import org.junit.Test;
import test.PrintingAckListener;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * @author Wouter Bos
 * @since 10-4-15
 */
public class PacketHandlersTest {

    private static final String NAME = "WoeterRoeter";
    private NetworkManager manager;
    private MessageController messageController;
    private PrintingAckListener ackListener;

    @Before
    public void setUp() throws Exception {
        manager = new NetworkManager(NAME);
        messageController = new MessageController(manager);
        ackListener = new PrintingAckListener();
    }

    @Test
    public void testIncomingPacketHandler() {
        testGetDataListeners();
        testRemoveDataListeners();
        testAddDataListener();
        testGetAckListeners();
        testAddAckListener();
        testRemoveAckListener();
        testGetBuffer();
    }

    private void testGetBuffer() {
        assert (manager.getIncomingPacketHandler().getBuffer().length == Protocol.RECEIVE_BUFFER_BYTES_SIZE);
    }

    private void testRemoveAckListener() {
        manager.getIncomingPacketHandler().removeAckListener(ackListener);
        assert (manager.getIncomingPacketHandler().getAckListeners().isEmpty());
    }

    private void testAddAckListener() {
        manager.getIncomingPacketHandler().addAckListener(ackListener);
        assert (manager.getIncomingPacketHandler().getAckListeners().contains(ackListener));
    }

    private void testGetAckListeners() {
        ArrayList<AckListener> ackListeners = manager.getIncomingPacketHandler().getAckListeners();
        assert (ackListeners.isEmpty());
    }

    private void testAddDataListener() {
        manager.getIncomingPacketHandler().addDataListener(messageController);
        assert (manager.getIncomingPacketHandler().getDataListeners().contains(messageController));
    }

    private void testRemoveDataListeners() {
        manager.getIncomingPacketHandler().removeDataListener(messageController);
        assert !(manager.getIncomingPacketHandler().getDataListeners().contains(messageController));
    }

    private void testGetDataListeners() {
        ArrayList<DataListener> datalisteners = manager.getIncomingPacketHandler().getDataListeners();
        assert (datalisteners.contains(messageController));
    }

    @Test
    public void testOutgoingPacketHandler() {
        testGetLastPingSend();
    }

    private void testGetLastPingSend() {
        assert (manager.getOutgoingPacketHandler().getLastPingSend() <= System.currentTimeMillis());
    }

}