package tests.network.packet;

import network.packet.FloatingPacket;
import network.packet.Packet;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for network.packet.FloatingPacket
 * @author Wouter Bos
 * @since 8-4-15
 */
public class FloatingPacketTest {


    // -----<=>-----< Fields >-----<=>----- \\
    private FloatingPacket packet;


    @Before
    public void setUp() throws Exception {
        Packet packet2 = new Packet();
        packet = new FloatingPacket(packet2.toBytes());
    }


    /**
     * Tests the getSentOn() method.
     * @throws Exception When the packet has been made in the future.
     */
    @Test
    public void testGetSentOn() throws Exception {
        assert (packet.getSentOn() <= System.currentTimeMillis());
    }


    /**
     * Tests the setSentOn() method.
     * @throws Exception If the method does not set the time properly.
     */
    @Test
    public void testSetSentOn() throws Exception {
        long time = System.currentTimeMillis();
        packet.setSentOn(time);
        assert (packet.getSentOn() == time);
    }
}