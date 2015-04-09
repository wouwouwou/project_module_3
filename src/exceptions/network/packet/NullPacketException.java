package exceptions.network.packet;

import exceptions.network.InvalidPacketException;

/**
 * @author Wouter Bos
 * @since 8-4-15
 */
public class NullPacketException extends InvalidPacketException {

    @Override
    public String getMessage() {
        return ("ERROR: This is a Null-Packet!");
    }

}
