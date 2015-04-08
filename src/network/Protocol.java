package network;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Holds constants used in the Protocol and network.
 * Created by tim on 7-4-15.
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
    public static final int TIMEOUT = 5000;

    public static final int COMMUNICATION_HEADER_LENGTH = 11;

    public static int CLIENT_ID = 0;

    //Packet types:
    public static final byte NULL_PACKET = 0;
    public static final byte DISCOVERY_PACKET = 1;
    public static final byte COMMUNICATION_PACKET = 2;


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
