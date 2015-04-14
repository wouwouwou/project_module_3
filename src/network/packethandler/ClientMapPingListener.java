package network.packethandler;

import network.DataListener;
import network.NetworkManager;
import network.Protocol;
import network.packet.Packet;

/**
 * @author Gerben Meijer
 * @since 10-4-15.
 * Listens for pings and modifies the networkManager.connectedClients Map
 */
public class ClientMapPingListener implements DataListener {


    // -----<=>-----< Fields >-----<=>----- \\
    private final NetworkManager networkManager;


    // -----<=>-----< Constructor(s) >-----<=>----- \\

    /**
     * Constructor for this class.
     * @param networkManager The NetworkManager which has to be assigned to the field.
     */
    public ClientMapPingListener(NetworkManager networkManager){
        this.networkManager = networkManager;
    }


    // -----<=>-----< Methods >-----<=>----- \\
    /**
     * Puts the source of the ping-packet in the list of connected clients.
     * @param packet Packet which has been received
     */
    @Override
    public void onReceive(Packet packet) {
        if (packet.getDataType() == Protocol.DataType.PING){
            networkManager.getConnectedClients().put(packet.getSource(), (byte) 0);
        }
    }
}
