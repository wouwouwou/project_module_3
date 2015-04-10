package network.packet;


import exceptions.network.InvalidPacketException;
import network.Protocol;

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
    private byte retries;
    //TODO maybe a maximum amount of retries/resents

    // -----<=>-----< Constructors >-----<=>----- \\
    /**
     * Constructing a floating packet from a (byte[])
     * @param data byte[]
     * @throws InvalidPacketException from super constructor
     * @see network.packet.Packet#Packet(byte[])
     */
    //TODO proper exception handling, also with documenting (correctly referring to our implementation) - Woeter
    public FloatingPacket(byte[] data) throws InvalidPacketException {
        super(data);
        this.retries = Protocol.MAX_RETRIES;
        this.sentOn = System.currentTimeMillis();
    }

    // -----<=>-----< Getters & Setters >-----<=>----- \\

    public long getSentOn(){
        return sentOn;
    }

    public void setSentOn(long sentOn) {
        this.sentOn = sentOn;
    }


    public byte getRetries() {
        return retries;
    }

    public void setRetries(byte retries) {
        this.retries = retries;
    }

    /**
     * Decreases retries and returns true if the packet has to be deleted
     * @return true if there are no retries left.
     */
    public boolean decreaseRetries(){
        retries -= 1;
        return retries <= 0;
    }
}
