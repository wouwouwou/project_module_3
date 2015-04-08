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

    // -----<=>-----< Fields >-----<=>----- \\
    private final ConcurrentHashMap<byte[], FloatingPacket> floatingPacketMap = new ConcurrentHashMap<>();
    private NetworkManager networkManager;

    // -----<=>-----< Constructor(s) >-----<=>----- \\
    public OutgoingPacketHandler(MulticastSocket socket, NetworkManager networkManager){
        super(socket);
        this.networkManager = networkManager;
    }

    // -----<=>-----< Methods >-----<=>----- \\
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

    /**
     * Updates the floatingPacketMap
     * <p>
     *     Loops over the whole floatingPacketMap
     *     Checking if the @param packet has the same Sequence number
     *     If a match is found, the tentative is removed from the map and the method stops.
     * </p>
     * @param packet Packet an Acknowledgment that has to be checked
     * @return boolean if floatingPacketMap is updated
     */
    public boolean updateTentative(Packet packet) {
        synchronized (floatingPacketMap) {
            for (byte[] tentativeSeq : floatingPacketMap.keySet()) {
                if (packet.getSequenceBytes() == tentativeSeq) {
                    floatingPacketMap.remove(tentativeSeq);
                    return true;
                }
            }
        }
        return false;
    }

}
