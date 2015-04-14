package tests.exceptions.network.packet;

import exceptions.network.packet.InvalidCommunicationHeaderLengthException;
import network.Protocol;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Wouter Bos
 * @since 8-4-15
 * Unit test for exceptions.network.packet.InvalidCommunicationHeaderLengthException
 */
public class InvalidCommunicationHeaderLengthExceptionTest {


    // -----<=>-----< Fields >-----<=>----- \\
    private InvalidCommunicationHeaderLengthException e;


    @Before
    public void setUp() throws Exception {
        e = new InvalidCommunicationHeaderLengthException();
    }


    /**
     * Tests the getMessage() method. Should give a proper description of the Exception.
     * @throws Exception assertionException when the description is not valid.
     */
    @Test
    public void testGetMessage() throws Exception {
        assert e.getMessage().equals("ERROR: The length of the Communication Header" +
                " is not " + Protocol.COMMUNICATION_HEADER_LENGTH + ".");
    }
}