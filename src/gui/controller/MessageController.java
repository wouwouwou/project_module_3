package gui.controller;

import file.FileHandler;
import file.FileReceiver;
import gui.Gui;
import gui.SoundPlayer;
import network.AckListener;
import network.DataListener;
import network.NetworkManager;
import network.Protocol;
import network.packet.Packet;

import javax.swing.*;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Controls the GUI. Implements network.DataListener and network.AckListener
 * @author Tistan de Boer
 * @since 8-4-15
 */
public class MessageController implements DataListener, AckListener{


    // -----<=>-----< Fields >-----<=>----- \\
    /**
     * The name of the user himself. In this case 'Ikzelf', which stands for 'I' in Dutch.
     */
    private static final String OWN_NAME = "Ikzelf";

    /**
     * The ID of the client himself, determined by the NetworkManager.
     */
    private static int OWN_ID;

    /**
     * The <code>Gui</code> that needs to be controlled.
     */
    private final Gui gui;

    /**
     * The <code>NetworkManager</code> that is used to control the network.
     */
    private final NetworkManager networkManager;

    /**
     * The <code>FileReceiver</code> that is used to control(and display the status) of the incoming files.
     */
    private final FileReceiver fileReceiver;

    /**
     * The <code>FileReceiver</code> that is used to display the status of the outgoing files.
     */
    private final FileReceiver fileAcker;

    /**
     * The Model that is used to store all chats (in <code>HashMap</code>. DefaultListModel
     * is used because it can be used to easily populate a JList.
     */
    private final HashMap<Integer, DefaultListModel<ChatMessage>> chatModel = new HashMap<>();

    /**
     * The Model that is used to store all clients. DefaultListModel is used because it can be used to easily populate a JList.
     */
    private final DefaultListModel<Client> clientModel = new DefaultListModel<>();

    /**
     * The Model that is used to store all files that are transferred
     * Unused since <code>ProcessMessage</code> is extending <code>ChatMessage</code>, it can be stored in <code>chatModel</code>.
     */
    private final DefaultListModel<ProcessMessage> processMessage = new DefaultListModel<>();

    /**
     * The amount of files sent (used to determine the unique id)
     */
    private int filecount = 0;


    // -----<=>-----< Constructor(s) >-----<=>----- \\
    /**
     *  Creates a new GUI that is linked to field <code>gui</code>.
     *  <p>
     *      Start a new <code>MessageController</code>, with a fileReceiver (a class that handles incoming data-packets),
     *      fileAcker (a class that handles incoming ACK's of sent data-packets), a <code>Gui</code>, a <code>networkManager</code>.
     *      It also adds an AckListener and a DataListener to the <code>FileReceiver</code>.
     *  </p>
     *  @param networkManager The networkmanager that can be called.
     */
    public MessageController(NetworkManager networkManager) {
        // fileReceiver will be set once.
        fileReceiver = new FileReceiver(this, false);
        fileAcker = new FileReceiver(this, true);


        gui = new Gui(this);
        this.networkManager = networkManager;

        OWN_ID = Protocol.CLIENT_ID;

        this.networkManager.getIncomingPacketHandler().addAckListener(this);
        this.networkManager.getIncomingPacketHandler().addDataListener(this);
    }


    // -----<=>-----< Actions that can be called >-----<=>----- \\
    /**
     * Sends a message to recipient <code>currentView</code> with message <code>messageField.getText()</code>.
     * Sends the message to it's own listener and to the NetworkLayer.
     */
    public void sendMessage() {
        if(!gui.getMessageField().getText().equals("")) {
            Packet packet = networkManager.constructPacket((byte) clientModel.get(gui.getCurrentView()).getId(), Protocol.DataType.TEXT, gui.getMessageField().getText().getBytes());
            networkManager.getOutgoingPacketHandler().send(packet);

            // Send message to own list
            ChatMessage message = new ChatMessage(gui.getMessageField().getText(), OWN_NAME, new Date(), clientModel.get(gui.getCurrentView()).getId(), OWN_ID);
            addChatMessage(message);
            gui.getMessageField().setText("");
            gui.setupAutoScroll();
        }
    }

    /**
     * Send the ACK to an FileReceiver that has no capability of forming files.
     * @param packet the packet that was ACKed.
     */
    @Override
    public void onAck(Packet packet) {
        if(packet.getDataType() == Protocol.DataType.FILE) {
            fileAcker.onReceive(packet);
        }
    }

