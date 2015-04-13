package network.packet;


import exceptions.network.InvalidPacketException;
import network.Protocol;

/**
 * @author Tim Hintzbergen
 * @since 7-4-15
 * Extends Packet, a floating packet has a timestamp.
 * <p>
 *     A floating packet represents an unacknowledged packet:
 *      - If the timestamp is reached, a new packet is constructed from this object.
 *      - This new packet is sent again and the timestamp is refreshed.
 * </p>
 */
public class FloatingPacket extends Packet {


    // -----<=>-----< Fields >-----<=>----- \\
    private long sentOn;
    private byte retries;


    // -----<=>-----< Constructors >-----<=>----- \\
    /**
     * Constructing a floating packet from a (byte[])
     * @param data byte[]
     * @throws InvalidPacketException from super constructor
     * @see network.packet.Packet#Packet(byte[])
     */
    //TODO Documenting especially in respect to Exception handling (correctly referring to our implementation) / Woeter
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

    /**
     * Returns a String representation of a Floating Packet using the super constructor
     * @return String
     */
    public String toString(){
        String out = super.toString();
        out += String.format("\t next hop: %s\n", sentOn);
        out += String.format("\t next hop: %s\n", retries);
        return out;
    }
}
