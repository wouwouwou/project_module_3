package file;

import gui.SoundPlayer;
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
    /**
     * The controller used to control the GUI and messagerelated stuff.
     */
    private final MessageController messageController;
    /**
     * If <code>isAck</code> is true, this class is only used to check the progress of a file transfer, not to create a file out of data packets.
     */
    private final boolean isAck;
    /**
     * A map with received data.
     */
    Map<List, SortedMap<Integer, byte[]>> receivedMap = new HashMap<>();
    /**
     * Lock, used to lock the method <code>onReceive</code>.
     */
    Lock lock = new ReentrantLock();


    // -----<=>-----< Constructor(s) >-----<=>----- \\

    /**
     * New FileReceiver class
     * @param messageController The controller that controls messages.
     * @param isAck If is true, this class is only used to check the progress of a file transfer (used with ACK's).
     */
    public FileReceiver(MessageController messageController, boolean isAck) {
        this.messageController = messageController;
        this.isAck = isAck;
    }

    
    // -----<=>-----< Methods >-----<=>----- \\

    /**
     * Called from <code>MessageController</code>.
     * <p>
     *     onReceive uses a <code>ReentrantLock</code>, so that no concurrent actions can be made to <code>receivedMap</code>.
     *     First, it creates a 'key', used to identify the file that is being sent. This key consists out of the Sender and an (for that sender)
     *     unique file number that increments every time a file is sent.
     *
     *     Using the key we check that the receivedMap doesn't contain this key. If receivedMap does not contain this key, the key is added.
     *     A message to the queue in the GUI is also added, stating file transfer has started.
     *
     *     Every time a new packet comes in (when the key is added to receivedMap), it will be added to the specific entry of receivedMap.
     *     If the total count of entries in that entry of receivedMap is equal to the total advertised amount of packets, the file transfer is complete.
     *
     *     If <code>isAck != true</code>, a new file will be made. First, all headers of the data packets will be removed. After that, the last packet,
     *     containing the file name, will be removed from the list.
     *     All bytes arrays that are left will be added together as a new (giant!) bytearray. This bytearray will be written
     *     to a file, with the filename that was removed from the list.
     *
     *     A sound will be played if the file transfer is complete (and sound is enabled!).
     *
     *     After all of this, the lock will be unlocked.
     *
     *
     * </p>
     * @param packet The packet that needs to be added to the list.
     * @see https://docs.google.com/spreadsheets/d/1txMKaJt0YtHc6zTXJE2hnVJPlrHriVockRcA48qDHl0/edit#gid=0 for further information about the protocol used for file transfer.
     */
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
                            if(receivedMap.get(key).size() == fh.getTotalPackets(data)){
                                messageController.setMessage(keySet, k, "Transfer complete!");
                            }else{
                                messageController.setMessage(keySet, k, "Outgoing file. Received " + receivedMap.get(key).size() + "/" + fh.getTotalPackets(data) + ". (" + (Math.round((receivedMap.get(key).size() / fh.getTotalPackets(data))*100) + "%)"));
                            }
                        }else{
                            if(receivedMap.get(key).size() == fh.getTotalPackets(data)){
                                messageController.setMessage(keySet, k, "Transfer complete!");
                                if(messageController.getSoundEnabled()) {
                                    SoundPlayer.playSound(false);
                                }
                            }else{
                                messageController.setMessage(keySet, k, "Incoming file. Sent " + receivedMap.get(key).size() + "/" + fh.getTotalPackets(data) + ". (" + (Math.round((receivedMap.get(key).size() / fh.getTotalPackets(data))*100) + "%)"));
                            }
                        }
                    }
                }
            }
        }
        if(!isAck) {
            // Check completeness
            // Complete: call FileHandler (save to file) and flush data from map.
            if (receivedMap.get(key).size() == fh.getTotalPackets(data)) {
                List<byte[]> list = new ArrayList<>(receivedMap.get(key).values());
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
}
