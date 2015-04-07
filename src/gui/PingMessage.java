package gui;

import java.util.Date;

/**
 * Created by tristan on 7-4-15.
 */
public class PingMessage implements Message{
    private final int id;

    private final String name;

    public PingMessage(int id, String name) {
        this.id = id;
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
