package gui.controller;

import java.util.Date;
import java.util.List;

/**
 * @author Tristan de Boer
 * @since 9-4-15
 * Class for showing the progress of a file-transfer to the user.
 */
public class ProcessMessage extends ChatMessage{


    // -----<=>-----< Fields >-----<=>----- \\
    /**
     * The unique identifiers of a file
     */
    private final List fileid;

    /**
     * The size of a file
     */
    private final int size;


    // -----<=>-----< Constructor(s) >-----<=>----- \\
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

    // -----<=>-----< Getters >-----<=>----- \\
    /**
     * Returns the unique identifiers of the file message.
     * @return The unique id of the file
     */
    public List getFileId() {
        return fileid;
    }

    /**
     * Returns the size of the file (packet size).
     * @return The size of the file-packet.
     */
    public int getSize() {
        return size;
    }
}
