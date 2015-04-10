package exceptions.network.packet;

import exceptions.network.InvalidPacketException;

/**
 * @author Wouter Bos
 * @since 8-4-15
 * A null packet is a packet that is empty
 * <p>
 *     A Null packet is a packet with Packet.type = Protocol.NULL_PACKET
 * </p>
 * @see //TODO reference to our protocol implementation
 */
public class NullPacketException extends InvalidPacketException {

    @Override
    public String getMessage() {
        return ("ERROR: This is a Null-Packet!");
    }

}
