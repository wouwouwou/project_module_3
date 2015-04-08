package tests.exceptions.packet;

import exceptions.network.packet.InvalidCommunicationHeaderLengthException;
import network.Protocol;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Wouter Bos
 * @since 8-4-15
 */
public class InvalidCommunicationHeaderLengthExceptionTest {

    private InvalidCommunicationHeaderLengthException e;

    @Before
    public void setUp() throws Exception {
        e = new InvalidCommunicationHeaderLengthException();
    }

    @Test
    public void testGetMessage() throws Exception {
        assert e.getMessage().equals("ERROR: The length of the Communication Header" +
                " is not " + Protocol.COMMUNICATION_HEADER_LENGTH + ".");
    }
}