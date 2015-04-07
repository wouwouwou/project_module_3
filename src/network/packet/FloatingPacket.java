package network.packet;


/**
 * Extends Packet, a floating packet has a timestamp.
 * If the timestamp is reached, the packet linked to this packet is resent
 * Created by tim on 7-4-15.
 */
public class FloatingPacket extends Packet {
    private long sentOn;

    public FloatingPacket(){
        super();
        this.sentOn = System.currentTimeMillis();
    }

    public FloatingPacket(byte[] data){
        super(data);
        this.sentOn = System.currentTimeMillis();
    }

    public long getSentOn(){
        return sentOn;
    }
}
