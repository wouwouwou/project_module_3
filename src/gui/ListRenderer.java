package gui;

import gui.controller.ChatMessage;
import gui.controller.Client;
import gui.controller.ProcessMessage;
import network.Protocol;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Generates a ListCell based on a JLabel for use in an JList.
 * @author Tistan de Boer
 * @since 7-4-15
 */
public class ListRenderer extends JLabel implements ListCellRenderer {
    // The width of the profile image
    private static final int NEW_WIDTH = 40;

    // The height of the profile image
    private static final int NEW_HEIGHT = 40;

    // The id of a the source of the entry (not used)
    @SuppressWarnings("unused")
    private int id;

    public ListRenderer(int id){
        this.id = id;
        setOpaque(true);
        setIconTextGap(24);
    }

    @Override
    public Component getListCellRendererComponent(JList jList, Object o, int i, boolean b, boolean b1) {
        if(o instanceof ChatMessage) {
            ChatMessage entry = (ChatMessage) o;
            DateFormat df = new SimpleDateFormat("HH:mm:ss MM-dd-yyyy");
            setText("<html>" + entry.getMessage() + "<br><font color='gray' size=-1'><b>" + entry.getName() + "</b> om " + df.format(entry.getDate()) + "</font></html>");
            // Set the image
            setIcon(getImage(entry));
            if(Protocol.CLIENT_ID == entry.getSource()){
                setBackground(new Color(0xB1C3CA));
            }else{
                setBackground(Color.WHITE);
            }
            setForeground(Color.BLACK);

            return this;
        }else if(o instanceof ProcessMessage){
            // Update visuals of the ProcessMessage
            return null;
        }else{
            Client entry = (Client) o;
            String lastseentext = "With free penguins!";
            if(entry.getId() != 0) {
                lastseentext = "<font color='gray' size=-1'>Laatst gezien:" + lastSeen(entry.getDate()) + "</font>";
            }
            if(entry.isRead()){
                setText("<html>"+entry.getName() +  "<br>"+lastseentext+"</html>");
            }else{
                setText("<html>"+entry.getName() +  "&emsp;<span color='GREEN'>\u272A Nieuw bericht!</span><br><font color='gray' size=-1'>Laatst gezien:&nbsp;" + lastSeen(entry.getDate())+"</font></html>");
            }
            if(b){
                setBackground(Color.LIGHT_GRAY);
                setForeground(Color.BLACK);
            }else {
                setBackground(Color.WHITE);
                setForeground(Color.BLACK);
            }
            return this;
        }
    }

    private String lastSeen(Date date){
        Date currentdate = new Date();
        long diff = currentdate.getTime() - date.getTime();
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60*1000) % 60;
        long diffHours = diff / (60*60*1000);
        int diffDays = (int) diff / (1000 * 60 * 60 * 24);
        if(diffDays > 0){
            return diffDays + " dagen";
        }else if(diffHours > 0){
            return diffHours + " uren";
        }else if(diffMinutes > 0){
            return diffMinutes + " minuten";
        }else{
            if(diffSeconds > 15){
                return (Math.round(diffSeconds/10) * 10) + "seconden";
            }else{
                return "enkele seconden";
            }
        }
    }

    private ImageIcon getImage(ChatMessage entry){
        int imagesource;
        if(entry.getSource() == 0){
            imagesource = 1;
        }else if(entry.getSource() > 0 && entry.getSource() < 5){
            imagesource = entry.getSource();
        }else{
            imagesource = 5;
        }
        URL iconURL = getClass().getResource("/gui/sources/"+imagesource+".png");
        ImageIcon icon = new ImageIcon(iconURL);
        Image img = icon.getImage();
        Image newimg = img.getScaledInstance(NEW_WIDTH, NEW_HEIGHT, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(newimg);
    }
}
