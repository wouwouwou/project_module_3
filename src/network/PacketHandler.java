package network;


import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.ArrayList;

/**
 * Created by gerben on 7-4-15.
 */
public class PacketHandler implements Runnable {
    private ArrayList<PacketListener> listeners;
    private byte[] buffer;
    private MulticastSocket socket;

    public PacketHandler(MulticastSocket socket, int buffersize){
        this.socket = socket;
        this.buffer = new byte[buffersize];
    }

    public void addListener(PacketListener listener){
        listeners.add(listener);
    }

    public void removeListener(PacketListener listener){
        listeners.remove(listener);
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
                    listener.onRecieve(packet);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
}
