package gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

/**
 * Created by tristan on 7-4-15.
 */
public class Gui extends JFrame {
    private JPanel rootPanel;
    private JList list1;
    private JTextField messageField;
    private JButton sendButton;
    private JList list2;
    private DefaultListModel<ChatMessage> chatModel = new DefaultListModel<>();
    private DefaultListModel<ChatMessage> clientModel = new DefaultListModel<>();

    public Gui (){
        super("IAPC: Incrypted Ad-hoc Penguin Chat");
        setContentPane(rootPanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        this.sendMessageButton();
        this.messagesList();
    }

    // ------------------- Set up the main functions -------------------
    /**
     * Sets up the 'send message' button
     */
    public void sendMessageButton(){
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println(messageField.getText());
                //TODO: send message to network

                // Send message to own list
                ChatMessage message = new ChatMessage(messageField.getText(), "me", new Date());
                addChatMessage(message);
            }
        });
    }

    /**
     * Sets up the messages list
     */
    public void messagesList(){
        list2.setModel(chatModel);
        list2.setCellRenderer(new ListRenderer());
    }



    /**
     *  Receive a message and play a sound
     */
    public void onReceive(ChatMessage message) {

    }

    /**
     * Add an entry to the chatmessages
     */
    public void addChatMessage(ChatMessage message){
        chatModel.addElement(message);
    }

    /**
     * Update the clients list
     */
    public void changeClients(){

    }
}
