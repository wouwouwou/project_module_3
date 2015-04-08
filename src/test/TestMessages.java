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





        //Packet conversion test
        Packet packet1 = new Packet();

        packet1.setSequenceNumber(123456789);
        packet1.setData("Ik ben woeter en ik houd van BONEN!".getBytes());

        System.out.printf("Original packet has sequence number: %s\n", packet1.getSequenceNumber());
        System.out.printf("Original packet has data: %s\n", new String(packet1.getData()));

        //Cast to byte[]
        byte[] packet1Bytes = packet1.toBytes();

        //Construct packet2 from bytes[]
        Packet packet2;
        try {
            packet2 = new Packet(packet1Bytes);
            System.out.printf("Constructed packet has sequence number: %s\n", packet2.getSequenceNumber());
            System.out.printf("Constructed packet has data: %s\n", new String(packet2.getData()));
        } catch (Packet.InvalidPacketException e) {
            e.printStackTrace();
        }






        //Send packets - Disabled at the moment
        /**
        while(true) {
            for (int i = 0; i < 10000; i++) {
                message = String.format("Number: %s", i);
                packet = new Packet();
                packet.setData(message.getBytes());
                networkManager.send(packet);

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    **/
    }
}
