package tests.file;

import file.FileHandler;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Tests whether a certain file can be split up to a certain packet size.
 * Headers will be added. After that, we check that packets can be rejoined to a file.
 *
 * NB: This test has not been made for the JUnit4 framework. Should be tested separately.
 * @author Tristan de Boer
 * @since 8-4-15
 */
public class FileHandlerTest {


    /**
     * Main method for starting the test.
     * @param args Starting arguments. Nothing is done with it.
     */
    public static void main(String[] args){
        new FileHandlerTest();
    }


    /**
     * Actually tests the class.
     * Change the path within the Path.get() method for the test to work.
     */
    public FileHandlerTest(){
        Path path = Paths.get("/home/tristan/Documents/2.png");
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

        // Add file name to data
        listbytearrayS.add(path.getFileName().toString().getBytes());

        // Add information
        List<byte[]> CS = fileHandler.addHeaders(listbytearrayS, 1);
        int CSlength = 0;
        for(byte[] CStocount: CS){
            CSlength += CStocount.length - 6;
        }
        System.out.println("CS size: " + CSlength);

    }

}
