import gui.controller.MessageController;
import network.NetworkManager;

/**
 * Start the main program
 * @author Tristan de Boer, Wouter Bos, Gerben Meijer, Tim Hintzbergen
 * @version 1.0
 */
public class Main {
    /**
     * Start the main program
     * @param args No arguments need to be given.
     */
    public static void main(String[] args) {
        NetworkManager networkManager = new NetworkManager();
        MessageController gui = new MessageController(networkManager);
    }
}
