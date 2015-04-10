package exceptions.network.packet;

import exceptions.network.InvalidPacketException;
import network.Protocol;

/**
 * @author Wouter Bos
 * @since 8-4-15
 * A packet has an incomplete header
 * <p>
 *     Thrown when a packet is not at-least the length of the header, this is therefore a faulty packet
 * </p>
 * @see //TODO reference to our protocol implementation
 */
public class InvalidCommunicationHeaderLengthException extends InvalidPacketException {

    @Override
    public String getMessage() {
        return ("ERROR: The length of the Communication Header is not " + Protocol.COMMUNICATION_HEADER_LENGTH + ".");
    }

}
