package file;

import gui.controller.MessageController;
import gui.controller.ProcessMessage;
import network.Protocol;
import network.packet.Packet;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Tristan de Boer
 * @since 8-4-15
 * Handles the packets that come in to the queue.
 */
public class FileReceiver {


    // -----<=>-----< Fields >-----<=>----- \\
    private final MessageController messageController;
    private final boolean isAck;
    Map<List, SortedMap<Integer, byte[]>> receivedMap = new HashMap<>();
    Lock lock = new ReentrantLock();


    // -----<=>-----< Constructor(s) >-----<=>----- \\
    public FileReceiver(MessageController messageController, boolean isAck) {
        this.messageController = messageController;
        this.isAck = isAck;
    }

    
    // -----<=>-----< Methods >-----<=>----- \\
    public void onReceive(Packet packet) {
        lock.lock();
        byte[] data = packet.getData();
        packet = packet.deepCopy();
        if(packet.hasFlag(Protocol.Flags.BROADCAST)){
            packet.setDestination((byte) 0);
        }
            FileHandler fh = new FileHandler();
            // Add data to receivedMap
            // If map doesn't contain filenumber, add a new entry.
            List key = new ArrayList();
            key.add(fh.getFileNumber(data));
        if(isAck) {
            key.add(packet.getDestination());
        }else{
            key.add(packet.getSource());
        }

            // ReceivedMap doesn't contain key, add the key.

            if(!receivedMap.containsKey(key)){
                receivedMap.put(key, new TreeMap<Integer, byte[]>());

                // Show a message to the queue that a new file is being transmitted
                for(int i = 0; i < messageController.getClientModel().size(); i++){
                    if((!isAck && messageController.getClientModel().get(i).getId() == packet.getSource()) || (isAck && messageController.getClientModel().get(i).getId() == packet.getDestination())){
                        // source == i
                        String message = "";
                        if(isAck) {
                            message = "Outgoing file. Sent 0/" + fh.getTotalPackets(data);
                        }else{
                            message = "Incoming file. Sent 0/" + fh.getTotalPackets(data);
                        }
                        ProcessMessage pm = new ProcessMessage(key, fh.getTotalPackets(data), message, messageController.getClientModel().get(i).getName(), new Date(), packet.getDestination(), packet.getSource());
                        messageController.addProcessMessage(pm);
                        messageController.addChatMessage(pm);
                    }
                }
            }

            if(!receivedMap.get(key).containsKey(fh.getSequenceNumber(data))) {
                receivedMap.get(key).put(fh.getSequenceNumber(data), data);


                for(Integer keySet: messageController.getChatModel().keySet()){
                    for(int k = 0; k < messageController.getChatModel().get(keySet).size(); k++){
                        if(messageController.getChatModel().get(keySet).get(k) instanceof ProcessMessage && ((ProcessMessage) messageController.getChatModel().get(keySet).get(k)).getFileId().get(0)== key.get(0) && ((ProcessMessage) messageController.getChatModel().get(keySet).get(k)).getFileId().get(1) == key.get(1)){
                            if(isAck){
                                messageController.setMessage(keySet, k, "Outgoing file. Received " + receivedMap.get(key).size() + "/" + fh.getTotalPackets(data));
                            }else{
                                messageController.setMessage(keySet, k, "Incoming file. Send " + receivedMap.get(key).size() + "/" + fh.getTotalPackets(data));
                            }
                        }
                    }
                }
            }
        if(!isAck) {
            // Check completeness
            // Complete: call FileHandler (save to file) and flush data from map.
            if (receivedMap.get(key).size() == fh.getTotalPackets(data)) {
                System.err.println("Plakken!");
                List<byte[]> list = new ArrayList<byte[]>(receivedMap.get(key).values());
                // Remove the headers
                List<byte[]> listbytearrayR = fh.removeHeaders(list);
                int DRlength = 0;
                for (byte[] DRtocount : listbytearrayR) {
                    DRlength += DRtocount.length;
                }
                //System.out.println("DR size: " + DRlength);
                // Get filename and remove from list
                String filename = new String(listbytearrayR.get(listbytearrayR.size() - 1));
                listbytearrayR.remove(listbytearrayR.size() - 1);
                // Convert to single byte array
                byte[] bytearrayR = fh.convergeToArray(listbytearrayR);
                //System.out.println("ER size: " + bytearrayR.length);
                //System.out.println("Flushing to disk!");

                fh.writeFile(bytearrayR, filename);
            }
        }
        lock.unlock();

    }


    // -----<=>-----< Static Method >-----<=>----- \\
    public static int byteArrayToInt(byte[] b)
    {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (b[i] & 0x000000FF) << shift;
        }
        return value;
    }
}
