package network;

import network.packet.FloatingPacket;
import network.packet.Packet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;

/**
 * Created by gerben on 7-4-15.
 */
public class OutgoingPacketHandler implements Runnable{

    private final HashMap<byte[], FloatingPacket> floatingPacketMap = new HashMap<byte[], FloatingPacket>();
    private MulticastSocket socket;
    private Thread thread;


    public OutgoingPacketHandler(MulticastSocket socket){
        this.socket = socket;
        this.thread = new Thread(this);
        this.thread.start();
    }

    @Override
    public void run() {
        while (true){
            synchronized (floatingPacketMap) {
                for (FloatingPacket packet : floatingPacketMap.values()) {
                    if (packet.getSentOn() + Protocol.TIMEOUT < System.currentTimeMillis()) {
                        try {
                            //TODO: Give group and port to datagram in the line below:
                            socket.send(new DatagramPacket(packet.toBytes(), packet.toBytes().length));
                            packet.setSentOn(System.currentTimeMillis());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public Thread getThread(){
        return thread;
    }

    public void send(Packet packet, InetAddress group){
        synchronized (floatingPacketMap) {
            try {
                socket.send(new DatagramPacket(packet.toBytes(), packet.toBytes().length, group, Protocol.GROUP_PORT));
            } catch (IOException e) {
                e.printStackTrace();
            }
            floatingPacketMap.put(packet.getFloatingKey(), new FloatingPacket(packet.toBytes()));
        }
    }

}
