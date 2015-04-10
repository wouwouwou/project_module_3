package file;

import network.packet.Packet;

import java.util.*;

/**
 * @author Tristan de Boer
 * @since 8-4-15
 */
public class FileReceiver {

    Map<Integer, SortedMap<Integer, byte[]>> receivedMap = new HashMap<>();
    /**
     *
     * @param packet
     */
    public void onReceive(Packet packet) {
        this.onReceive(packet.getData());
    }

    public void onReceive(byte[] data) {
        synchronized (receivedMap) {
            FileHandler fh = new FileHandler();
            // Add data to receivedMap
            // If map doesn't contain filenumber, add a new entry.
            if(!receivedMap.containsKey(fh.getFileNumber(data))){
                receivedMap.put(fh.getFileNumber(data), new TreeMap<Integer, byte[]>());
            }
            // If entry filenumber doesn't contain a packet number, add a new entry with packet data.
            if(!receivedMap.get(fh.getFileNumber(data)).containsKey(fh.getSequenceNumber(data))){

                receivedMap.get(fh.getFileNumber(data)).put(fh.getSequenceNumber(data), data);
                System.out.println("adding to map!" + receivedMap.get(fh.getFileNumber(data)).size() + "/" + fh.getTotalPackets(data) + "of file" + fh.getFileNumber(data));
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
                System.out.println("DR size: " + DRlength);
                // Get filename and remove from list
                String filename = new String(listbytearrayR.get(listbytearrayR.size() - 1));
                listbytearrayR.remove(listbytearrayR.size() - 1);
                // Convert to single byte array
                byte[] bytearrayR = fh.convergeToArray(listbytearrayR);
                System.out.println("ER size: " + bytearrayR.length);
                System.out.println("Flushing to disk!");

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
