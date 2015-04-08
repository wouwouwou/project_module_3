package test;

import network.NetworkManager;
import network.Protocol;
import network.packet.Packet;

import java.io.IOException;

/**
 * Tests the multicast adhoc network, sending 10 numbers to all subscribers
 * Created by gerben on 7-4-15.
 */
public class TestMessages {

    public static void main(String[] args){
        // Initialize test objects

        NetworkManager networkManager = new NetworkManager();
        PrintingDataListener l = new PrintingDataListener();
        networkManager.getIncomingPacketHandler().addDataListener(l);
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

        packet2 = new Packet(packet1Bytes);
        System.out.printf("Constructed packet has sequence number: %s\n", packet2.getSequenceNumber());
        System.out.printf("Constructed packet has data: %s\n", new String(packet2.getData()));



        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Send packets

        try {
            Packet woeterPacket = networkManager.constructPacket((byte) 1, Protocol.DataType.TEXT, "Hoi Woeter!".getBytes());
            networkManager.send(woeterPacket);
        } catch (IOException e) {
            System.err.println("WoeterIsErNietException");
        }

        try {
            Packet tristanPacket = networkManager.constructPacket((byte) 2, Protocol.DataType.TEXT, "Hoi Tristan!".getBytes());
            networkManager.send(tristanPacket);
        } catch (IOException e) {
            System.err.println("TristanIsErNietException");
        }

        try {
            Packet gerbenPacket = networkManager.constructPacket((byte) 3, Protocol.DataType.TEXT, "Hoi Gerben!".getBytes());
            System.out.println(gerbenPacket);
            networkManager.send(gerbenPacket);
        } catch (IOException e) {
            System.err.println("GerbenIsErNietException");
        }

        try {
            Packet timPacket = networkManager.constructPacket((byte) 4, Protocol.DataType.TEXT, "Hoi Tim!".getBytes());
            networkManager.send(timPacket);
        } catch (IOException e) {
            System.err.println("TimIsErNietException");
        }




    }
}
