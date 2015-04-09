package tests.network;

import network.Protocol;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;

import static org.junit.Assert.*;

/**
 * Unit test for network.Protocol
 * @author Wouter Bos
 * @since 8-4-15
 */
public class ProtocolTest {

    @Test
    public void testFixSign() throws Exception {
        int a = Protocol.fixSign((byte)-120);
        int b = Protocol.fixSign((byte)-50);
        assert (a == 136);
        assert (b == 206);
    }
}