package network;

/**
 * Represents a packet
 * Sets up a basic packet, for easy construction and reading
 * Created by tim on 7-4-15.
 */
public class Packet {
    // -----<=>-----< Fields >-----<=>----- \\
    private byte[] dt;
    private byte src; //Not used yet

    // -----<=>-----< Constructors >-----<=>----- \\

    public Packet() {}

    public Packet(byte[] dt) {
        this.dt = dt;
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
        return this.dt;
    }

    /**
     * Getter for the source field (String) of this packet
     * @return String the source address of this packet
     */
    public byte getSource() {
        return this.src;
    }
}
