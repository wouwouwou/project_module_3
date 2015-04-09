package test;

import network.packet.Packet;
import network.DataListener;

/**
 * @author Gerben Meijer
 * @since 7-4-15
 */
public class PrintingDataListener implements DataListener {

    @Override

    public void onReceive(Packet packet) {
        System.out.println(packet);
        System.out.println(new String(packet.getData()));
    }
}
