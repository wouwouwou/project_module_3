package network;

import network.packet.Packet;

/**
 * @author Gerben Meijer
 * @since 7-4-15
 */
public interface PacketListener {
    void onReceive(Packet packet);
}
