package network.packethandler;

import exceptions.network.InvalidPacketException;
import network.NetworkManager;
import network.Protocol;
import network.packet.FloatingPacket;
import network.packet.Packet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Gerben Meijer
 * @since 7-4-15
 */
public class OutgoingPacketHandler extends PacketHandler {

    // Fields
    private final ConcurrentHashMap<List<Byte>, FloatingPacket> floatingPacketMap = new ConcurrentHashMap<>();
    private NetworkManager networkManager;

    // Constructor(s)
    public OutgoingPacketHandler(NetworkManager networkManager){
        super(networkManager);
        this.networkManager = networkManager;
    }

    // Methods
    @Override
    public void run() {
        while (true) {
            if (System.currentTimeMillis() > networkManager.getLastTableDrop() + Protocol.CONVERGE_TIME) {
                synchronized (floatingPacketMap) {
                    for (FloatingPacket packet : floatingPacketMap.values()) {
                        if (packet.getSentOn() + Protocol.TIMEOUT < System.currentTimeMillis()) {
                            this.send(packet);
                            packet.setSentOn(System.currentTimeMillis());
                        }
                    }
                }
            }

            if(System.currentTimeMillis() > networkManager.getLastTableDrop() + 10000){
                networkManager.dropTable();
                networkManager.setDiscoverySequenceNum((short) (networkManager.getDiscoverySequenceNum() + 1));
                networkManager.sendTable();
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }

    }

    /**
     * Broadcasts a packet over a Multicast group network
     * <p>
     *     Broadcasted packets will be added to a tentative list
     *     As long as a tentative packet has not been received, it will be retransmitted
     * </p>
     * @param packet Packet the packet that will be broadcasted to the multicast network
     */
    public void send(Packet packet){
        InetAddress group = networkManager.getGroup();
        synchronized (floatingPacketMap) {
            try {
                socket.send(new DatagramPacket(packet.toBytes(), packet.toBytes().length, group, Protocol.GROUP_PORT));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(packet.getFlags() == Protocol.Flags.DATA) {
                try {
                    floatingPacketMap.put(packet.getFloatingKey(), new FloatingPacket(packet.toBytes()));
                } catch (InvalidPacketException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Packet handleACK(Packet ackPacket){
        System.out.println("Ack received!");
        System.out.println(floatingPacketMap.containsKey(ackPacket.getFloatingKey()));
        if(floatingPacketMap.containsKey(ackPacket.getFloatingKey())){
            Packet original = floatingPacketMap.get(ackPacket.getFloatingKey());
            floatingPacketMap.remove(ackPacket.getFloatingKey());
            return original;
        }
        return null;
    }


}
