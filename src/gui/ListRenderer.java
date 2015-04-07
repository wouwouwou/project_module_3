package gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by tristan on 7-4-15.
 */
public class ListRenderer extends JLabel implements ListCellRenderer {

    private static final int NEW_WIDTH = 40;
    private static final int NEW_HEIGHT = 40;

    public ListRenderer(){
        setOpaque(true);
        setIconTextGap(12);
    }

    @Override
    public Component getListCellRendererComponent(JList jList, Object o, int i, boolean b, boolean b1) {
        if(o instanceof ChatMessage) {
            ChatMessage entry = (ChatMessage) o;
            DateFormat df = new SimpleDateFormat("HH:mm:ss MM-dd-yyyy");
            setText("<html>" + entry.getMessage()+"<br><font color='gray' size=-1'>"+entry.getName()+" om "+df.format(entry.getDate())+"</font></html>");
            // Set the image
            URL iconURL = getClass().getResource("/gui/1.png");
            ImageIcon icon = new ImageIcon(iconURL);
            Image img = icon.getImage();
            Image newimg = img.getScaledInstance(NEW_WIDTH, NEW_HEIGHT, java.awt.Image.SCALE_SMOOTH);
            icon = new ImageIcon(newimg);

            setIcon(icon);

            setBackground(Color.WHITE);
            setForeground(Color.BLACK);

            return this;
        }else{
            Client entry = (Client) o;
            if(entry.isRead()){
                setText(entry.getName() +  "");
                System.out.println("Read!");
            }else{
                setText("<html>"+entry.getName() +  "&emsp;<span color='GREEN'>\u272A Nieuw bericht!</span></html>");
                System.out.println("Not yet read!");
            }
            if(b){
                setBackground(Color.GRAY);
                setForeground(Color.BLACK);
            }else {
                setBackground(Color.WHITE);
                setForeground(Color.BLACK);
            }
            return this;
        }
    }
}
