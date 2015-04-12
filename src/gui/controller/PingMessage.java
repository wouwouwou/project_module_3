package gui.controller;

/**
 * The message that is derived from a PingMessage.
 * @author Tistan de Boer
 * @since 7-4-15
 * @deprecated Not used in implementation.
 */
public class PingMessage implements Message{
    /**
     * The id of this message.
     */
    private final int id;

    /**
     * The name of the sender of the PingMessage.
     */
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
     * @return name The name of the sender
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the id of a sender
     * @return id The source-id of the sender
     */
    public int getId() {
        return id;
    }
}
