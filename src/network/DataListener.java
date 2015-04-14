package network;

import network.packet.Packet;

/**
 * Interface for a DataListener
 * @author Gerben Meijer
 * @since 7-4-15
 */
public interface DataListener {

    /**
     * Is called when a packet has been received.
     * @param packet Packet which has been received
     */
    void onReceive(Packet packet);

}
