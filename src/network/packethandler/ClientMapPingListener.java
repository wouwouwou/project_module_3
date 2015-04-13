package network.packethandler;

import network.DataListener;
import network.NetworkManager;
import network.Protocol;
import network.packet.Packet;

/**
 * @author Gerben Meijer
 * @since 10-4-15.
 * Listens for pings and modifies/(resets) the networkManager.connectedClients Map
 */
public class ClientMapPingListener implements DataListener {


    // -----<=>-----< Fields >-----<=>----- \\
    NetworkManager networkManager;


    // -----<=>-----< Constructor(s) >-----<=>----- \\
    public ClientMapPingListener(NetworkManager networkManager){
        this.networkManager = networkManager;
    }


    // -----<=>-----< Methods >-----<=>----- \\
    @Override
    public void onReceive(Packet packet) {
        if (packet.getDataType() == Protocol.DataType.PING){
            networkManager.getConnectedClients().put(packet.getSource(), (byte) 0);
        }
    }
}
