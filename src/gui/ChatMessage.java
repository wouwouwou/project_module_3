package gui;

import java.util.Date;

/**
 * Created by tristan on 7-4-15.
 */
class ChatMessage implements Message {
    private final String message;

    private final String name;

    private final Date date;

    private final int destination;

    // ------------------- Constructor -------------------

    public ChatMessage(String message, String name, Date date, int destination) {
        this.message = message;
        this.name = name;
        this.date = date;
        this.destination = destination;
    }

    // ------------------- Getters -------------------
    /**
     * Returns the date of an message.
     * @return date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Returns the sender of an message.
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the message.
     * @return
     */
    public String getMessage() {
        return message;
    }

    public int getDestination() {
        return destination;
    }
}
