package network.packet;


/**
 * Extends Packet, a floating packet has a timestamp.
 * If the timestamp is reached, the packet linked to this packet is resent.
 * @author Tim Hintzbergen
 * Created on 7-4-15.
 */
public class FloatingPacket extends Packet {

    // -----<=>-----< Fields >-----<=>----- \\

    private long sentOn;

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
    //TODO proper exception handling, also with documenting (correctly refering to our implementation) - Woeter
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
