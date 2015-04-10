package gui;

import gui.controller.MessageController;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.File;

/**
 * Controlled by a MessageController
 * @author Tistan de Boer
 * @since 7-4-15
 */
public class Gui extends JFrame {
    private static final Font DEFAULT_FONT = new Font("Ubuntu", Font.PLAIN, 15);
    private final MessageController messageController;
    private JPanel rootPanel;
    private JList list1;
    // DO NOT DELETE chatPanel and clientPanel!
    @SuppressWarnings("unused")
    private JPanel chatPanel;
    @SuppressWarnings("unused")
    private JPanel clientPanel;
    private JTextField messageField;
    private JButton sendButton;
    private JList list2;
    private JButton fileButton;
    private JScrollPane scrollPane;
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
        this.fileButtonActionListener();


        // Setup JList Refresher
        this.setupThread();

        // Setup autoscroll
        this.setupAutoScroll();
        // Set visible
        setVisible(true);
    }

    private void setupAutoScroll() {
        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());
            }
        });
    }


    // ------------------- Set up the GUI functions -----------------------------------------------------------------------------------------------

    /**
     * Sets up the messages list.
     */
    public void messagesList(){
        try{
            messageController.getClientModel().get(currentView).setRead(true);
            if(messageController.getChatModel().get(messageController.getClientModel().get(currentView).getId()) == null){
                list2.setModel(new DefaultListModel<>());
            }else {
                list2.setModel(messageController.getChatModel().get(messageController.getClientModel().get(currentView).getId()));
            }
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
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set the font of some Components that aren't set.
     */
    private void setFonts() {
        messageField.setFont(DEFAULT_FONT);
        sendButton.setFont(DEFAULT_FONT);
        fileButton.setFont(DEFAULT_FONT);
    }

    /**
     * Refresh the left contacts bar every 15 seconds or so.
     */
    private void setupThread() {
        (new Thread() {
            public void run() {
                while(true){
                    try {
                        sleep(1000);
                        getList1().revalidate();
                        getList1().repaint();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
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

    /**
     * Add an action listener to <code>fileButton</code>. Show a dialog if the button is pressed.
     */
    public void fileButtonActionListener(){
        fileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                final JFileChooser fc = new JFileChooser();
                int returnVal = fc.showOpenDialog(Gui.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    messageController.sendFile(file.toPath());
                } else {

                }
            }
        });
    }

    public int getCurrentView() {
        return currentView;
    }

    public JList getList1(){
        synchronized (list1) {
            return list1;
        }
    }
    public JTextField getMessageField() {
        return messageField;
    }
}
