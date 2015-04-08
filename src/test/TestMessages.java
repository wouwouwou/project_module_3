package test;

import network.NetworkManager;
import network.packet.Packet;

/**
 * Tests the multicast adhoc network, sending 10 numbers to all subscribers
 * Created by gerben on 7-4-15.
 */
public class TestMessages {

    public static void main(String[] args){
        // Initialize test objects

        NetworkManager networkManager = new NetworkManager();
        PrintingPacketListener l = new PrintingPacketListener();
        networkManager.getIncomingPacketHandler().addListener(l);
        Packet packet;
        String message;

        //Send packets

        while(true) {
            for (int i = 0; i < 10000; i++) {
                message = String.format("Number: %s", i);
                packet = new Packet(message.getBytes());
                networkManager.send(packet);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
