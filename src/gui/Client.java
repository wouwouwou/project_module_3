package gui;

import java.util.Date;

/**
 * Created by tristan on 7-4-15.
 */
public class Client {
    private final String name;

    private final int id;

    private final Date date;
    private Boolean read;

    // ------------------- Constructor -------------------

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
     * Returns the name of a client
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

    public void setRead(Boolean read){
        this.read = read;
    }

    public Boolean isRead() {
        return read;
    }
}
