package gui;

import java.util.Date;

/**
 * Created by tristan on 7-4-15.
 */
public class GuiTest {
    public static void main(String[] args){
        Gui gui = new Gui();
        gui.onReceive(new PingMessage(1, "Tristan"));
        gui.onReceive(new ChatMessage("Hoi! Hoe gaat het met jullie?", "Tristan", new Date(), 0));
        gui.onReceive(new ChatMessage("Even een privebericht!", "Tristan", new Date(), 1));
        gui.onReceive(new ChatMessage("Even een privebericht!", "Tristan", new Date(), 1));
        gui.onReceive(new ChatMessage("Even een privebericht!", "Tristan", new Date(), 2));
    }
}
