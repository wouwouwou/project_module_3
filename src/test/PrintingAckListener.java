package test;

import exceptions.network.InvalidPacketException;
import network.AckListener;
import network.packet.Packet;

/**
 * Created by gerben on 8-4-15.
 */
public class PrintingAckListener implements AckListener {
    @Override
    public void onAck(Packet packet) {
        System.out.printf("Ack: %s\n", packet.getSequenceNumber());
    }
}
