package file;

import gui.controller.ChatMessage;
import gui.controller.Client;
import gui.controller.MessageController;
import gui.controller.ProcessMessage;
import network.packet.Packet;

import javax.swing.*;
import java.util.*;

/**
 * @author Tristan de Boer
 * @since 8-4-15
 */
public class FileReceiver {

    private final MessageController messageController;
    Map<Integer, SortedMap<Integer, byte[]>> receivedMap = new HashMap<>();

    public FileReceiver(MessageController messageController) {
        this.messageController = messageController;
    }


    public void onReceive(Packet packet) {
        byte[] data = packet.getData();
        synchronized (receivedMap) {
            FileHandler fh = new FileHandler();
            // Add data to receivedMap
            // If map doesn't contain filenumber, add a new entry.
            if(!receivedMap.containsKey(fh.getFileNumber(data))){
                receivedMap.put(fh.getFileNumber(data), new TreeMap<Integer, byte[]>());
                DefaultListModel<Client> clientModel = messageController.getClientModel();
                // Show a message to the queue that a new file is being transmitted.
                for(int i = 0; i < clientModel.size(); i++) {
                    if((i == 0 && packet.getDestination() == 0) || (clientModel.get(i).getId() == packet.getSource())) {
                        System.out.println("Adding in queue" + i);
                        ProcessMessage pm = new ProcessMessage(fh.getFileNumber(data), fh.getTotalPackets(data), "Incoming file. Received 0/"+fh.getTotalPackets(data), clientModel.get(i).getName(), new Date(), packet.getDestination(), packet.getSource());
                        messageController.addProcessMessage(pm);
                        messageController.addChatMessage(pm);
                        break;
                    }
                }

            }
            // If entry filenumber doesn't contain a packet number, add a new entry with packet data.
            if(!receivedMap.get(fh.getFileNumber(data)).containsKey(fh.getSequenceNumber(data))){
                HashMap<Integer,DefaultListModel<ChatMessage>> chatMessages = messageController.getChatModel();
                receivedMap.get(fh.getFileNumber(data)).put(fh.getSequenceNumber(data), data);

                for(int i = 0; i < messageController.getClientModel().size(); i++){
                    System.out.println("QWERTY");
                    if(messageController.getChatModel().get(i) != null){
                        System.out.println("UIOP");
                        for(int k = 0; k < messageController.getChatModel().size(); k++){
                            if(messageController.getChatModel().get(i).get(k).getId() == fh.getFileNumber(data) && messageController.getChatModel().get(i).get(k).getSource() == packet.getSource()){
                                System.out.println("Gotcha!");
                                messageController.setMessage(i, k, "Incoming file. Received " + receivedMap.get(fh.getFileNumber(data)).size() + "/" + fh.getTotalPackets(data));
                            }
                        }
                    }
                }
                //System.out.println("adding to map!" + receivedMap.get(fh.getFileNumber(data)).size() + "/" + fh.getTotalPackets(data) + "of file" + fh.getFileNumber(data));
            }

            // Check completeness
            // Complete: call FileHandler (save to file) and flush data from map.
            if(receivedMap.get(fh.getFileNumber(data)).size() == fh.getTotalPackets(data)){
                List<byte[]> list = new ArrayList<byte[]>(receivedMap.get(fh.getFileNumber(data)).values());
                // Remove the headers
                List<byte[]> listbytearrayR = fh.removeHeaders(list);
                int DRlength = 0;
                for(byte[] DRtocount: listbytearrayR){
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
    }

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
