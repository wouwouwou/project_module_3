package file;

import network.NetworkManager;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by tristan on 8-4-15.
 */
public class FileHandlerTest {
    public static void main(String[] args){
        Path path = Paths.get("/home/tristan/Documents/1.png");
        FileHandler fileHandler = new FileHandler();
        List<byte[]> data = fileHandler.sendFile(path, 1, 1, null);

        System.out.println("Writing file...");
        fileHandler.writeFile(fileHandler.convergeToArray(data), "./a.png");
    }
}
