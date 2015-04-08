package gui.controller;

/**
 * Created by tristan on 7-4-15.
 * The interface Message, used by some XXMessages that are send by the networklayer.
 */
interface Message {
    /**
     * The name of a sender of a message
     * @return name
     */
    String getName();

    /**
     * The id of a sender of a message
     * @return id
     */
    int getId();
}
