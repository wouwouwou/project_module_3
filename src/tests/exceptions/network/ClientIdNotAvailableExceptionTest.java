package tests.exceptions.network;

import exceptions.network.ClientIdNotAvailableException;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Wouter Bos
 * @since 10-4-15
 * Unit test for exceptions.network.packet.NullPacketException
 */
public class ClientIdNotAvailableExceptionTest {


    // -----<=>-----< Fields >-----<=>----- \\
    private ClientIdNotAvailableException e;


    @Before
    public void setUp() throws Exception {
        e = new ClientIdNotAvailableException();
    }


    /**
     * Tests the getMessage() method. Should give a proper description of the Exception.
     * @throws Exception assertionException when the description is not valid.
     */
    @Test
    public void testGetMessage() throws Exception {
        assert e.getMessage().equals("No Client ID found by NetworkManager.getClientId()");
    }
}