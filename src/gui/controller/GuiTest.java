package gui.controller;

import network.NetworkManager;

import java.util.Date;

/**
 * Tests the GUI's capability of receiving messages.
 * @author Tistan de Boer
 * @since 7-4-15
 */
public class GuiTest {
    /**
     * Checks if it is possible to receive messages.
     * @param args No args are available.
     */
    public static void main(String[] args){
        MessageController gui = new MessageController(new NetworkManager());
        // Send ping Tristan
        gui.onReceive(new PingMessage(2, "Tristan"));
        // Send ping Gerben
        gui.onReceive(new PingMessage(3, "Gerben"));

        // Check if Gerben can't be added twice
        gui.onReceive(new PingMessage(3, "Gerben"));
        gui.onReceive(new PingMessage(3, "Gerben2"));

        // Send messages.
        gui.onReceive(new ChatMessage("Hoi! Hoe gaat het met jullie?", "Tristan", new Date(), 0, 2));
        gui.onReceive(new ChatMessage("Even een privebericht!", "Tristan", new Date(), 1, 2));
        gui.onReceive(new ChatMessage("Even een privebericht!", "Gerb00n", new Date(), 1, 3));
        gui.onReceive(new ChatMessage("Meh, mijn linux is weer eens gecrasht...", "Gerb00n", new Date(), 0, 3));

        // Sleep
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Try to add Gerben an other time
        gui.onReceive(new PingMessage(3, "Gerben"));

        // Send messages
        gui.onReceive(new ChatMessage("Even een tweede privebericht!", "Tristan", new Date(), 1, 2));
        gui.onReceive(new ChatMessage("Never mind, hij doet het weer :)", "Gerb00n", new Date(), 0, 3));
        gui.onReceive(new ChatMessage("Even een swekkerend privebericht!", "Gerben", new Date(), 1, 3));
    }
}
