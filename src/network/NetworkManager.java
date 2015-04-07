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

    public static void main(String[] args){
        NetworkManager networkManager = new NetworkManager();
    }

    public NetworkManager(){
        // join a Multicast group and send the group salutations
        String msg = "Hello2";
        InetAddress group = null;
        try {
            group = InetAddress.getByName("228.5.6.7");
            //group = InetAddress.getByName("255.255.255.255");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        MulticastSocket s = null;
        try {
            s = new MulticastSocket(6789);
            s.joinGroup(group);
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
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
