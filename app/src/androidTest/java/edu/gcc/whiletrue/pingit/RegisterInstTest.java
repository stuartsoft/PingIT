package edu.gcc.whiletrue.pingit;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by BOWMANRS1 on 2/12/2016.
 */

@RunWith(AndroidJUnit4.class)
public class RegisterInstTest {

    @Rule//startup activity to test
    public ActivityTestRule<StartupActivity> mActivityRule = new ActivityTestRule<>(
            StartupActivity.class);

    @Test
    public void test1() {
        // Click buttons and do crap
        String name = "John Doe";
        String email = "unittest@gmail.com";
        String pass = "justinrocks";

        onView(withId(R.id.registerNameTxt))
            .perform(typeText(name), closeSoftKeyboard());
        onView(withId(R.id.registerEmailTxt))
                .perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.registerPasswordTxt))
                .perform(typeText(pass), closeSoftKeyboard());
        onView(withId(R.id.registerConfirmPassword))
                .perform(typeText(pass), closeSoftKeyboard());

        onView(withId(R.id.registerBtn)).perform(click());
        //pressBack();
        //pressBack();
    }
}
