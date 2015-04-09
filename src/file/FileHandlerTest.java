package file;

import network.NetworkManager;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tristan on 8-4-15.
 */
public class FileHandlerTest {
    FileReceiver fr = new FileReceiver();

    public static void main(String[] args){
        new FileHandlerTest();
    }

    public FileHandlerTest(){
        Path path = Paths.get("/home/tristan/Documents/1.png");
        FileHandler fileHandler = new FileHandler();
        // Open file
        byte[] bytearrayS = fileHandler.openFile(path);
        System.out.println("bytearrayS size: " + bytearrayS.length);


        // Split file to multiple byte arrays
        List<byte[]> listbytearrayS = fileHandler.splitToPacketData(bytearrayS);
        int listbytearraySlength = 0;
        for(byte[] listbytearrayStocount: listbytearrayS){
            listbytearraySlength += listbytearrayStocount.length;
        }
        System.out.println("listbytearrayS size: " + listbytearraySlength);

        // Add information
        List<byte[]> CS = fileHandler.addHeaders(listbytearrayS, 1);
        int CSlength = 0;
        for(byte[] CStocount: CS){
            CSlength += CStocount.length - 6;
        }
        System.out.println("CS size: " + CSlength);

        for(byte[] bytearray: CS){
            fr.onReceive(bytearray);
        }
        /*
        List<byte[]> list = CS;
        // Remove the headers
        List<byte[]> listbytearrayR = fileHandler.removeHeaders(list);
        int DRlength = 0;
        for(byte[] DRtocount: listbytearrayR){
            DRlength += DRtocount.length;
        }
        System.out.println("DR size: " + DRlength);

        // Convert to single byte array
        byte[] bytearrayR = fileHandler.convergeToArray(listbytearrayR);
        System.out.println("ER size: " + bytearrayR.length);
        System.out.println("Flushing to disk!");

        fileHandler.writeFile(bytearrayR, "a.png");*/
    }

}
