package network;

/**
 * @author Tim Hintzbergen
 * @since 7-4-15
 * Holds constants used in the Protocol and network.
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
    public static final int TIMEOUT = 1100;

    public static final int COMMUNICATION_HEADER_LENGTH = 12;
    public static final int DISCOVERY_HEADER_LENGTH = 4;
    public static final long CONVERGE_TIME = 100;
    public static final long PING_INTERVAL = 1000;
    public static final long TABLE_DROP_INTERVAL = 30000;
    public static final short MAX_PACKET_BUFFER_SIZE = 500;


    //Number of simulanious packets before file packets are put in to a buffer.
    public static final int FILE_SEND_BUFFER_SIZE = 20;

    //Payload and buffer size
    public static final int RECEIVE_BUFFER_BYTES_SIZE = 4096;
    public static final int MAX_COMMUNICATION_PAYLOAD_SIZE = RECEIVE_BUFFER_BYTES_SIZE - COMMUNICATION_HEADER_LENGTH;
    public static final byte MAX_RETRIES = 10;
    public static final Byte MAX_MISSED_PINGROUNDS = 4;


    public static int CLIENT_ID = 0;

    //Packet types:
    public static final byte NULL_PACKET = 0;
    public static final byte DISCOVERY_PACKET = 1;
    public static final byte COMMUNICATION_PACKET = 2;

    //Nested classes
    public static class Flags {
        public static byte DATA = 1;
        public static byte ACK = 2;
        public static byte BROADCAST = 4;
    }

    public static class DataType{
        public static byte PING = 1;
        public static byte TEXT = 2;
        public static byte FILE = 3;
    }


    // -----<=>-----< Queries >-----<=>----- \\
    /**
     * Correctly converts a (byte) to a (int), keeping respect to signed bytes in java.
     * @param data byte
     * @return int correctly converted data (byte) to (int)
     */
    public static int fixSign(byte data){
        //Function to fix signed stuff.
        long dataL = (long) data;
        return (int )dataL & 0xff;
    }
}
