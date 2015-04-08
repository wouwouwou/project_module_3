package network;

import java.net.MulticastSocket;

/**
 * @author Wouter Bos
 * Created on 8-4-15.
 */
public abstract class PacketHandler implements Runnable {

    MulticastSocket socket;
    Thread thread;

    public PacketHandler(MulticastSocket socket) {
        this.socket = socket;
        this.thread = new Thread(this);
        this.thread.start();
    }

    public Thread getThread(){
        return thread;
    }

}
