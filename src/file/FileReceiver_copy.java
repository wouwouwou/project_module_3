/*
package file;

import gui.controller.MessageController;
import gui.controller.ProcessMessage;
import network.Protocol;
import network.packet.Packet;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

*/
/**
 * Handles the packets that come in to the queue.
 * @author Tristan de Boer
 * @since 8-4-15
 *//*

public class FileReceiver_copy {

    */
/**
     * The <code>messageController</code> to send the messages to.
     *//*

    private final MessageController messageController;
    private boolean onlyack = false;

    */
/**
     * An mapping of mapping of parts of a file.
     *//*

    Map<List, SortedMap<Integer, byte[]>> receivedMap = new HashMap<>();
    */
/**
     * Lock, otherwise multiple adaptions can be made to <code>receivedMap</code> at the same time.
     *//*

    Lock lock = new ReentrantLock();

    */
/**
     * New FileReceiver. <code>MessageController</code> is used to send messages to to view in GUI.
     * @param messageController
     *//*

    public FileReceiver_copy(MessageController messageController) {
        this.messageController = messageController;
    }

    */
/**
     * New Filereceiver. <code>MessageController</code> is used to send messages to to view in GUI. Calling this constructor will make it an fileAcker.
     * @param messageController The messageController messages need to be send to.
     * @param b If is set to true, make an fileAcker.
     *//*

    public FileReceiver_copy(MessageController messageController, boolean b) {
        this.messageController = messageController;
        this.onlyack = true;
    }


    */
/**
     * Receive and process a new packet.
     * @param packet The packet that was received.
     *//*

    public void onReceive(Packet packet) {
        lock.lock();
        byte[] data = packet.getData();
        packet = packet.deepCopy();
        if(packet.hasFlag(Protocol.Flags.BROADCAST)){
            packet.setDestination((byte) 0);
        }
        FileHandler fh = new FileHandler();
        List key = new ArrayList();

        key.add(fh.getFileNumber(data));
        key.add(packet.getSource());
            // ReceivedMap doesn't contain key, add the key.
            */
/*for(int i = 0; i < receivedMap.size(); i++){
                if(receivedMap.get(i) != null) {
                    for (Map.Entry<Integer, byte[]> entry : receivedMap.get(i).entrySet()) {
                        System.out.print("[" + entry.getKey() + ":" + entry.getValue().toString() + "]");
                    }
                    System.out.println();
                }
            }*//*


        // Check if receivedMap contains key, otherwise add the key and add a new entry to the model population list2
        if(!receivedMap.containsKey(key)){
            receivedMap.put(key, new TreeMap<Integer, byte[]>());

            // Show a message to the queue that a new file is being transmitted
            for(int i = 0; i < messageController.getClientModel().size(); i++){
                if(messageController.getClientModel().get(i).getId() == packet.getSource()){
                    // source == i

                    String message = "Incoming file. Received 0/"+fh.getTotalPackets(data);
                    ProcessMessage pm;
                    if(onlyack){
                        pm = new ProcessMessage(key, fh.getTotalPackets(data), message, messageController.getClientModel().get(i).getName(), new Date(), packet.getSource(), packet.getDestination());
                    }else {
                        pm = new ProcessMessage(key, fh.getTotalPackets(data), message, messageController.getClientModel().get(i).getName(), new Date(), packet.getDestination(), packet.getSource());
                    }
                    messageController.addProcessMessage(pm);
                    messageController.addChatMessage(pm);
                }
            }
        }

        // Add the sequence number and data to the map. After it has been added, update list2 with information that a new part has been received.
        if(!receivedMap.get(key).containsKey(fh.getSequenceNumber(data))){
            System.err.println("Files@!");
            receivedMap.get(key).put(fh.getSequenceNumber(data), data);

            for(int j = 0; j < messageController.getClientModel().size(); j++){
                if(messageController.getChatModel().get(j) != null) {
                    for (int k = 0; k < messageController.getChatModel().get(j).size(); k++) {
                        if (messageController.getChatModel().get(j).get(k) instanceof ProcessMessage) {
                            // Check if we need to update this processmessage.
                            List testKey = ((ProcessMessage)messageController.getChatModel().get(j).get(k)).getFileId();
                            if(testKey == key){
                                messageController.getChatModel().get(j).get(k).setMessage("Incoming file. Received " + receivedMap.get(key).size() + "/" + fh.getTotalPackets(data));
                                messageController.updateList2();
                            }
                        }
                    }
                }
            }
            //this.updateMessage(key, "Incoming file. Received " + receivedMap.get(key).size() + "/" + fh.getTotalPackets(data));
        }
        // Check completeness
        // Complete: call FileHandler (save to file) and flush data from map.
        if(receivedMap.get(key).size() == fh.getTotalPackets(data) && !onlyack) {
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
            // Delete references to clear memory of JVM.
            receivedMap.remove(key);
            this.updateMessage(key, "Transfer has completed. Saved to " + filename);
            // Display message the file transfer has completed.
        }else if(receivedMap.get(key).size() == fh.getTotalPackets(data) && onlyack){
            // Display message the file transfer has completed.
            this.updateMessage(key, "Transfer has completed.");
        }
        lock.unlock();
    }

    */
/**
     * Update the message of an entry in a DefaultListModel in an entry of chatModel.
     * @param key The unique identifiers for the file that is linked to the chat entry.
     * @param message The message that needs to be set.
     *//*

    private void updateMessage(List key, String message){
        for(int j = 0; j < messageController.getClientModel().size(); j++){
            if(messageController.getChatModel().get(j) != null) {
                for (int k = 0; k < messageController.getChatModel().get(j).size(); k++) {
                    if (messageController.getChatModel().get(j).get(k) instanceof ProcessMessage) {
                        // Check if we need to update this processmessage.
                        List testKey = ((ProcessMessage)messageController.getChatModel().get(j).get(k)).getFileId();
                        if(testKey == key){
                            messageController.setMessage(j, k, message);
                            //messageController.getChatModel().get(j).get(k).setMessage(message);
                            messageController.updateList2();
                        }
                    }
                }
            }
        }
    }
    */
/**
     * Convert four bytes to an integer of 32 bits.
     * @param b The bytes that need to be converted
     * @return
     *//*

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
*/