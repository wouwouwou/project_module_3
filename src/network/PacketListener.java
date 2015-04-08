package network;

import network.packet.Packet;

/**
 * @author Gerben Meijer
 * Created on 7-4-15.
 */
public interface PacketListener {
    public void onReceive(Packet packet);
}
