package edu.gcc.whiletrue.pingit;

import java.io.Serializable;

public class Ping implements Serializable {
    private String title;
    private String message;
    private String date;

    /*The following strings are defined outside of strings.xml only because the Pings class
    is designed to function independently of the Android frameworks. This allows
    us to run standard JUnit tests on the class and more accurately validate
    the class's reliability in isolation, independently of any Android dependencies.*/
    public final static String titleDefault = "TLC Helpdesk";
    public final static String messageDefault = "Your computer is ready for pickup.";
    public final static String dateDefault = "01/01/70 at 12:00:00 AM";

    public Ping() { //default constructor
        title = titleDefault;
        message = messageDefault;
        date = dateDefault;
    }

    public Ping(String title, String message, String date) { //constructor for custom pings
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
