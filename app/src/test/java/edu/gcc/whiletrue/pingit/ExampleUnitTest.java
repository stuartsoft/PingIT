package edu.gcc.whiletrue.pingit;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {

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
    public void addition_isCorrect() throws Exception {
        /*This is a standard test case. Use assertions to evaluate if classes
        are operating as intended. Assertions should be made using the expected value as the
        first parameter and actual value as the second parameter*/

        int expected = 4;
        int actual = 2+2;
        assertEquals(expected, actual);
    }
}