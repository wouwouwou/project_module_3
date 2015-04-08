package network;

import network.packet.Packet;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Holds constants used in the Protocol and network.
 * @author Tim Hintzbergen
 * @since 7-4-15
 */
public class Protocol {
    // -----<=>-----< Fields >-----<=>----- \\
    /**
     * This is the multicast Address (String) used by our adhoc network
     */
    public static final String GROUP_ADDRESS = "228.2.2.2";

    /**
     * This is the multicast Port (int) used by our adhoc network
     */
    public static final int GROUP_PORT = 6789;

    /**
     * This is the Timeout before a packet is resent.
     */
    public static final int TIMEOUT = 200;

    public static final int COMMUNICATION_HEADER_LENGTH = 12;
    public static final int DISCOVERY_HEADER_LENGTH = 4;
    public static final long CONVERGE_TIME = 100;

    public static int CLIENT_ID = 0;

    //Packet types:
    public static final byte NULL_PACKET = 0;
    public static final byte DISCOVERY_PACKET = 1;
    public static final byte COMMUNICATION_PACKET = 2;


    //Internal classes
    public static class Flags {
        public static byte DATA = 1;
        public static byte ACK = 2;
    }

    public static class DataType{
        public static byte PING = 1;
        public static byte TEXT = 2;
        public static byte FILE = 3;
    }

    // -----<=>-----< Queries >-----<=>----- \\
    /**
     * Getter for the InetAddress of the multicast network
     * @return InetAddress of the group from the GROUP_ADDRESS String
     * @throws UnknownHostException
     */
    public InetAddress getGroupAddress() throws UnknownHostException {
        return InetAddress.getByName(GROUP_ADDRESS);
    }

}
