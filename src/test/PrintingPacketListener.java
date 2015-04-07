package test;

import network.packet.Packet;
import network.PacketListener;

/**
 * Created by gerben on 7-4-15.
 */
public class PrintingPacketListener implements PacketListener{

    @Override
    public void onReceive(Packet packet) {
        System.out.println(new String(packet.getData()));
    }
}
