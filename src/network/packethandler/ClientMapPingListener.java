package network.packethandler;

import network.DataListener;
import network.NetworkManager;
import network.Protocol;
import network.packet.Packet;

/**
 * Created by gerben on 10-4-15.
 * Listens for pings and modifies the networkManager.connectedClients Map
 */
public class ClientMapPingListener implements DataListener {
    NetworkManager networkManager;

    public ClientMapPingListener(NetworkManager networkManager){
        this.networkManager = networkManager;
    }

    @Override
    public void onReceive(Packet packet) {
        if (packet.getDataType() == Protocol.DataType.PING){
            networkManager.getConnectedClients().put(packet.getSource(), (byte) 0);
        }
    }
}
