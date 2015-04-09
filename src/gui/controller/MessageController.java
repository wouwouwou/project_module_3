package gui.controller;

import exceptions.network.InvalidPacketException;
import file.FileHandler;
import file.FileReceiver;
import gui.Gui;
import network.DataListener;
import network.NetworkManager;
import network.Protocol;
import network.packet.Packet;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;

/**
 * Controls the GUI.
 * @author Tistan de Boer
 * @since 8-4-15
 */
public class MessageController implements DataListener{
    // The ID of this client
    private static final int OWN_ID = 1;

    // The Gui that is used by this client
    private final Gui gui;
    private final NetworkManager networkManager;
    private final FileReceiver fileReceiver;

    // The chatModel that is used to store all chats (in a HashMap). A DefaultListModel can easily be used to populate a JList.
    private HashMap<Integer, DefaultListModel<ChatMessage>> chatModel = new HashMap<>();

    // The clientModel is used to store all clients. A DefaultListModel can easily be used to populate a JList.
    private DefaultListModel<Client> clientModel = new DefaultListModel<>();

    private int filecount = 0;
    /**
     *  Creates a new GUI that is linked to field <code>gui</code>.
     *  @param networkManager The networkmanager that can be called.
     */
    public MessageController(NetworkManager networkManager) {
        // fileReceiver will be set once.
        fileReceiver = new FileReceiver();

        gui = new Gui(this);
        this.networkManager = networkManager;
        if(this.networkManager == null){
            System.out.println("networkManager is null");
        }
        this.networkManager.getIncomingPacketHandler().addDataListener(this);
    }


    // ------------------- Actions that can be called ----------------------------------------------------------------------------

    /**
     * Sends a message to recipient <code>currentView</code> with message <code>messageField.getText()</code>. Sends the message to it's own listener and to the NetworkLayer.
     */
    public void sendMessage() {
        try {
            Packet packet = networkManager.constructPacket((byte)clientModel.get(gui.getCurrentView()).getId(), Protocol.DataType.TEXT, gui.getMessageField().getText().getBytes());
            networkManager.getOutgoingPacketHandler().send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //TODO: send message to network with selected client (0 = broadcast)

        // Send message to own list
        ChatMessage message = new ChatMessage(gui.getMessageField().getText(), "ikzelf", new Date(), clientModel.get(gui.getCurrentView()).getId(), OWN_ID);
        addChatMessage(message);
        gui.getMessageField().setText("");
    }

    /**
     *  Receive a message and determine what to do. Messages can by of type <code>PingMessage</code> and <code>ChatMessage</code>.
     *  @param packet The message the sender has got to tell.
     */
    public void onReceive(Packet packet) {
        packet = packet.clone();
        if(packet.hasFlag(Protocol.Flags.BROADCAST)){
            packet.setDestination((byte) 0);
        }
        if(packet.getDataType() == Protocol.DataType.TEXT){
            for(int i = 0; i < clientModel.size(); i++){
                if(clientModel.get(i).getId() == packet.getSource()){
                    addChatMessage(new ChatMessage(new String(packet.getData()), clientModel.get(i).getName(), new Date(), packet.getDestination(), packet.getSource()));
                    System.out.println("Added chat message " + new String(packet.getData()));
                    break;
                }
            }
        }else if(packet.getDataType() == Protocol.DataType.PING){
            int client = -1;
            for(int i = 0; i < clientModel.size(); i++){
                if(clientModel.get(i).getId() == packet.getSource()){
                    client = i;
                    break;
                }
            }
            if(client > 0){
                clientModel.get(client).setDate();
            }else{
                addClient(packet.getSource(), new String(packet.getData()));
            }
        }else if(packet.getDataType() == Protocol.DataType.FILE){
            // TODO ROUTING TO FILE HANDLER/FILE RECEIVER


        }
    }

    /**
     * Handles the file control.
     * @param path The path of the file that needs to be send
     */
    public void sendFile(Path path){
        FileHandler fh = new FileHandler();
        // Read the file using a buffered reader
        fh.sendFile(path, filecount, gui.getCurrentView(), networkManager);
        filecount++;
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
