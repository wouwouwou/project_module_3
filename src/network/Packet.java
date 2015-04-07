package network;

/**
 * Represents a packet
 * Sets up a basic packet, for easy construction and reading
 * Created by tim on 7-4-15.
 */
public class Packet {
    // -----<=>-----< Fields >-----<=>----- \\
    private byte[] dt;


    // -----<=>-----< Constructors >-----<=>----- \\

    public Packet() {}

    public Packet(byte[] dt) {
        this.dt = dt;
    }

    // -----<=>-----< Methods >-----<=>----- \\
    /**
     * Prints the data in a packet to the standart out (as a String)
     */
    public void print() {
        System.out.println(new String(this.getData()));
    }


    // -----<=>-----< Queries >-----<=>----- \\
    /**
     * Getter for the data field (byte[]) of a packet
     * @return byte[] with the data of the packet
     */
    public byte[] getData() {
        return this.dt;
    }
}
