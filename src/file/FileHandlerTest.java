package file;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Tristan de Boer
 * @since 8-4-15
 */
public class FileHandlerTest {
    public static void main(String[] args){
        Path path = Paths.get("/home/tristan/Documents/1.png");
        FileHandler fileHandler = new FileHandler();
        fileHandler.sendFile(path, 1, 1);

    }
}
