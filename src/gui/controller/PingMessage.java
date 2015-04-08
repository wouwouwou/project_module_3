package gui.controller;

/**
 * Created by tristan on 7-4-15.
 */
public class PingMessage implements Message{
    // The ID of the sender of a ping
    private final int id;

    // The name of a sender
    private final String name;

    // ------------------- Constructor --------------------------------------

    /**
     * PingMessage
     * @param id the ID of a sender of a ping
     * @param name the name of a sender of ping
     */
    public PingMessage(int id, String name) {
        this.id = id;
        this.name = name;
    }


    // ------------------- Getters ---------------------------------------------------------

    /**
     * Returns the name of a sender
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the id of a sender
     * @return id
     */
    public int getId() {
        return id;
    }
}
