package network.packethandler;

import java.net.MulticastSocket;

/**
 * Abstract class for handling with packets.
 * @author Wouter Bos
 * @since 8-4-15.
 */
public abstract class PacketHandler implements Runnable {

    // -----<=>-----< Fields >-----<=>----- \\
    MulticastSocket socket;
    Thread thread;

    // -----<=>-----< Constructor(s) >-----<=>----- \\
    public PacketHandler(MulticastSocket socket) {
        this.socket = socket;
        this.thread = new Thread(this);
        this.thread.start();
    }

    // -----<=>-----< Queries >-----<=>----- \\
    public Thread getThread(){
        return thread;
    }

}
