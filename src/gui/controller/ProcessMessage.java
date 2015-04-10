package gui.controller;

import java.util.Date;
import java.util.List;

/**
 * Created by tristan on 9-4-15.
 */
public class ProcessMessage extends ChatMessage{

    private final List fileid;

    private final int size;

    private int process;


    public ProcessMessage(List fileid, int size, String message, String name, Date date, int destination, int source) {
        super(message, name, date, destination, source);
        this.fileid = fileid;
        this.size = size;
    }

    public List getFileId() {
        return fileid;
    }

    public int getProcess(){
        return process;
    }

    public int getSize() {
        return size;
    }

    public void setProcess(int process) {
        this.process = process;
    }


}
