package network.packethandler;

import network.NetworkManager;
import network.Protocol;
import network.packet.FloatingPacket;
import network.packet.Packet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Gerben Meijer
 * @since 7-4-15
 */
public class OutgoingPacketHandler extends PacketHandler {

    private final ConcurrentHashMap<byte[], FloatingPacket> floatingPacketMap = new ConcurrentHashMap<>();
    private NetworkManager networkManager;


    public OutgoingPacketHandler(MulticastSocket socket, NetworkManager networkManager){
        super(socket);
        this.networkManager = networkManager;
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
