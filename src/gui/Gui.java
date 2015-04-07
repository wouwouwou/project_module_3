package gui;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tristan on 7-4-15.
 */
public class Gui extends JFrame {
    private static final Font DEFAULT_FONT = new Font("Ubuntu", Font.PLAIN, 15);
    private JPanel rootPanel;
    private JList list1;
    private JTextField messageField;
    private JButton sendButton;
    private JList list2;
    private JPanel chatPanel;
    private JPanel clientPanel;
    private ArrayList<DefaultListModel<ChatMessage>> chatModel = new ArrayList<DefaultListModel<ChatMessage>>();
    private DefaultListModel<Client> clientModel = new DefaultListModel<>();
    private int currentView = 0;

    public Gui (){
        super("IAPC: Incrypted Ad-hoc Penguin Chat");
        this.setDefaultLookAndFeel();
        setContentPane(rootPanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        this.clientList();
        this.sendMessageButton();
        this.messagesList();
        this.setFonts();
        this.sendTextfield();
        this.setSize(600,600);
    }

    private void sendTextfield() {
        messageField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
    }


    // ------------------- Set up the main functions -------------------

    /**
     * Set the client list (a group 0 is added as broadcast group).
     */
    private void clientList() {
        this.addClient(0, "Lobby");

        list1.setModel(clientModel);
        list1.setCellRenderer(new ListRenderer());
        list1.setSelectedIndex(0);
        list1.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                if(!listSelectionEvent.getValueIsAdjusting()){
                    currentView = list1.getSelectedIndex();
                    messagesList();
                }
            }
        });
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

    /**
     * Sets up the 'send message' button
     */
    public void sendMessageButton(){
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println(messageField.getText());
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        //TODO: send message to network with selected client (0 = broadcast)

        // Send message to own list
        ChatMessage message = new ChatMessage(messageField.getText(), "ikzelf", new Date(), currentView);
        addChatMessage(message);
        messageField.setText("");
    }

    /**
     * Sets up the messages list
     */
    public void messagesList(){
        try{
            list2.setModel(chatModel.get(currentView));
            clientModel.get(currentView).setRead(true);

        }catch (IndexOutOfBoundsException e){

        }
        list2.setCellRenderer(new ListRenderer());
    }



    /**
     *  Receive a message and determine what to do.
     */
    public void onReceive(Message message) {
        if(message instanceof PingMessage){
            addClient(((PingMessage) message).getId(), ((PingMessage) message).getName());
        }else if(message instanceof ChatMessage){
            if(((ChatMessage)message).getDestination() != currentView && ((ChatMessage)message).getDestination() < clientModel.size()){
                clientModel.get(((ChatMessage)message).getDestination()).setRead(false);
            }
            addChatMessage((ChatMessage) message);
        }
    }

    /**
     * Add an entry to the chatmessages
     */
    public void addChatMessage(ChatMessage message){
        try{
            chatModel.get(message.getDestination()).addElement(message);
        }catch(IndexOutOfBoundsException e){
            chatModel.add(message.getDestination(), new DefaultListModel<ChatMessage>());
            chatModel.get(message.getDestination()).addElement(message);
        }
        this.messagesList();

    }

    /**
     * Update the clients list
     */
    public void changeClients(){

    }

    /**
     * Adds an client to the list
     */
    public void addClient(int id, String name){
        clientModel.addElement(new Client(id, name, null, true));
    }
}
