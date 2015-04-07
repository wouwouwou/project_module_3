package test;

import network.NetworkManager;
import network.Packet;

/**
 * Created by gerben on 7-4-15.
 */
public class TestMessages {

    public static void main(String[] args){
        // Initialize test objects

        NetworkManager networkManager = new NetworkManager();
        PrintingPacketListener l = new PrintingPacketListener();
        networkManager.getPacketHandler().addListener(l);
        Packet packet;
        String message;

        //Send packets

        for (int i = 0; i < 10; i++) {
            message = String.format("Number: %s", i);
            packet = new Packet(message.getBytes());
            networkManager.send(packet);
        }
    }
}
