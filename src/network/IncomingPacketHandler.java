package network;

import network.packet.Packet;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.ArrayList;

/**
 * @author Gerben Meijer
 * Created on 7-4-15.
 */
public class IncomingPacketHandler implements Runnable {
    private ArrayList<PacketListener> listeners;
    private byte[] buffer;
    private MulticastSocket socket;
    private Thread thread;

    /**
     * Constructs a new IncomingPacketHandler, this is done by the NetworkHandler.
     * The IncomingPacketHandler is then started in a new Thread
     * @param socket
     * @param buffersize
     */
    public IncomingPacketHandler(MulticastSocket socket, int buffersize){
        this.socket = socket;
        this.buffer = new byte[buffersize];
        this.listeners = new ArrayList<PacketListener>();
        this.thread = new Thread(this);
        thread.start();
    }

    public void addListener(PacketListener listener){
        listeners.add(listener);
    }

    public void removeListener(PacketListener listener){
        listeners.remove(listener);
    }

    public Thread getThread(){
        return thread;
    }

    public ArrayList<PacketListener> getListeners(){
        return listeners;
    }

    public byte[] getBuffer(){
        return buffer;
    }

    @Override
    public void run() {
        DatagramPacket recv = new DatagramPacket(buffer, buffer.length);
        Packet packet;
        while(true){


            try {
                socket.receive(recv);
                packet = new Packet(recv.getData());
                for(PacketListener listener: listeners){
                    listener.onReceive(packet);
                }
            } catch (IOException | Packet.InvalidPacketException e) {
                e.printStackTrace();
            }


        }
    }

    public static void printArray(Object[] objects){
        String out = "[";
        for (Object b: objects){
            out += b + " ";
        }
        out += "]";
        System.out.println(out);
    }
}
