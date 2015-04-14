package exceptions.network.packet;

import exceptions.network.InvalidPacketException;

/**
 * @author Wouter Bos
 * @since 8-4-15
 * Specific Exception Class.
 * <p>
 *     Thrown if a Null-packet has been received. (A null packet is a packet that is empty, so the
 *     Packet.Type equals Protocol.NULL_PACKET)
 * </p>
 * @see <a href="../../../Project_files/Protocol_design.pdf">Protocol Design</a>
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
