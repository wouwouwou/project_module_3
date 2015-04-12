package gui.controller;

import java.util.Date;
import java.util.List;

/**
 * Created by tristan on 9-4-15.
 */
public class ProcessMessage extends ChatMessage{

    /**
     * The unique identifiers of a file
     */
    private final List fileid;

    /**
     * The size of a file
     */
    private final int size;


    /**
     * A new message that describes the progress of sending/receiving a file.
     * @param fileid The unique identifiers of a file
     * @param size The size of a file.
     * @param message The message that describes the progress of a file.
     * @param name The name of the user that has sent the file.
     * @param date The date the file was sent.
     * @param destination The destination of the message of this file.
     * @param source The source of the file.
     */
    public ProcessMessage(List fileid, int size, String message, String name, Date date, int destination, int source) {
        super(message, name, date, destination, source);
        this.fileid = fileid;
        this.size = size;
    }

    /**
     * Returns the unique identifiers of the file message.
     * @return
     */
    public List getFileId() {
        return fileid;
    }

    /**
     * Returns the size of the file (packet size).
     * @return
     */
    public int getSize() {
        return size;
    }
}
