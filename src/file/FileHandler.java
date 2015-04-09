package file;

import network.NetworkManager;
import network.Protocol;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tristan de Boer
 * @since 8-4-15
 */
public class FileHandler {

    /**
     * The maximum length of the data contained in the packet.
     */
    private static final int MAX_DATA_LENGTH = 1000;

    /**
     * Open a file from path and send it to the networklayer.
     * @param file The file that has to be send
     * @param networkManager The networkmanager the data is send to.
     */
    public List<byte[]> sendFile(Path file, int number, int client, NetworkManager networkManager){
        // File data
        byte[] data = this.openFile(file);
        int predictedtotal = data.length;
        // Split File data to 'stukjes'
        List<byte[]> listData = this.splitToPacketData(data);
        int total = 0;
        for(byte[] xdfon: listData){
            total += xdfon.length;
        }

        // Create a new list (this list will be returned)
        List<byte[]> sendList = new ArrayList<>();

        int sequencenumber = listData.size();
        int count = 0;

        for(byte[] sendData: listData){
            byte[] toSendData = new byte[sendData.length + 6];


            Integer firstline = count << 16 | sequencenumber;
            System.arraycopy(ByteBuffer.allocate(4).putInt(firstline).array(), 0, toSendData, 0, 4);


            System.arraycopy(ByteBuffer.allocate(4).putInt(number).array(), 2, toSendData, 4, 2);

            System.arraycopy(sendData, 0, toSendData, 7, sendData.length - 1);
            for(byte b: toSendData){
                System.out.print(b);
            }

            // Send data to network!
            if(networkManager != null) {
                try {
                    networkManager.constructPacket((byte) client, Protocol.DataType.TEXT, toSendData);

                } catch (IOException e) {

                }
            }
            sendList.add(toSendData);
            System.out.println();
            System.out.println("Wrote " + sendList.size() + "/" + sequencenumber);
            count++;
        }
        System.out.println(total + " / " + predictedtotal);
        return sendList;
    }

    /**
     * Creates an file out of listdata made by the datalistener.
     * @param listData The collected listdata
     */
    public void createFile(List<byte[]> listData){
        String filename = new String(listData.get(listData.size() - 1));
        listData.remove(listData.size() - 1);
        byte[] convergeddata = this.convergeToArray(listData);
        this.writeFile(convergeddata, filename);
    }

    /**
     * Opens a file from path and returns the byte array of the file
     * @param file The absolute path to the file.
     */
    public byte[] openFile(Path file){

        try {
            return Files.readAllBytes(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Writes data to a filename.
     * @param listdata The data that has to be written
     * @param filename The filename
     */
    public void writeFile(byte[] listdata, String filename){
        Path p = Paths.get("./a.png");
        try {
            FileOutputStream fos = new FileOutputStream(p.toFile());
            fos.write(listdata);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Converges a List of bytes to a single byte array.
     * @param listData List of bytes
     * @return res
     */
    public byte[] convergeToArray(List<byte[]> listData){
        int length = 0;
        for(byte[] data: listData){
            length += data.length;
        }
        byte[] res = new byte[length];
        int current = 0;
        for(byte[] data: listData){
            System.arraycopy(data, 7, res, current, data.length);
            current += data.length;
        }
        return res;
    }

    /**
     * Splits a single byte array to a list.
     * @param data Single byte array
     * @return reslist
     */
    public List<byte[]> splitToPacketData(byte[] data){
        List<byte[]> reslist = new ArrayList<>();
        for(int i = 0; i < data.length; i += MAX_DATA_LENGTH){
            byte[] resByteArray = new byte[Math.min(MAX_DATA_LENGTH, data.length - i)];
            System.arraycopy(data, i, resByteArray, 0, Math.min(MAX_DATA_LENGTH, data.length - i));
            reslist.add(resByteArray);
        }
        return reslist;
    }
}
