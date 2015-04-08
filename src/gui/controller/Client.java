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
     * @param id The id of a client
     * @param name The name of a client
     * @param date The last date the client has been seen
     * @param read Messages of the client have been read
     */
    public Client(int id, String name, Date date, Boolean read) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.read = read;
    }

    // ------------------- Getters -------------------
    /**
     * Returns the date of an client.
     * @return date The date a client has been seen last
     */
    public Date getDate() {
        return date;
    }

    /**
     * Returns the name of a client.
     * @return name The name of a client
     */
    public String getName() {
        return name;
    }

    /**
     * Returns <code>id</code> of a client.
     * @return id The id of a client
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the <code>read</code> to the given parameter.
     * @param read Determines whether the messages of the client have been read
     */
    public void setRead(Boolean read){
        this.read = read;
    }

    /**
     * Returns the <code>read</code> of a client (determines whether all messages of a client have been read.
     * @return read The Boolean whether messages have been read
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