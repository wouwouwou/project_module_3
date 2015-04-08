package network.packet;

import network.Protocol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a packet
 * Sets up a basic packet, for easy construction and reading
 * @author Tim Hintzbergen
 * @since 7-4-15
 */
public class Packet {
    // -----<=>-----< Fields >-----<=>----- \\

    private byte[] data = new byte[0];
    private int sequenceNumber;
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
     * Constructs a (Packet) from a (byte[]), throws a exception if the (byte[]) does not statisfy our implementation
     * @param packet byte[] Data to be constructed into a packet
     * @throws InvalidPacketException
     */
    //TODO See other TODO about the InvalidPacketException
    public Packet(byte[] packet) throws InvalidPacketException {
        fromBytes(packet);
    }

    // -----<=>-----< Methods >-----<=>----- \\
    /**
     * Prints the data in a packet to the standard out (as a String)
     */
    public void print() {
        System.out.println(new String(this.getData()));
    }

    /**
     * Constructs a (Packet) object from a byte[]
     * <p>
     *     This method will assign bytes from the byte[] to fields of the new Packet object following our design implementation
     * </p>
     * @param packet byte[] (byte[]) packet you want to convert to a (Packet)
     * @throws InvalidPacketException if the packet does not follow our design implementation
     */
    //TODO proper exception handling, also with documenting (correctly refering to our implementation) - Woeter
    public void fromBytes(byte[] packet) throws InvalidPacketException {
        if (packet.length < Protocol.COMMUNICATION_HEADER_LENGTH){
            throw new InvalidPacketException();
        }

        type = packet[0];

        if(type == Protocol.NULL_PACKET){
            throw new InvalidPacketException();
        }

        dataType = packet[1];

        source = packet[2];

        destination = packet[3];

        sequenceNumber = (fixSign(packet[4]) << 24) + (fixSign(packet[5]) << 16) +(fixSign(packet[6]) << 8) + fixSign(packet[7]);

        flags = packet[8];

        int dataLength = (fixSign(packet[9]) << 8) + (fixSign(packet[10]));

        nextHop = packet[11];

        data = new byte[dataLength];

        System.arraycopy(packet, Protocol.COMMUNICATION_HEADER_LENGTH, data, 0, dataLength);

    }

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
     * Correctly converts a (byte) to a (int), keeping respect to signed bytes in java
     * @param data byte
     * @return int correctly converted data (byte) to (int)
     */
    public static int fixSign(byte data){
        //Function to fix signed stuff.
        long dataL = (long) data;
        return (int )dataL & 0xff;
    }


    // -----<=>-----< Queries >-----<=>----- \\
    /**
     * Getter for the data field (byte[]) of this packet
     * @return byte[] with the data of the packet
     */
    public byte[] getData() {
        return this.data;
    }

    /**
     * Gives a byte[] representation of the sequenceNumber (long) field
     * @return byte[] The sequence number converted to a byte array with 4 entries
     */
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
    public List<Byte> getFloatingKey(){
        Byte[] out = new Byte[5];
        for (int i = 0; i < getSequenceBytes().length; i++) {
            out[i] = getSequenceBytes()[i];
        }

        out[4] = destination;
        return Arrays.asList(out);
    }

    public String toString(){
        String out = "Packet: ";
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
    public void setData(byte[] data) {
        this.data = data;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getDataType() {
        return dataType;
    }

    public void setDataType(byte dataType) {
        this.dataType = dataType;
    }

    public byte getSource() {
        return source;
    }

    public void setSource(byte source) {
        this.source = source;
    }

    public byte getDestination(){
        return destination;
    }

    public void setDestination(byte destination) {
        this.destination = destination;
    }

    public byte getFlags() {
        return flags;
    }

    public void setFlags(byte flags) {
        this.flags = flags;
    }

    public byte getNextHop() {
        return nextHop;
    }

    public void setNextHop(byte nextHop) {
        this.nextHop = nextHop;
    }


    //-----------<=>---< Exceptions >---<=>-------------\\

    //TODO proper exception handling and documentation
    public class InvalidPacketException extends Exception{

    }
}
