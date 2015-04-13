package tests.network.packet;

import network.Protocol;
import org.junit.Before;
import org.junit.Test;
import network.packet.Packet;
import java.util.List;

/**
 * Unit test for network.packet.Packet
 * @author Wouter Bos
 * @since 8-4-15
 */
public class PacketTest {

    private Packet packet1;
    private Packet packet2;

    @Before
    public void setUp() throws Exception {
        packet1 = new Packet();
    }

    @Test
    public void testGetters() throws Exception {
        assert (packet1.getData().length == 0);
        assert (packet1.getDataType() == 0);
        assert (packet1.getDestination() == 0);
        assert (packet1.getFlags() == 0);
        assert (packet1.getNextHop() == 0);
        assert (packet1.getSequenceNumber() == 0);
        assert (packet1.getSource() == 0);
        assert (packet1.getType() == Protocol.COMMUNICATION_PACKET);
    }

    @Test
    public void testSetters() throws Exception {
        byte[] data = new byte[1];
        data[0] = 5;
        packet1.setData(data);
        assert (packet1.getData().length == 1);
        assert (packet1.getData()[0] == 5);

        packet1.setDataType((byte)4);
        assert (packet1.getDataType() == 4);

        packet1.setDestination((byte)3);
        assert (packet1.getDestination() == 3);

        packet1.setFlags((byte)2);
        assert (packet1.getFlags() == 2);

        packet1.setNextHop((byte)1);
        assert (packet1.getNextHop() == 1);

        packet1.setSequenceNumber(8);
        assert (packet1.getSequenceNumber() == 8);

        packet1.setSource((byte)7);
        assert (packet1.getSource() == 7);

        packet1.setType(Protocol.DISCOVERY_PACKET);
        assert (packet1.getType() == Protocol.DISCOVERY_PACKET);
    }

    @Test
    public void testToBytes() throws Exception {
        byte[] data = new byte[1];
        data[0] = 8;
        packet1.setData(data);
        packet1.setDataType((byte) 7);
        packet1.setDestination((byte) 6);
        packet1.setFlags((byte) 5);
        packet1.setNextHop((byte) 4);
        packet1.setSequenceNumber(3);
        packet1.setSource((byte) 2);
        packet1.setType(Protocol.DISCOVERY_PACKET);

        byte[] packetInBytes = packet1.toBytes();
        assert (packetInBytes.length == Protocol.COMMUNICATION_HEADER_LENGTH + data.length);
        assert (packetInBytes[0] == Protocol.DISCOVERY_PACKET);
        assert (packetInBytes[1] == 7);
        assert (packetInBytes[2] == 2);
        assert (packetInBytes[3] == 6);
        assert (packetInBytes[4] == 0);
        assert (packetInBytes[5] == 0);
        assert (packetInBytes[6] == 0);
        assert (packetInBytes[7] == 3);
        assert (packetInBytes[8] == 5);
        assert (packetInBytes[9] == 0);
        assert (packetInBytes[10] == 1);
        assert (packetInBytes[11] == 4);
        assert (packetInBytes[12] == 8);
    }

    @Test
    public void testGetSequenceBytes() throws Exception {
        packet1.setSequenceNumber(5);
        byte[] result = packet1.getSequenceBytes();
        assert (result[3] == 5);

        packet1.setSequenceNumber(256);
        result = packet1.getSequenceBytes();
        assert (result[2] == 1);

        packet1.setSequenceNumber(128);
        result = packet1.getSequenceBytes();
        assert (result[3] == -128);
    }

    @Test
    public void testGetFloatingKey() throws Exception {
        packet1.setSource((byte)10);
        packet1.setDestination((byte)20);

        packet1.setFlags((byte) 2);
        List<Byte> result = packet1.getFloatingKey();
        assert (result.size() == 5);
        assert (result.get(0) == 0);
        assert (result.get(1) == 0);
        assert (result.get(2) == 0);
        assert (result.get(3) == 0);
        assert (result.get(4) == 10);

        packet1.setFlags((byte) 1);
        result = packet1.getFloatingKey();
        assert (result.size() == 5);
        assert (result.get(0) == 0);
        assert (result.get(1) == 0);
        assert (result.get(2) == 0);
        assert (result.get(3) == 0);
        assert (result.get(4) == 20);
    }

}