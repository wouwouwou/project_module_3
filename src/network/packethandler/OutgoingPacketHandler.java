package network.packethandler;

import exceptions.network.InvalidPacketException;
import network.NetworkManager;
import network.Protocol;
import network.packet.FloatingPacket;
import network.packet.Packet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class for handling with outgoing packets.
 * @author Gerben Meijer
 * @since 7-4-15
 */
public class OutgoingPacketHandler extends PacketHandler {


    // -----<=>-----< Fields >-----<=>----- \\
    private final ConcurrentHashMap<List<Byte>, FloatingPacket> floatingPacketMap = new ConcurrentHashMap<>();
    private final NetworkManager networkManager;
    private long lastPingSend = 0;
    private final ArrayList<Packet> filePacketBuffer = new ArrayList<>();

    
    // -----<=>-----< Constructor(s) >-----<=>----- \\
    /**
     * Constructs a new OutgoingPacketHandler.
     * The OutgoingPacketHandler is then started in a new Thread.
     * @param networkManager The related NetworkManager.
     */
    public OutgoingPacketHandler(NetworkManager networkManager){
        super(networkManager);
        this.networkManager = networkManager;
    }


    // -----<=>-----< Queries & Methods >-----<=>----- \\
    /**
     * A large method. It sends packets and also keeps track of packets
     * which have to be resend.
     */
    @Override
    public void run() {
        while (true) {
            if (System.currentTimeMillis() > networkManager.getLastTableDrop() + Protocol.CONVERGE_TIME) {
                synchronized (floatingPacketMap) {
                    while(floatingPacketMap.size() < Protocol.FILE_SEND_BUFFER_SIZE && filePacketBuffer.size() > 0){
                        synchronized (filePacketBuffer) {
                            send(filePacketBuffer.get(0));
                            filePacketBuffer.remove(0);
                        }
                    }

                    for (FloatingPacket packet : floatingPacketMap.values()) {

                        //If there are packets to be re-sent, do so. The timeout is exponential, this way slow networks can also keep up.
                        if (packet.getSentOn() + Protocol.TIMEOUT * Math.pow(2, Math.max(Protocol.MAX_RETRIES - packet.getRetries(), 3)) < System.currentTimeMillis()) {
                            this.send(packet);
                            packet.setSentOn(System.currentTimeMillis());
                        }
                    }
                }
            }

            if(System.currentTimeMillis() > networkManager.getLastTableDrop() + Protocol.TABLE_DROP_INTERVAL){
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
                networkManager.sendTable();
                Packet pingPacket = networkManager.constructPing();
                send(pingPacket);
                //System.out.println("broadcasting Ping: " + "\tname: " + networkManager.getClientName() + "\n" + pingPacket);
                // 'resets' the ping_timer
                lastPingSend = System.currentTimeMillis();
                networkManager.increasePingRound();
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
            packet.addFlag(Protocol.Flags.BROADCAST);
            byte[] packetBytes = packet.toBytes();
            for (Byte i: networkManager.getConnectedClients().keySet()) {
                if (i != Protocol.CLIENT_ID) {
                    packetBytes[3] = i;

                    try {
                        this.send(new Packet(packetBytes));
                    } catch (InvalidPacketException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            synchronized (floatingPacketMap) {
                try {
                    if (!(packet instanceof FloatingPacket) && packet.getDataType() == Protocol.DataType.FILE && !packet.hasFlag(Protocol.Flags.ACK) && packet.getSource() == Protocol.CLIENT_ID && floatingPacketMap.size() > Protocol.FILE_SEND_BUFFER_SIZE){
                        synchronized (filePacketBuffer) {
                            //ADD the file to a buffer.
                            filePacketBuffer.add(packet);
                        }
                    } else {

                        if (floatingPacketMap.containsKey((packet.getFloatingKey()))) {
                            floatingPacketMap.remove(packet.getFloatingKey());
                        }


                        //Try to find a route
                        byte[] route;
                        route = networkManager.getTableEntryByDestination(packet.getDestination());

                        if (route == null) {
                            //If there is no known route, throw an Exception and schedule the packet for a retry.
                            if (packet.getSource() == Protocol.CLIENT_ID) {
                                scheduleForResend(packet);
                            }
                            throw new IOException(String.format("Destination %s unreachable.", packet.getDestination()));
                        }

                        packet.setNextHop(route[2]);
                        socket.send(new DatagramPacket(packet.toBytes(), packet.toBytes().length, group, Protocol.GROUP_PORT));

                        if (packet.getFlags() == Protocol.Flags.DATA && packet.getSource() == Protocol.CLIENT_ID) {
                            scheduleForResend(packet);
                        }

                    }
                } catch (IOException e) {
                    //This does not need further handling, packet already scheduled for resend.
                }

            }
        }
    }

    /**
     * Handles an Acknowledgement packet
     * <p>
     *     Finds the floating packet that has been acknowledged and removes it from the map.
     *     Then the packet does not get resend again, and again, and again, and again....
     * </p>
     * @param ackPacket The packet to be removed
     * @return The original packet linked to this ACK.
     */
    public Packet handleACK(Packet ackPacket){
        if(floatingPacketMap.containsKey(ackPacket.getFloatingKey())){
            Packet original = floatingPacketMap.get(ackPacket.getFloatingKey());
            floatingPacketMap.remove(ackPacket.getFloatingKey());
            return original;
        }
        return null;
    }

    /**
     * Gets the time when the last ping packet has been send as a long and in millis.
     * @return Time of the last ping.
     */
    public long getLastPingSend() {
        return this.lastPingSend;
    }

    /**
     * Schedules a packet for a resent
     * <p>
     *     Makes a new floating packet if necessary. Adding it to the floatingPacketMap if there are {@link FloatingPacket#retries resends left} for the packet
     * </p>
     * @param packet The packet that must be resend.
     * @see FloatingPacket
     */
    private void scheduleForResend(Packet packet) {
        boolean resend = true;
        FloatingPacket floatingPacket = null;

        if( !(packet instanceof FloatingPacket)) {

            try {
                floatingPacket = new FloatingPacket(packet.toBytes());
            } catch (InvalidPacketException e) {
                //Made a InvalidPacket from a valid packet
                e.printStackTrace();
            }

        } else {
            floatingPacket = (FloatingPacket) packet;
            resend = !((FloatingPacket) packet).decreaseRetries();
        }
        if (resend) {
            floatingPacketMap.put(packet.getFloatingKey(), floatingPacket);
        }

    }

}
