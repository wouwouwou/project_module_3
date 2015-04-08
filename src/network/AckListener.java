package network;

import network.packet.Packet;

/**
 * Created by gerben on 8-4-15.
 */
public interface AckListener {

    /**
     * Is called when receiving an ACK
     * @param packet the packet that was ACKed.
     */
    public void onAck(Packet packet);
}