    /**
     *  Receive a message and determine what to do. An packet with datatype <code>Protocol.DataType.TEXT</code>
     *  <p>
     *      If the packet has DataType <code>Protocol.DataType.TEXT</code>, a new chat message will be added to the entry
     *      of chatModel that has the same user assigned as the clientModel.
     *
     *      If a packet has DataType <code>Protocol.DataType.PING</code>, the 'last seen' of a user will be updated and
     *      his name will be updated.
     *
     *      If the packet has DataType <code>Packet.DataType.FILE</code>, the packet will be sent to a <code>FileReceiver</code>.
     *      This will be done in a Thread, so that the GUI can continue.
     *  </p>
     *  @param packet The message the sender has got to tell.
     */
    public void onReceive(Packet packet) {
        packet = packet.deepCopy();
        if(packet.hasFlag(Protocol.Flags.BROADCAST)){
            packet.setDestination((byte) 0);
        }
        if(packet.getDataType() == Protocol.DataType.TEXT){
            // If packet is of type Protocol.DataType.TEXT, add it to the queue of the source
            for(int i = 0; i < clientModel.size(); i++){
                if((clientModel.get(i).getId() == packet.getSource())){
                    addChatMessage(new ChatMessage(new String(packet.getData()), clientModel.get(i).getName(), new Date(), packet.getDestination(), packet.getSource()));
                    //System.out.println("Added chat message " + new String(packet.getData()));
                    break;
                }
            }
            if(new String(packet.getData()).equals("!pinguplay")){
                SoundPlayer.playSound(true);
            }else if(gui.getSoundEnabled()) {
                SoundPlayer.playSound(false);
            }
            gui.getList1().revalidate();
            gui.getList1().repaint();

        }else if(packet.getDataType() == Protocol.DataType.PING){
            // If packet is of type Protocol.Datatype.PING, add a client to the clientModel and/or update the 'last seen' date
            int client = -1;
            for(int i = 0; i < clientModel.size(); i++){
                if(clientModel.get(i).getId() == packet.getSource()){
                    client = i;
                    break;
                }
            }

            if(client > 0){
                // Check if 'via' was changed.
                for(int i = 0; i < clientModel.size(); i++){
                    if(networkManager.getTableEntryByDestination((byte)clientModel.get(client).getId())!= null && clientModel.get(i).getId() == networkManager.getTableEntryByDestination((byte)clientModel.get(client).getId())[2]){
                        clientModel.get(client).setRoute(clientModel.get(i).getName());
                        break;
                    }
                }
                clientModel.get(client).setDate();
                // Update client name
                clientModel.get(client).setName(new String(packet.getData()));
            }else{
                addClient(packet.getSource(), new String(packet.getData()));
            }
            gui.getList1().revalidate();
            gui.getList1().repaint();
        }else if(packet.getDataType() == Protocol.DataType.FILE){
            // If packet is of type Protocol.DataType.FILE, use FileReceiver to determine further actions.
            final Packet finalPacket = packet;
            new Thread(
                    new Runnable() {

                        public void run() {
                            fileReceiver.onReceive(finalPacket);
                        }
                    }).start();

        }
    }

    /**
     * Handles the file control. Splits up an packet and sends it to the NetworkManager as a packet.
     * <p>
     *     This thread opens a file and splits it to byte-arrays of a certain length. Headers are added to the byte arrays
     *     Packets are generated with that byte-arrays and these packets are sent to the networkManager.
     * </p>
     * @param path The path of the file that needs to be send.
     * @see <a href="../../../Project%20files/Protocol_design.pdf">Protocol Design</a>
     */
    public void sendFile(final Path path){
        new Thread(
                new Runnable() {

                    public void run() {
                        FileHandler fileHandler = new FileHandler();
                        // Open file
                        byte[] bytearrayS = fileHandler.openFile(path);
                        //System.out.println("bytearrayS size: " + bytearrayS.length);


                        // Split file to multiple byte arrays
                        List<byte[]> listbytearrayS = fileHandler.splitToPacketData(bytearrayS);
                        int listbytearraySlength = 0;
                        for(byte[] listbytearrayStocount: listbytearrayS){
                            listbytearraySlength += listbytearrayStocount.length;
                        }
                        //System.out.println("listbytearrayS size: " + listbytearraySlength);

                        // Add file name to data
                        listbytearrayS.add(path.getFileName().toString().getBytes());
                        // Add information
                        filecount++;
                        List<byte[]> CS = fileHandler.addHeaders(listbytearrayS, filecount);
                        int CSlength = 0;
                        for(byte[] CStocount: CS){
                            CSlength += CStocount.length - 6;
                        }

                        // Send data to other client(s) using the NetworkManager
                        for(byte[] toSendData: CS){
                            Packet packet;
                            byte destination = ByteBuffer.allocate(4).putInt(clientModel.get(gui.getCurrentView()).getId()).array()[3];
                            packet = networkManager.constructPacket(destination, Protocol.DataType.FILE, toSendData);
                            System.out.println("Sending packet to " + destination);
                            networkManager.getOutgoingPacketHandler().send(packet);
                        }


                    }
                }).start();

    }


    // -----<=>-----< Sub-Methods for onReceive(Packet packet) >-----<=>----- \\
    /**
     * Add an entry to the chatmessages.
     * @param message The message that should be added
     */
    public void addChatMessage(ChatMessage message){
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
            // If the queue doesn't exist, create a new one.
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
        gui.updateList2();
    }

    /**
     * Add a message to the list of ProcessMessages
     * @param pm ProcessMessage
     */
    public void addProcessMessage(ProcessMessage pm) {
        processMessage.addElement(pm);
    }

    /**
     * Adds an client to <code>clientModel</code>.
     * @param id The id of a client that has to be added
     * @param name The name of the client that has to be added
     */
    public void addClient(int id, String name) {
        clientModel.addElement(new Client(id, name, new Date(), true));
    }


    // -----<=>-----< Getters (mostly used by GUI) >-----<=>----- \\
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

    /**
     * Calls gui.updateList2().
     * @see gui.Gui
     */
    private void updateList2() {
        gui.updateList2();
    }

    /**
     * Set the message of a message in an index of chatModel.
     * @param i An entry of chatModel to alter.
     * @param j An entry of chatModel -> DefaultListModel to alter.
     * @param s The change that should be made.
     */
    public void setMessage(int i, int j, String s) {
        chatModel.get(i).get(j).setMessage(s);
        updateList2();
    }

    /**
     * Returns whether sounds are enabled.
     * @return Boolean
     */
    public boolean getSoundEnabled() {
        return gui.getSoundEnabled();
    }
}
