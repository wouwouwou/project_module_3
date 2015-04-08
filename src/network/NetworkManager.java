package network;

import network.packet.Packet;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Created by gerben on 7-4-15.
 */
public class NetworkManager {
    private MulticastSocket socket;
    private IncomingPacketHandler incomingPacketHandler;
    private OutgoingPacketHandler outgoingPacketHandler;
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

            //Get our client ID and set Protocol.CLIENT_ID
            Protocol.CLIENT_ID = this.getClientId();
            System.out.println("Init with client id: " + Protocol.CLIENT_ID);


            //Create and start the IncomingPacketHandler
            incomingPacketHandler = new IncomingPacketHandler(socket, 1000);

            //Create and start the OutgoingPacketHandler
            outgoingPacketHandler = new OutgoingPacketHandler(socket, this);


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


        outgoingPacketHandler.send(packet, group);

    }


    public IncomingPacketHandler getIncomingPacketHandler() {
        return incomingPacketHandler;
    }

    public InetAddress getGroup() {
        return group;
    }

    /**
     * Returns the client id, taken from the ip address
     * @return client id
     */
    public int getClientId(){
        InetAddress addr = null;
        try {
            Enumeration<InetAddress> addrs = NetworkInterface.getNetworkInterfaces().nextElement().getInetAddresses();

            while (addrs.hasMoreElements() && (addr == null || addr.getAddress()[0] != 192)){
                addr = addrs.nextElement();
            }

        } catch (SocketException e) {
            e.printStackTrace();
        }

        if(addr == null){
            return 0;
        }
        return addr.getAddress()[3];
    }
}
