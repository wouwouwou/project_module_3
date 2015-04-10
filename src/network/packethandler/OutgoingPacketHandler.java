package network.packethandler;

import exceptions.network.DestinationNotInTableException;
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
    private long lastPingSend = 0;

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

            // Broadcasts a ping
            if (System.currentTimeMillis() > getLastPingSend() + Protocol.PING_INTERVAL) {
                Packet pingPacket = networkManager.constructPing(networkManager.getClientName());
                send(pingPacket);
                System.out.println("broadcasting Ping: " + "\tname: " + networkManager.getClientName() + "\n" + pingPacket);
                // 'resets' the ping_timer
                lastPingSend = System.currentTimeMillis();
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
    public void send(Packet packet) {
        InetAddress group = networkManager.getGroup();
        if (packet.getDestination() == 0) {
            System.out.println(packet.getFlags());
            packet.addFlag(Protocol.Flags.BROADCAST);
            System.out.println(packet.getFlags());
            byte[] packetBytes = packet.toBytes();
            for (byte i = 1; i < 5; i++) {
                if (i != Protocol.CLIENT_ID) {
                    System.out.println("Sending to " + i);
                    packetBytes[3] = i;
                    try {
                    byte[] route = networkManager.getTableEntryByDestination(i);
                        packetBytes[11] = route[2];
                        try {
                            this.send(new Packet(packetBytes));
                            System.out.println("Sent");
                        } catch (InvalidPacketException e) {
                            e.printStackTrace();
                        }
                    } catch (DestinationNotInTableException e) {
                        //TODO
                    }
                }
            }
        } else {
            //TODO Synchronized might break because it is called from a synchronized block in run()
            synchronized (floatingPacketMap) {
                try {
                    if(floatingPacketMap.containsKey((packet.getFloatingKey()))){
                        floatingPacketMap.remove(packet.getFloatingKey());
                    }

                    byte[] route;
                    try {
                        //Try to find a route
                        route = networkManager.getTableEntryByDestination(packet.getDestination());
                        //If there is no known route, throw an Exception and schedule the packet for a retry.
                    } catch (DestinationNotInTableException e) {
                        floatingPacketMap.put(packet.getFloatingKey(), new FloatingPacket(packet.toBytes()));
                        throw new IOException(String.format("Destination %s unreachable.", packet.getDestination()));
                    }

                    packet.setNextHop(route[2]);
                    socket.send(new DatagramPacket(packet.toBytes(), packet.toBytes().length, group, Protocol.GROUP_PORT));

                    if (packet.getFlags() == Protocol.Flags.DATA) {
                            floatingPacketMap.put(packet.getFloatingKey(), new FloatingPacket(packet.toBytes()));
                    }

                } catch (IOException | InvalidPacketException e) {
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

    // Queries
    public long getLastPingSend() {
        return this.lastPingSend;
    }

}
