package exceptions.network.packet;

import exceptions.network.InvalidPacketException;
import network.Protocol;

/**
 * @author Wouter Bos
 * @since 8-4-15
 * Specific Exception Class
 * <p>
 *     Thrown when a packet is not at least the length of the header. This is therefore a faulty packet
 * </p>
 * @see <a href="../../../Project_files/Protocol_design.pdf">Protocol Design</a>
 * @see {@link Protocol#COMMUNICATION_HEADER_LENGTH Header length constant}
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
