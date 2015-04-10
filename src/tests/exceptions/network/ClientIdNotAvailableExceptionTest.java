package tests.exceptions.network;

import exceptions.network.ClientIdNotAvailableException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by wouwouwou on 10-4-15.
 */
public class ClientIdNotAvailableExceptionTest {

    private ClientIdNotAvailableException e;

    @Before
    public void setUp() throws Exception {
        e = new ClientIdNotAvailableException();
    }

    @Test
    public void testGetMessage() throws Exception {
        assert e.getMessage().equals("No Client ID found by NetworkManager.getClientId()");
    }
}