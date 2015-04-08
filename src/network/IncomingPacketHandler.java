package network;

import network.packet.Packet;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.ArrayList;

/**
 * @author Gerben Meijer
 * @since 7-4-15.
 */
public class IncomingPacketHandler extends PacketHandler {
    private ArrayList<PacketListener> listeners;
    private byte[] buffer;

    /**
     * Constructs a new IncomingPacketHandler, this is done by the NetworkHandler.
     * The IncomingPacketHandler is then started in a new Thread
     * @param socket
     * @param buffersize
     */
    public IncomingPacketHandler(MulticastSocket socket, int buffersize){
        super(socket);
        this.buffer = new byte[buffersize];
        this.listeners = new ArrayList<>();
    }

    public void addListener(PacketListener listener){
        listeners.add(listener);
    }

    public void removeListener(PacketListener listener){
        listeners.remove(listener);
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
