package tests.exceptions.network.packet;

import exceptions.network.packet.NullPacketException;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for exceptions.network.packet.NullPacketException
 * @author Wouter Bos
 * @since 8-4-15
 */
public class NullPacketExceptionTest {

    private NullPacketException e;

    @Before
    public void setUp() throws Exception {
        e = new NullPacketException();
    }

    @Test
    public void testGetMessage() throws Exception {
        assert e.getMessage().equals("ERROR: This is a Null-Packet!");
    }
}