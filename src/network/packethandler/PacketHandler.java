package network.packethandler;

import network.NetworkManager;

import java.net.MulticastSocket;

/**
 * Abstract class for handling with packets.
 * @author Wouter Bos
 * @since 8-4-15.
 */
abstract class PacketHandler implements Runnable {

    // -----<=>-----< Fields >-----<=>----- \\
    MulticastSocket socket;
    private Thread thread;
    NetworkManager networkManager;

    // -----<=>-----< Constructor(s) >-----<=>----- \\
    PacketHandler(NetworkManager networkManager) {
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
