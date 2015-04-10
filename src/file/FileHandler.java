package file;

import network.Protocol;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private static int MAX_DATA_LENGTH;

    public FileHandler(){
        MAX_DATA_LENGTH = Protocol.MAX_COMMUNICATION_PAYLOAD_SIZE - 6;
    }

    /**
     *  Adds headers to a List of bytearrays (to keep them in order and check if they all are available).
     */
    public List<byte[]> addHeaders(List<byte[]> dataArray, int filenumber){
        List<byte[]> result = new ArrayList<>();
        int partCount = 0;
        for(byte[] data: dataArray){
            byte[] res = new byte[data.length + 6];
            int header1 = partCount << 16 | dataArray.size();
            int header2 = filenumber;
            System.arraycopy(ByteBuffer.allocate(4).putInt(header1).array(), 0, res, 0, 4);
            System.arraycopy(ByteBuffer.allocate(4).putInt(header2).array(), 2, res, 4, 2);
            System.arraycopy(data, 0, res, 6, data.length);
            result.add(res);
            partCount++;
        }
        return result;
    }

    /**
     *  Removes the headers from an List of bytearrays
     */
    public List<byte[]> removeHeaders(List<byte[]> headerArray){
        List<byte[]> result = new ArrayList<>();
        for(byte[] headerData: headerArray){
            byte[] res = new byte[headerData.length - 6];
            System.arraycopy(headerData, 6, res, 0, headerData.length - 6);
            result.add(res);
        }
        return result;
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
        if(createDir(new File (System.getProperty("user.home")+"/Penguin/"))) {
            Path p = Paths.get(System.getProperty("user.home") + "/Penguin/" + filename);
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
    }

    /**
     * Create PenguinDir (if not exists)
     */
    public boolean createDir(File filename){
        return (filename.exists() || (!filename.exists() && filename.mkdir()));
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

    /**
     * Returns the sequence number of a packet.
     * @param data
     * @return
     */
    public int getSequenceNumber(byte[] data){
        byte[] countArray = new byte[4];
        System.arraycopy(data, 0, countArray, 2, 2);
        return byteArrayToInt(countArray);
    }

    /**
     * Returns the total amount of packets to be expected for this file.
     */
    public int getTotalPackets(byte[] data){
        byte[] totalArray = new byte[4];
        System.arraycopy(data, 2, totalArray, 2, 2);
        return byteArrayToInt(totalArray);
    }

    /**
     * Returns the number of this file.
     */
    public int getFileNumber(byte[] data){
        byte[] fileArray = new byte[4];
        System.arraycopy(data, 4, fileArray, 2, 2);
        return byteArrayToInt(fileArray);
    }

    /**
     * Converts a byte array to an integer.
     * @param b the byte array
     * @return int The integer representation of the byte
     */
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
