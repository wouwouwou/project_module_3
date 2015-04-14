package gui;

import gui.controller.MessageController;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * @author Tistan de Boer
 * @since 7-4-15
 * GUI, controlled by a MessageController.
 */
public class Gui extends JFrame {


    // -----<=>-----< Fields >-----<=>----- \\
    // Default font used in application
    private static final Font DEFAULT_FONT = new Font("Ubuntu", Font.PLAIN, 15);
    // The controller of this gui.
    private final MessageController messageController;
    // rootPanel containing all other JPanels.
    private JPanel rootPanel;
    /**
     * Left list, used for contacts.
     */
    private JList list1;
    // DO NOT DELETE chatPanel and clientPanel!
    @SuppressWarnings("unused")
    private JPanel chatPanel;
    @SuppressWarnings("unused")
    private JPanel clientPanel;
    private JTextField messageField;
    private JButton sendButton;
    /**
     * Right list, used for messages.
     */
    private JList list2;
    private JButton fileButton;
    private JScrollPane scrollPane;
    private JCheckBox playSoundCheckBox;
    /**
     * The view currently used (selected by <code>list1.getSelectedIndex()</code>.
     */
    private int currentView = 0;


    // -----<=>-----< Constructor(s) >-----<=>----- \\
    /**
     * Setup the GUI.
     * @param mc The MessageController which controls this GUI.
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


    // -----<=>-----< Methods >-----<=>----- \\
    /**
     * Scrolls scrollPane to the bottom of the screen.
     */
    public void setupAutoScroll() {
        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
    }


    // -----<=>-----< Set up the GUI functions >-----<=>----- \\
    /**
     * Sets up the messages list.
     * <p>
     *     Sets clientModel.get(currentView).getRead() to true.
     *     Creates a new DefaultListModel if no content is set, otherwise it will set the model to getChatModel().get( client belonging to current view).
     * </p>
     */
    public void messagesList(){
        try{
            if(currentView == 0){
                fileButton.setEnabled(false);
            }else{
                fileButton.setEnabled(true);
            }
            messageController.getClientModel().get(currentView).setRead(true);
            if(messageController.getChatModel().get(messageController.getClientModel().get(currentView).getId()) == null){
                list2.setModel(new DefaultListModel<>());
            }else {
                list2.setModel(messageController.getChatModel().get(messageController.getClientModel().get(currentView).getId()));
            }
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        list2.setCellRenderer(new ListRenderer(messageController.getOwnID()));
        setupAutoScroll();
    }

    /**
     * Set the client list (a group 0 is added as broadcast group).
     * <p>
     *     Adds Lobby as client 0 ands sets the model of list1 to <code>clientModel</code>, and selects the first index of <code>list1</code>.
     * </p>
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
     * Refresh the <code>list1</code> every 1 second (used to update the "last seen" messages).
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

    /**
     * Revalidate list2 if updates are made.
     */
    public void updateList2() {
        setupAutoScroll();
        list2.revalidate();
        list2.repaint();
    }


    // -----<=>-----< Action listeners >-----<=>----- \\
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
    private void sendMessageButtonActionListener(){
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
    private void clientListActionListener(){
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
    private void fileButtonActionListener(){
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


    // -----<=>-----< Getters >-----<=>----- \\
    /**
     * Returns <code>currentView</code>.
     * @return currentView
     */
    public int getCurrentView() {
        return currentView;
    }

    /**
     * Returns <code>list1</code>.
     * @return list1
     */
    public JList getList1(){
        synchronized (list1) {
            return list1;
        }
    }

    /**
     * Returns the <code>messageField</code>.
     * @return messageField
     */
    public JTextField getMessageField() {
        return messageField;
    }

    /**
     * Returns the value of 'play sound' checkbox.
     */
    public boolean getSoundEnabled() {
        return playSoundCheckBox.isSelected();
    }
}
