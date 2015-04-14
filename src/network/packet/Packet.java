package network.packet;

import exceptions.network.packet.InvalidCommunicationHeaderLengthException;
import exceptions.network.packet.NullPacketException;
import network.Protocol;
import exceptions.network.InvalidPacketException;
import java.util.Arrays;
import java.util.List;

/**
 * @author Tim Hintzbergen
 * @since 7-4-15
 * Represents a packet for easy construction and reading.
 * @see <a href="../../../Project%20files/Protocol_design.pdf">Protocol Design</a>
 * @see Protocol
 */
public class Packet {


    // -----<=>-----< Fields >-----<=>----- \\
    private byte[] data = new byte[0];
    private int sequenceNumber = 0;
    private byte type = Protocol.COMMUNICATION_PACKET;
    private byte dataType = 0;
    private byte source = 0;
    private byte destination = 0;
    private byte flags = 0;
    private byte nextHop = 0;


    // -----<=>-----< Constructors >-----<=>----- \\
    /**
     * Empty packet constructor
     */
    public Packet() {}

    /**
     * Constructs a Packet from a byte[]
     * <p>
     *     This constructor will assign bytes from the byte[] to fields of the newly initialized Packet object.
     *     An InvalidPacketException is thrown and caught if the packet doesn't fulfill the requirements of <a href="../../../Project_files/Protocol_design.pdf">our protocol</a>.
     * </p>
     * @param packet byte[] Data to be constructed into a packet
     */
    public Packet(byte[] packet) throws InvalidPacketException {
        if (packet.length < Protocol.COMMUNICATION_HEADER_LENGTH) {
            throw new InvalidCommunicationHeaderLengthException();
        }

        type = packet[0];

        if(type == Protocol.NULL_PACKET) {
            throw new NullPacketException();
        }

        dataType = packet[1];

        source = packet[2];

        destination = packet[3];

        sequenceNumber = (Protocol.fixSign(packet[4]) << 24) + (Protocol.fixSign(packet[5]) << 16) +(Protocol.fixSign(packet[6]) << 8) + Protocol.fixSign(packet[7]);

        flags = packet[8];

        int dataLength = (Protocol.fixSign(packet[9]) << 8) + (Protocol.fixSign(packet[10]));

        nextHop = packet[11];

        data = new byte[dataLength];

        if(dataLength != 0 && data.length != 0) {
            System.arraycopy(packet, Protocol.COMMUNICATION_HEADER_LENGTH, data, 0, dataLength);
        }
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
     * Converts this (Packet) object to a (byte[])
     * <p>
     *     Converts the data fields of this packet to (byte)s, in a (byte[]), following our protocol implementation
     * </p>
     * @return byte[] The converted packet
     */
    public byte[] toBytes(){
        //Create the new byte[]

        byte[] out = new byte[Protocol.COMMUNICATION_HEADER_LENGTH + data.length];
        //Shape the packet
        out[0] = type;

        out[1] = dataType;

        out[2] = source;

        out[3] = destination;

        out[4] = (byte) (sequenceNumber >> 24);
        out[5] = (byte) (sequenceNumber >> 16);
        out[6] = (byte) (sequenceNumber >> 8);
        out[7] = (byte) (sequenceNumber);

        out[8] = flags;

        out[9] = (byte) (data.length >> 8);
        out[10] = (byte) (data.length);

        out[11] = nextHop;

        System.arraycopy(data, 0, out, Protocol.COMMUNICATION_HEADER_LENGTH, data.length);

        return out;
    }

    /**
     * Makes a deepCopy from this packet
     * <p>
     *     Builds a deepCopy from this packet by calling toBytes() and constructing a new packet from the byte[] using Packet(byte[])
     *     The new packet is a perfect copy and has a different reference.
     *     If a packet couldn't be cloned, a null object will be returned.
     * </p>
     * @return Packet a new (cloned) instance of the packet called upon, with a different reference
     * @see #toBytes()
     * @see #Packet(byte[]) Packet(byte[])
     */
    public Packet deepCopy() {
        try {
            return new Packet(this.toBytes());
        } catch (InvalidPacketException e) {
            e.printStackTrace();
            System.err.println("this Packet couldn't be cloned: " + "\n" + this);
            return null;
        }
    }
    
    /**
     * Gives a byte[] representation of the sequenceNumber (long) field
     * @return byte[] The sequence number converted to a byte array with 4 entries
     */
    public byte[] getSequenceBytes() {
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
    public List<Byte> getFloatingKey(){
        Byte[] out = new Byte[5];
        for (int i = 0; i < getSequenceBytes().length; i++) {
            out[i] = getSequenceBytes()[i];
        }
        if(flags == Protocol.Flags.ACK){
            out[4] = source;
        } else {
            out[4] = destination;
        }
        return Arrays.asList(out);
    }

    /**
     * Builds a String representation of a packet
     * @return String representation of a packet and its contents
     */
    public String toString(){
        String out = "Packet: \n";
        out += String.format("\t type: %s\n", type);
        out += String.format("\t data type: %s\n", dataType);
        out += String.format("\t source: %s\n", source);
        out += String.format("\t destination: %s\n", destination);
        out += String.format("\t sequence number: %s\n", sequenceNumber);
        out += String.format("\t flags: %s\n", flags);
        out += String.format("\t data length: %s\n", data.length);
        out += String.format("\t next hop: %s\n", nextHop);

        return out;
    }

    // -----<=>-----< Setters & Getters >-----<=>----- \\
    /**
     * Gets the data of this packet as a byte[].
     * @return The data of this packet.
     */
    public byte[] getData() {
        return this.data;
    }

    /**
     * Sets the data of this packet.
     * @param data The data to be set.
     */
    public void setData(byte[] data) {
        this.data = data;
    }

    /**
     * Gets the sequence-number of this packet as a long.
     * @return The sequence-number of this packet.
     */
    public long getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * Sets the sequence-number of this packet.
     * @param sequenceNumber The sequence-number to be set.
     */
    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * Gets the type of this packet as a byte.
     * @return The type of this packet.
     */
    public byte getType() {
        return type;
    }

    /**
     * Sets the type of this packet.
     * @param type The type to be set.
     */
    public void setType(byte type) {
        this.type = type;
    }

    /**
     * Gets the data-type of this packet as a byte.
     * @return The data-type of this packet.
     */
    public byte getDataType() {
        return dataType;
    }

    /**
     * Sets the data-type of this packet.
     * @param dataType The data-type to be set.
     */
    public void setDataType(byte dataType) {
        this.dataType = dataType;
    }

    /**
     * Gets the source of this packet as a byte.
     * @return The source of this packet.
     */
    public byte getSource() {
        return source;
    }

    /**
     * Sets the source of this packet.
     * @param source The source to be set.
     */
    public void setSource(byte source) {
        this.source = source;
    }

    /**
     * Gets the destination of this packet as a byte.
     * @return The destination of this packet.
     */
    public byte getDestination(){
        return destination;
    }

    /**
     * Sets the destination of this packet.
     * @param destination The destination to be set.
     */
    public void setDestination(byte destination) {
        this.destination = destination;
    }

    /**
     * Gets the flags of this packet as a byte.
     * @return The flags of this packet.
     */
    public byte getFlags() {
        return flags;
    }

    /**
     * Sets the flags of this packet.
     * @param flags The flags to be set.
     */
    public void setFlags(byte flags) {
        this.flags = flags;
    }

    /**
     * Adds a flag to this packet.
     * @param flag The flag to be added.
     */
    public void addFlag(byte flag){
        flags |= flag;
    }

    /**
     * Removes a flag from this packet.
     * @param flag The flag to be removed.
     */
    public void removeFlag(byte flag){
        flags &= ~flag;
    }

    /**
     * Checks if this packet contains a flag.
     * @param flag The flag to be checked.
     * @return True if this packet contains the flag to be checked.
     */
    public boolean hasFlag(byte flag){
        return (flags & flag) != 0;
    }

    /**
     * Gets the next-hop of this packet as a byte.
     * @return The next-hop of this packet.
     */
    public byte getNextHop() {
        return nextHop;
    }

    /**
     * Sets the next-hop of this packet.
     * @param nextHop The next-hop to be set.
     */
    public void setNextHop(byte nextHop) {
        this.nextHop = nextHop;
    }
}
