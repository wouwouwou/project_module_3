package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

/**
 * Created by gerben on 7-4-15.
 */
public class NetworkManager {
    private MulticastSocket socket;
    private PacketHandler packetHandler;
    private InetAddress group;

    public static void main(String[] args){
        NetworkManager networkManager = new NetworkManager();
    }

    public NetworkManager() {
        //Get the group address
        try {
            group = InetAddress.getByName(Protocol.GROUP_ADDRESS);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        //Create the Multicast socket
        try {
            socket = new MulticastSocket(Protocol.GROUP_PORT);

            //Join the multicast group
            socket.joinGroup(group);

            //Create and start the PacketHandler
            packetHandler = new PacketHandler(socket, 1000);


            /**
             DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(),
             group, 6789);
             s.send(hi);
             // get their responses!
             byte[] buf = new byte[1000];
             DatagramPacket recv = new DatagramPacket(buf, buf.length);
             for (int i = 0; i < 2; i++) {
             s.receive(recv);
             System.out.println(new String(recv.getData()));
             }

             // OK, I'm done talking - leave the group...
             s.leaveGroup(group);
             **/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(Packet packet){
        DatagramPacket p = new DatagramPacket(packet.getData(), packet.getData().length,
                group, Protocol.GROUP_PORT);
        try {
            socket.send(p);
        } catch (IOException e) {
            System.err.println("The packet could not be sent!");
            e.printStackTrace();
        }
    }


    public PacketHandler getPacketHandler() {
        return packetHandler;
    }
}
