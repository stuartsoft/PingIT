package edu.gcc.whiletrue.pingit;

/**
 * Created by STEGNERBT1 on 1/30/2016.
 */
public class Ping {
    private String title;
    private String message;
    private String date;

    public Ping() {
        title = "TLC Helpdesk";
        message = "Your computer is ready to be picked up!";
        date = "01/30/16 at 6:00 PM";
    }

    public Ping(String title, String message, String date) {
        this.title = title;
        this.message = message;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }
}
