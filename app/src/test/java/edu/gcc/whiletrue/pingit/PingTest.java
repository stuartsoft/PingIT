package edu.gcc.whiletrue.pingit;

import org.junit.Test;

import java.lang.String;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class PingTest {

    private Ping testPing;

    @Test
    public void setupDefaultPing() throws Exception {
        testPing = new Ping();
        assertEquals(Ping.titleDefault, testPing.getTitle());
        assertEquals(Ping.messageDefault, testPing.getMessage());
        assertEquals(Ping.dateDefault, testPing.getDate());
    }

    @Test
    public void setupCustomPing() throws Exception {
        String testTitle = "MY TITLE";
        String testMessage = "MY MESSAGE";
        String testDate = "MY DATE";

        testPing = new Ping(testTitle, testMessage, testDate);
        assertEquals(testTitle, testPing.getTitle());
        assertEquals(testMessage, testPing.getMessage());
        assertEquals(testDate, testPing.getDate());
    }
}