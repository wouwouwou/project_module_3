package gui;

import gui.controller.ChatMessage;
import gui.controller.Client;
import network.Protocol;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Tistan de Boer
 * @since 7-4-15
 * Generates a ListCell based on a JLabel for use in an JList.
 */
public class ListRenderer extends JLabel implements ListCellRenderer {


    // -----<=>-----< Fields >-----<=>----- \\
    /**
     * The width of the profile image.
     */
    private static final int NEW_WIDTH = 40;

    /**
     * The height of the profile image.
     */
    private static final int NEW_HEIGHT = 40;

    /**
     * The id of the source of an entry.
     */
    @SuppressWarnings("unused")
    private int id;


    // -----<=>-----< Constructor(s) >-----<=>----- \\

    /**
     * Generates a new <code>ListRenderer</code>.
     * @param id
     */
    public ListRenderer(int id){
        this.id = id;
        setOpaque(true);
        setIconTextGap(24);
    }


    // -----<=>-----< Queries >-----<=>----- \\

    /**
     * Generate a new <code>ListCell</code> for a <code>ChatMessage</code> or a <code>Client</code> with certain information.
     * <p>
     *     <code>ChatMessage</code>: add a profileimage, senddate, and the message.
     *     <code>Client</code>: add a last seen and a 'New Message' notification if the user has unread messages.
     * </p>
     * @param jList
     * @param o
     * @param i
     * @param b
     * @param b1
     * @return
     */
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
        }else{
            Client entry = (Client) o;
            String routevia = "";
            String lastseentext = "With free penguins!";
            if(entry.getId() != 0) {
                routevia = "<font color='gray' size=-1'>via " + entry.getRoute() + "</font>";
                lastseentext = "<font color='gray' size=-1'>Laatst gezien: " + lastSeen(entry.getDate()) + "</font>";
            }
            if(entry.isRead()){
                setText("<html>"+entry.getName() +  routevia + "<br>"+lastseentext+"</html>");
            }else{
                setText("<html>"+entry.getName() +  "&emsp;<span color='GREEN'>\u272A Nieuw bericht!</span><br>"+lastseentext+"</html>");
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

    /**
     * Returns the 'last seen' as a string, rounded to seconds, minutes, hours, etc.
     * @param date
     * @return
     */
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
            if(diffMinutes == 1) {
                return diffMinutes + " minuut";
            }else{
                return diffMinutes + " minuten";
            }
        }else{
            if(diffSeconds > 15){
                return (Math.round(diffSeconds/10) * 10) + " seconden";
            }else{
                // User is probably online :)
                return "<font color='GREEN'>Online</font>";
            }
        }
    }

    /**
     * Return the profileimage of a user as a <code>ImageIcon</code> (with dimensions <code>NEW_WIDTH</code> and <code>NEW_HEIGHT</code>).
     * @param entry The chatmessage, containing information about the user.
     * @return ImageIcon
     */
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
