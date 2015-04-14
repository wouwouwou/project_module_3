package network.packethandler;

import network.NetworkManager;

import java.net.MulticastSocket;

/**
 * @author Wouter Bos
 * @since 8-4-15.
 * Abstract class for handling with packets.
 */
abstract class PacketHandler implements Runnable {


    // -----<=>-----< Fields >-----<=>----- \\
    final MulticastSocket socket;
    private final Thread thread;
    NetworkManager networkManager;


    // -----<=>-----< Constructor(s) >-----<=>----- \\
    /**
     * Constructor for this class. Also starts this class as a thread.
     * @param networkManager The related NetworkManager.
     */
    PacketHandler(NetworkManager networkManager) {
        this.socket = networkManager.getSocket();
        this.networkManager = networkManager;
        this.thread = new Thread(this);
        this.thread.start();
    }


    // -----<=>-----< Queries >-----<=>----- \\
    /**
     * A method for getting the thread related to this class.
     * @return Thread of this class.
     */
    public Thread getThread(){
        return thread;
    }

}
