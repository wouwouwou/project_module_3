package tests.network;

import network.Protocol;
import org.junit.Test;

/**
 * Unit test for network.Protocol
 * @author Wouter Bos
 * @since 8-4-15
 */
public class ProtocolTest {

    /**
     * Tests the static method of the Protocol class.
     * This method is needed because Java sucks with signed bits / bytes.
     * @throws Exception When fixSign does not fix the bits / bytes.
     */
    @Test
    public void testFixSign() throws Exception {
        int a = Protocol.fixSign((byte)-120);
        int b = Protocol.fixSign((byte)-50);
        assert (a == 136);
        assert (b == 206);
    }
}