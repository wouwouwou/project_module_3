package network.packet;

import network.Protocol;

/**
 * Represents a packet
 * Sets up a basic packet, for easy construction and reading
 * @author Tim Hintzbergen
 * Created on 7-4-15.
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

    // -----<=>-----< Constructors >-----<=>----- \\

    public Packet() {

    }

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

        int dataLength = (fixSign(packet[9]) <<8) + (fixSign(packet[10]));

        data = new byte[dataLength];

        System.arraycopy(packet, Protocol.COMMUNICATION_HEADER_LENGTH, data, 0, dataLength);

    }

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

        System.arraycopy(data, 0, out, Protocol.COMMUNICATION_HEADER_LENGTH, data.length);



        return out;
    }

    public static int fixSign(byte data){
        //Function to fix signed stuff.
        long dataL = (long) data;
        int dataI = (int )dataL & 0xff;
        return dataI;
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

    //-----------<=>---< Exceptions >---<=>-------------\\

    public class InvalidPacketException extends Exception{

    }
}
