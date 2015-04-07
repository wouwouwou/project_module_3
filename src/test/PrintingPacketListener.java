package test;

import network.Packet;
import network.PacketListener;

/**
 * Created by gerben on 7-4-15.
 */
public class PrintingPacketListener implements PacketListener{

    @Override
    public void onRecieve(Packet packet) {
        System.out.println(new String(packet.getData()));
    }
}
