package file;

import gui.controller.ChatMessage;
import gui.controller.Client;
import gui.controller.MessageController;
import gui.controller.ProcessMessage;
import network.Protocol;
import network.packet.Packet;

import javax.swing.*;
import java.util.*;

/**
 * @author Tristan de Boer
 * @since 8-4-15
 */
public class FileReceiver {

    private final MessageController messageController;
    Map<Object[], SortedMap<Integer, byte[]>> receivedMap = new HashMap<>();

    public FileReceiver(MessageController messageController) {
        this.messageController = messageController;
    }


    public void onReceive(Packet packet) {
        byte[] data = packet.getData();
        packet = packet.clone();
        if(packet.hasFlag(Protocol.Flags.BROADCAST)){
            packet.setDestination((byte) 0);
        }
        synchronized (receivedMap) {
            FileHandler fh = new FileHandler();
            // Add data to receivedMap
            // If map doesn't contain filenumber, add a new entry.
            Object[] put = new Object[2];
            put[0] = fh.getFileNumber(data);
            put[1] = packet.getSource();
            if(!receivedMap.containsKey(put)){
                receivedMap.put(put, new TreeMap<Integer, byte[]>());
                DefaultListModel<Client> clientModel = messageController.getClientModel();
                ProcessMessage pm = null;
                // Show a message to the queue that a new file is being transmitted.
                for(int i = 0; i < clientModel.size(); i++){
                    if((clientModel.get(i).getId() == put[1])){
                        System.err.println("Adding in queue" + i);
                        pm = new ProcessMessage(put, fh.getTotalPackets(data), "Incoming file. Received 0/"+fh.getTotalPackets(data), clientModel.get(i).getName(), new Date(), packet.getDestination(), packet.getSource());
                        break;
                    }
                }
                messageController.addProcessMessage(pm);
                messageController.addChatMessage(pm);
            }
            // If entry filenumber doesn't contain a packet number, add a new entry with packet data.
            if(!receivedMap.get(put).containsKey(fh.getSequenceNumber(data))){
                HashMap<Integer,DefaultListModel<ChatMessage>> chatMessages = messageController.getChatModel();
                receivedMap.get(put).put(fh.getSequenceNumber(data), data);
                for(int j = 0; j < chatMessages.size(); j++){
                    DefaultListModel<ChatMessage> chatModel = chatMessages.get(j);
                    if(chatModel != null) {
                        for (int i = 0; i < chatModel.size(); i++) {
                            if (chatModel.get(i) instanceof ProcessMessage) {
                                if (((ProcessMessage) chatModel.get(i)).getFileId() == put) {
                                    // Update this packet :)

                                    chatModel.get(i).setMessage("Incoming file. Received " + receivedMap.get(put).size() + "/" + fh.getTotalPackets(data));
                                    messageController.updateList2();
                                }
                            }
                        }
                    }
                }

                //System.out.println("adding to map!" + receivedMap.get(filenumber).size() + "/" + fh.getTotalPackets(data) + "of file" + filenumber);
            }

            // Check completeness
            // Complete: call FileHandler (save to file) and flush data from map.
            if(receivedMap.get(put).size() == fh.getTotalPackets(data)){
                List<byte[]> list = new ArrayList<byte[]>(receivedMap.get(put).values());
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
