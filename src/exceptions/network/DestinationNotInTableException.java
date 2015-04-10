package exceptions.network;

import exceptions.NetworkException;
import network.Protocol;

/**
 * @author Tim Hintzbergen
 * @since 10-4-15.
 */
public class DestinationNotInTableException extends NetworkException {

    private int destination = -1;

    public DestinationNotInTableException(byte destination) {
        this.destination = Protocol.fixSign(destination);
    }

    @Override
    public String getMessage() {
        if (this.destination != -1)
            return "Destination: " + this.destination + " is not found in the routingTable.";
        else
            return "the destination is not found in the routingTable.";
    }
}
