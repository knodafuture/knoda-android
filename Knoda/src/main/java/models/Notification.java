package models;

/**
 * Created by jeffcailteux on 6/25/14.
 */
public class Notification {
    public String id = "";
    public String type = null;
    public String message = null;

    public Notification() {
    }

    public Notification(String id, String type) {
        this.id = id;
        this.type = type;
    }
}
