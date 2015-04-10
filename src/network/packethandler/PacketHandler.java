package network.packethandler;

import network.NetworkManager;

import java.net.MulticastSocket;

/**
 * Abstract class for handling with packets.
 * @author Wouter Bos
 * @since 8-4-15.
 */
public abstract class PacketHandler implements Runnable {

    // -----<=>-----< Fields >-----<=>----- \\
    MulticastSocket socket;
    protected Thread thread;
    protected NetworkManager networkManager;

    // -----<=>-----< Constructor(s) >-----<=>----- \\
    public PacketHandler(NetworkManager networkManager) {
        this.socket = networkManager.getSocket();
        this.networkManager = networkManager;
        this.thread = new Thread(this);
        this.thread.start();
    }

    // -----<=>-----< Queries >-----<=>----- \\
    /**
     * A method for nicely shutting down the application.
     */
    public Thread getThread(){
        return thread;
    }

}
