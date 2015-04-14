package exceptions.network.packet;

import exceptions.network.InvalidPacketException;
import network.Protocol;

/**
 * Specific Exception Class
 * <p>
 *     Thrown when a packet is not at least the length of the header. This is therefore a faulty packet
 * </p>
 * @author Wouter Bos
 * @since 8-4-15;
 * @see <a href="../../../../Project%20files/Protocol_design.pdf">Protocol Design</a>
 * @see Protocol#COMMUNICATION_HEADER_LENGTH
 */
public class InvalidCommunicationHeaderLengthException extends InvalidPacketException {

    /**
     * Overrides the getMessage() method with an own message.
     * @return Message of the Exception
     */
    @Override
    public String getMessage() {
        return ("ERROR: The length of the Communication Header is not " + Protocol.COMMUNICATION_HEADER_LENGTH + ".");
    }

}
