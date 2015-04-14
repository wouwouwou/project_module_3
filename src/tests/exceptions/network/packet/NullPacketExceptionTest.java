package tests.exceptions.network.packet;

import exceptions.network.packet.NullPacketException;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Wouter Bos
 * @since 8-4-15
 * Unit test for exceptions.network.packet.NullPacketException
 */
public class NullPacketExceptionTest {


    // -----<=>-----< Fields >-----<=>----- \\
    private NullPacketException e;


    @Before
    public void setUp() throws Exception {
        e = new NullPacketException();
    }


    /**
     * Tests the getMessage() method. Should give a proper description of the Exception.
     * @throws Exception assertionException when the description is not valid.
     */
    @Test
    public void testGetMessage() throws Exception {
        assert e.getMessage().equals("ERROR: This is a Null-Packet!");
    }
}