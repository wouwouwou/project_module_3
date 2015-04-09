package test;

import network.NetworkManager;
import network.Protocol;
import network.packet.Packet;

import java.io.IOException;

/**
 * Tests the multicast adhoc network, sending 10 numbers to all subscribers
 * @author Gerben Meijer
 * @since 7-4-15
 */
public class TestMessages {

    public static void main(String[] args){
        // Initialize test objects

        NetworkManager networkManager = new NetworkManager();
        PrintingDataListener l = new PrintingDataListener();
        PrintingAckListener pal = new PrintingAckListener();
        networkManager.getIncomingPacketHandler().addDataListener(l);
        networkManager.getIncomingPacketHandler().addAckListener(pal);


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
