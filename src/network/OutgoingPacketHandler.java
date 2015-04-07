package network;

import java.net.MulticastSocket;
import java.util.HashMap;

/**
 * Created by gerben on 7-4-15.
 */
public class OutgoingPacketHandler implements Runnable{

    private HashMap<byte[], FloatingPacket> floatingPacketMap;
    private MulticastSocket socket;

    @Override
    public void run() {

    }

    public OutgoingPacketHandler(MulticastSocket socket){
        this.socket = socket;
    }

    public void send(Packet packet){

    }

}
