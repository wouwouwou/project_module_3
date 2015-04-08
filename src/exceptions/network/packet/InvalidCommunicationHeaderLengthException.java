package exceptions.network.packet;

import exceptions.network.InvalidPacketException;
import network.Protocol;

/**
 * @author Wouter Bos
 * @since 8-4-15
 */
public class InvalidCommunicationHeaderLengthException extends InvalidPacketException {

    @Override
    public String getMessage() {
        return ("ERROR: The length of the Communication Header is not " + Protocol.COMMUNICATION_HEADER_LENGTH + ".");
    }

}
