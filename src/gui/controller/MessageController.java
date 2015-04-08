package gui.controller;

import gui.Gui;

import javax.swing.*;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by tristan on 8-4-15.
 * Controls the GUI.
 */
public class MessageController {
    // The ID of this client
    private static final int OWN_ID = 1;

    // The Gui that is used by this client
    private final Gui gui;

    // The chatModel that is used to store all chats (in a HashMap). A DefaultListModel can easily be used to populate a JList.
    private HashMap<Integer, DefaultListModel<ChatMessage>> chatModel = new HashMap<>();

    // The clientModel is used to store all clients. A DefaultListModel can easily be used to populate a JList.
    private DefaultListModel<Client> clientModel = new DefaultListModel<>();

    /**
     *  Creates a new GUI that is linked to field <code>gui</code>.
     */
    public MessageController(){
        gui = new Gui(this);
    }


    // ------------------- Actions that can be called ----------------------------------------------------------------------------

    /**
     * Sends a message to recipient <code>currentView</code> with message <code>messageField.getText()</code>. Sends the message to it's own listener and to the NetworkLayer.
     */
    public void sendMessage() {
        //TODO: send message to network with selected client (0 = broadcast)

        // Send message to own list
        ChatMessage message = new ChatMessage(gui.getMessageField().getText(), "ikzelf", new Date(), clientModel.get(gui.getCurrentView()).getId(), OWN_ID);
        addChatMessage(message);
        gui.getMessageField().setText("");
    }

    /**
     *  Receive a message and determine what to do. Messages can by of type <code>PingMessage</code> and <code>ChatMessage</code>.
     *  @param message The message the sender has got to tell.
     */
    public void onReceive(Message message) {
        if(message instanceof PingMessage){
            // Message is a ping message. Determine if the client is already added to the <code>clientModel</code>
            int client = -1;
            for(int i = 0; i < clientModel.size(); i++){
                if(clientModel.get(i).getId() == message.getId()){
                    client = i;
                    break;
                }
            }
            if(client > 0){
                clientModel.get(client).setDate();
            }else{
                addClient(message.getId(), (message.getName()));
            }
        }else if(message instanceof ChatMessage){
            // Message is a ChatMessage. Add the message to the list.
            addChatMessage((ChatMessage) message);
        }
    }

    // ------------------- Methods that can be called by onReceive ---------------------------------------------------------

    /**
     * Add an entry to the chatmessages.
     * @param message The message that should be added
     */
    private void addChatMessage(ChatMessage message){
        int queue;
        if(message.getDestination() == 0){
            queue = 0;
        }else if(message.getSource() != OWN_ID){
            queue = message.getSource();
        }else{
            queue = message.getDestination();
        }
        System.out.println(message.getMessage()+message.getName()+message.getDestination());
        try{
            chatModel.get(queue).addElement(message);
        }catch(NullPointerException e){
            chatModel.put(queue, new DefaultListModel<ChatMessage>());
            chatModel.get(queue).addElement(message);
        }

        if(queue != gui.getCurrentView()){
            for(int i = 0; i < clientModel.size(); i++){
                if(clientModel.get(i).getId() == queue){
                    clientModel.get(i).setRead(false);
                }
            }
        }
        gui.messagesList();
    }

    /**
     * Remove the client with <code>id</code> from <code>clientModel</code>.
     * @param id The id to delete
     */
    @SuppressWarnings("unused")
    private void changeClients(int id){
        for(int i = 0; i < clientModel.size(); i++){
            if(clientModel.get(i).getId() == id){
                clientModel.remove(i);
            }
        }
    }

    /**
     * Adds an client to <code>clientModel</code>.
     * @param id The id of a client that has to be added
     * @param name The name of the client that has to be added
     */
    public void addClient(int id, String name) {
        clientModel.addElement(new Client(id, name, new Date(), true));
    }

    // ------------------- Getters (mostly used by Gui) ---------------------------------------------------------

    /**
     * Returns the clientModel.
     * @return clientModel
     */
    public DefaultListModel<Client> getClientModel() {
        return clientModel;
    }

    /**
     * Returns the id of this client.
     * @return OWN_ID
     */
    public int getOwnID() {
        return OWN_ID;
    }

    /**
     * Returns the chatModel.
     * @return chatModel
     */
    public HashMap<Integer,DefaultListModel<ChatMessage>> getChatModel() {
        return chatModel;
    }
}
