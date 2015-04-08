package gui;

import gui.controller.MessageController;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by tristan on 7-4-15.
 * Controlled by a MessageController.
 */
public class Gui extends JFrame {
    private static final Font DEFAULT_FONT = new Font("Ubuntu", Font.PLAIN, 15);
    private final MessageController messageController;
    private JPanel rootPanel;
    private JList list1;
    // DO NOT DELETE chatPanel and clientPanel!
    private JPanel chatPanel;
    private JPanel clientPanel;
    private JTextField messageField;
    private JButton sendButton;
    private JList list2;
    private int currentView = 0;

    /**
     * Setup the GUI
     */
    public Gui (MessageController mc){
        super("PC: Penguin Chat");
        this.messageController = mc;
        // Setup JFrame
        setContentPane(rootPanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Setup custom modules
        this.setDefaultLookAndFeel();
        this.clientList();
        this.messagesList();
        this.setFonts();
        this.setSize(600, 600);
        // Add all action listeners
        this.clientListActionListener();
        this.sendTextfieldActionListener();
        this.sendMessageButtonActionListener();

        // Set visible
        setVisible(true);
    }

    // ------------------- Set up the GUI functions -----------------------------------------------------------------------------------------------

    /**
     * Sets up the messages list.
     */
    public void messagesList(){
        try{
                messageController.getClientModel().get(currentView).setRead(true);
                list2.setModel(messageController.getChatModel().get(messageController.getClientModel().get(currentView).getId()));
        }catch (IllegalArgumentException e){

        }
        list2.setCellRenderer(new ListRenderer(messageController.getOwnID()));
    }

    /**
     * Set the client list (a group 0 is added as broadcast group).
     */
    private void clientList() {
        // Add 'lobby' as broadcast group
        messageController.addClient(0, "Lobby");

        // Set the entries of <code>list1</code> to clientModel and use <code>ListRenderer</code> as CellRenderer
        list1.setModel(messageController.getClientModel());
        list1.setCellRenderer(new ListRenderer(messageController.getOwnID()));

        list1.setSelectedIndex(0);
    }
    /**
     * Set default look and feel to Ubuntu look and feel.
     */
    private void setDefaultLookAndFeel() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set the font of some Components that aren't set.
     */
    private void setFonts() {
        messageField.setFont(DEFAULT_FONT);
        sendButton.setFont(DEFAULT_FONT);
    }

    //  ------------------- Action listeners -----------------------------------------------------------------------------------------------

    /**
     * Adds an action listener to the <code>messageField</code>. If <key>ENTER</key> is pressed, the message will be send.
     */
    private void sendTextfieldActionListener() {
        messageField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                messageController.sendMessage();
            }
        });
    }

    /**
     * Adds an action listener to the <code>sendButton</code>. The message will be send if this button is pressed.
     */
    public void sendMessageButtonActionListener(){
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                messageController.sendMessage();
            }
        });
    }

    /**
     * Add an action listener to <code>list1</code>. Update the messageList if an other index has been selected.
     */
    public void clientListActionListener(){
        list1.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                if (!listSelectionEvent.getValueIsAdjusting()) {
                    currentView = list1.getSelectedIndex();
                    messagesList();
                }
            }
        });
    }


    public int getCurrentView() {
        return currentView;
    }

    public JTextField getMessageField() {
        return messageField;
    }
}
