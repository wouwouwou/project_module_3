package gui.controller;

import java.util.Date;

/**
 * Created by tristan on 7-4-15.
 * A client that is connected to this client.
 */
public class Client {

    // The name of a client
    private final String name;

    // The id of a client
    private final int id;

    // The last time the client has been seen
    private Date date;

    // Determines whether you've read the last messages of this client
    private Boolean read;

    // ------------------- Constructor -------------------

    /**
     * Construct a client, based on a id, name, last seen date and whether the messages of this client have been read.
     * @param id
     * @param name
     * @param date
     * @param read
     */
    public Client(int id, String name, Date date, Boolean read) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.read = read;
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
     * Returns the name of a client.
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Returns <code>id</code> of a client.
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the <code>read</code> to the given parameter.
     * @param read
     */
    public void setRead(Boolean read){
        this.read = read;
    }

    /**
     * Returns the <code>read</code> of a client (determines whether all messages of a client have been read.
     * @return read
     */
    public Boolean isRead() {
        return read;
    }

    /**
     * Sets the date to the current date.
     */
    public void setDate() {
        this.date = new Date();
    }

}
