package network;

import network.packet.FloatingPacket;
import network.packet.Packet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by gerben on 7-4-15.
 */
public class OutgoingPacketHandler implements Runnable{

    private final ConcurrentHashMap<byte[], FloatingPacket> floatingPacketMap = new ConcurrentHashMap<byte[], FloatingPacket>();
    private MulticastSocket socket;
    private NetworkManager networkManager;
    private Thread thread;


    public OutgoingPacketHandler(MulticastSocket socket, NetworkManager networkManager){
        this.networkManager = networkManager;
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
                        this.send(packet, networkManager.getGroup());
                        packet.setSentOn(System.currentTimeMillis());
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
            try {
                floatingPacketMap.put(packet.getFloatingKey(), new FloatingPacket(packet.toBytes()));
            } catch (Packet.InvalidPacketException e) {
                e.printStackTrace();
            }
        }
    }

}
