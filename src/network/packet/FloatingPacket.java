package network.packet;


/**
 * Extends Packet, a floating packet has a timestamp.
 * <p>
 *     A floating packet represents an unacknowledged packet:
 *      - If the timestamp is reached, a new packet is constructed from this object.
 *      - This new packet is sent again and the timestamp is refreshed.
 * </p>
 * @author Tim Hintzbergen
 * @since 7-4-15
 */
public class FloatingPacket extends Packet {

    // -----<=>-----< Fields >-----<=>----- \\

    private long sentOn;
    //TODO maybe a maximum amount of retries/resents

    // -----<=>-----< Constructors >-----<=>----- \\

    /**
     * Empty Constructor
     */
    public FloatingPacket(){
        super();
        this.sentOn = System.currentTimeMillis();
    }

    /**
     * Constructing a packet from a (byte[])
     * @param data byte[]
     * @throws InvalidPacketException
     */
    //TODO proper exception handling, also with documenting (correctly referring to our implementation) - Woeter
    public FloatingPacket(byte[] data) throws InvalidPacketException {
        super(data);
        this.sentOn = System.currentTimeMillis();
    }

    // -----<=>-----< Getters & Setters >-----<=>----- \\

    public long getSentOn(){
        return sentOn;
    }

    public void setSentOn(long sentOn) {
        this.sentOn = sentOn;
    }
}
