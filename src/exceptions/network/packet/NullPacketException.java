package exceptions.network.packet;

import exceptions.network.InvalidPacketException;

/**
 * Specific Exception Class.
 * <p>
 *     Thrown if a Null-packet has been received. (A null packet is a packet that is empty, so the
 *     Packet.Type equals Protocol.NULL_PACKET)
 * </p>
 * @author Wouter Bos
 * @since 8-4-15
 * @see <a href="../../../../Project%20files/Protocol_design.pdf">Protocol Design</a>
 */
public class NullPacketException extends InvalidPacketException {

    /**
     * Overrides the getMessage() method with an own message.
     * @return Message of the Exception
     */
    @Override
    public String getMessage() {
        return ("ERROR: This is a Null-Packet!");
    }

}
