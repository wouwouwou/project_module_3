package tests.exceptions.packet;

import exceptions.network.packet.NullPacketException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author wouter Bos
 * @since 8-4-15
 */
public class NullPacketExceptionTest {

    NullPacketException e;

    @Before
    public void setUp() throws Exception {
        e = new NullPacketException();
    }

    @Test
    public void testGetMessage() throws Exception {
        assert e.getMessage().equals("ERROR: This is a Null-Packet!");
    }
}