package network.packet;

/**
 * Represents a packet
 * Sets up a basic packet, for easy construction and reading
 * @author Tim Hintzbergen
 * Created on 7-4-15.
 */
public class Packet {
    // -----<=>-----< Fields >-----<=>----- \\
    private byte[] data;
    private long sequenceNumber;
    private byte destination;

    // -----<=>-----< Constructors >-----<=>----- \\

    public Packet() {

    }

    public Packet(byte[] data) {
        this.data = data;
    }

    // -----<=>-----< Methods >-----<=>----- \\
    /**
     * Prints the data in a packet to the standard out (as a String)
     */
    public void print() {
        System.out.println(new String(this.getData()));
    }


    // -----<=>-----< Queries >-----<=>----- \\
    /**
     * Getter for the data field (byte[]) of this packet
     * @return byte[] with the data of the packet
     */
    public byte[] getData() {
        return this.data;
    }


    public byte[] getSequenceBytes(){
        byte[] out = new byte[4];
        out[0] = (byte) (sequenceNumber >> 24);
        out[1] = (byte) (sequenceNumber >> 16);
        out[2] = (byte) (sequenceNumber >> 8);
        out[3] = (byte) (sequenceNumber);
        return out;
    }

    /**
     * Generates a key for use in the map of Floating packets in OutgoingPacketHandler
     * @return key for use in the outgoing packet handler
     */
    public byte[] getFloatingKey(){
        byte[] out = new byte[5];
        System.arraycopy(getSequenceBytes(), 0, out, 0, 4);
        out[4] = destination;
        return out;
    }

    public byte getDestination(){
        return destination;
    }

    public byte[] toBytes(){
        //Shape the packet
        //TODO: Put the whole packet in a byte[]
        return this.data;
    }
}
