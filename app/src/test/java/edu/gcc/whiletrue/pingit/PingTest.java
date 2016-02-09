package edu.gcc.whiletrue.pingit;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.String;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class PingTest {

    private Ping testPing;

    @BeforeClass
    public static void onBeforeClass() throws Exception{
        /*This method will be called only once ever, before the start of the first test.
        Resources that will be shared by all tests should be initialized here.*/
    }

    @Before
    public void onBefore() throws Exception{
        //This method will be called before the start of each and every test
    }

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