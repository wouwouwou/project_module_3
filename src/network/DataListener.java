package network;

import network.packet.Packet;

/**
 * @author Gerben Meijer
 * @since 7-4-15
 * Interface for a DataListener
 */
public interface DataListener {

    /**
     * Is called when a packet has been received.
     * @param packet Packet which has been received
     */
    void onReceive(Packet packet);

}
