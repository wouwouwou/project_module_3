package file;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tristan on 8-4-15.
 */
public class FileHandler {


    /**
     * The maximum length of the data contained in the packet.
     */
    private static final int MAX_DATA_LENGTH = 1000;

    /**
     * Open a file from path and send it to the networklayer.
     * @param file The file that has to be send
     */
    public void sendFile(Path file, int number, int client){
        byte[] data = this.openFile(file);
        List<byte[]> listData = this.splitToPacketData(data);
        listData.add(file.getFileName().toString().getBytes());
        int sequencenumber = listData.size();
        int count = 0;
        for(byte[] sendData: listData){
            byte[] toSendData = new byte[sendData.length + 6];


            Integer firstline = count << 16 | sequencenumber;
            System.arraycopy(ByteBuffer.allocate(4).putInt(firstline).array(), 0, toSendData, 0, 4);

            short filenumber = (short) number;
            System.arraycopy(ByteBuffer.allocate(4).putInt(filenumber).array(), 0, toSendData, 4, 2);

            System.arraycopy(sendData, 0, toSendData, 6, sendData.length - 1);
            for(byte b: toSendData){
                System.out.print(b);
            }

            //  gerboon.sendPacket(data, client);
            //  TODO SEND PACKET TO NETWORKLAYER
            System.out.println();
            count++;
        }
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
        Path p = Paths.get(filename);
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
            System.arraycopy(data, 0, res, current, data.length);
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
