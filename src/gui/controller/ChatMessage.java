package gui.controller;

import java.util.Date;

/**
 * A message that contains certain information.
 * @author Tristan de Boer
 * @since 7-4-15
 */
public class ChatMessage implements Message {

    // The message
    private final String message;

    // The name of the sender of a message
    private final String name;

    // The date a message has been send
    private final Date date;

    // The destination of a message
    private final int destination;

    // The source of a message
    private int source;

    // ------------------- Constructor ----------------------------------------------------------------------------

    /**
     * A new ChatMessage
     * @param message The message of the <code>ChatMessage</code>
     * @param name The name of the sender
     * @param date The date the <code>ChatMessage</code> has been send
     * @param destination The destination
     * @param source The source
     */
    public ChatMessage(String message, String name, Date date, int destination, int source) {
        System.out.printf("New Chatmessage s: %s, d: %s\n", source, destination);
        this.message = message;
        this.name = name;
        this.date = date;
        this.destination = destination;
        this.source = source;
    }

    // ------------------- Getters ----------------------------------------------------------------------------
    /**
     * Returns the date of an message.
     * @return date The date of an message
     */
    public Date getDate() {
        return date;
    }

    /**
     * Returns the sender of an message.
     * @return name The name of the sender
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the source of an message.
     * @return source The source-id of the sender
     */
    public int getId() {
        return source;
    }

    /**
     * Returns the message.
     * @return message The message the sender told
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the destination of a message.
     * @return destination The destination of the message (this host)
     */
    public int getDestination() {
        return destination;
    }

    /**
     * Returns the source of a message.
     * @return source The source-id of the message
     */
    public int getSource(){
        return getId();
    }
}
