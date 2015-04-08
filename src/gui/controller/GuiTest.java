package gui.controller;

import java.util.Date;

/**
 * Created by tristan on 7-4-15.
 */
public class GuiTest {
    public static void main(String[] args){
        MessageController gui = new MessageController();
        gui.onReceive(new PingMessage(2, "Tristan"));
        gui.onReceive(new PingMessage(3, "Gerben"));
        gui.onReceive(new PingMessage(3, "Gerben"));
        gui.onReceive(new PingMessage(3, "Gerben2"));
        gui.onReceive(new ChatMessage("Hoi! Hoe gaat het met jullie?", "Tristan", new Date(), 0, 2));
        gui.onReceive(new ChatMessage("Even een privebericht!", "Tristan", new Date(), 1, 2));
        gui.onReceive(new ChatMessage("Even een privebericht!", "Gerb00n", new Date(), 1, 3));
        gui.onReceive(new ChatMessage("Meh, mijn linux is weer eens gecrasht...", "Gerb00n", new Date(), 0, 3));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        gui.onReceive(new PingMessage(3, "Gerben"));
        gui.onReceive(new ChatMessage("Even een tweede privebericht!", "Tristan", new Date(), 1, 2));
        gui.onReceive(new ChatMessage("Never mind, hij doet het weer :)", "Gerb00n", new Date(), 0, 3));
        gui.onReceive(new ChatMessage("Even een swekkerend privebericht!", "Gerben", new Date(), 1, 3));
    }
}
