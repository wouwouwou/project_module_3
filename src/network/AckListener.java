package network;

import network.packet.Packet;

/**
 * Interface for an AckListener.
 * @author Gerben Meijer
 * @since 8-4-15
 */
public interface AckListener {

    /**
     * Is called when receiving an ACK
     * @param packet the packet that was ACKed.
     */
    void onAck(Packet packet);
}
