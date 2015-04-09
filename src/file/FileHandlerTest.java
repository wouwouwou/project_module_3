package file;

import network.NetworkManager;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by tristan on 8-4-15.
 */
public class FileHandlerTest {
    public static void main(String[] args){
        Path path = Paths.get("/home/tristan/Documents/1.png");
        FileHandler fileHandler = new FileHandler();
        fileHandler.sendFile(path, 1, 1, new NetworkManager());

    }
}
