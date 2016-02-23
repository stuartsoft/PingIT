package edu.gcc.whiletrue.pingit;

import android.support.test.espresso.action.ViewActions;
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
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by BOWMANRS1 on 2/12/2016.
 */

@RunWith(AndroidJUnit4.class)
public class RegisterUITest {

    String name = "John Doe";
    String email = "unittest@gmail.com";
    String pass = "justinrocks";

    @Rule//startup activity to test
    public ActivityTestRule<StartupActivity> mActivityRule = new ActivityTestRule<>(
            StartupActivity.class);

    //Regular, successful sign up
    @Test
    public void test01() {
        // Click fields, type info, and create an account

        onView(withId(R.id.registerNameTxt))
            .perform(typeText(name), closeSoftKeyboard());
        onView(withId(R.id.registerEmailTxt))
            .perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.registerPasswordTxt))
            .perform(typeText(pass), closeSoftKeyboard());
        onView(withId(R.id.registerConfirmPassword))
            .perform(typeText(pass), closeSoftKeyboard());

        onView(withId(R.id.registerBtn)).perform(click());

    }

    //test that the registration page warns if the user didn't enter their name
    @Test
    public void test03(){
        //onView(withId(R.id.registerNameTxt))
        // .perform(typeText(name), closeSoftKeyboard());
        onView(withId(R.id.registerEmailTxt))
            .perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.registerPasswordTxt))
            .perform(typeText(pass), closeSoftKeyboard());
        onView(withId(R.id.registerConfirmPassword))
            .perform(typeText(pass), closeSoftKeyboard());

        onView(withId(R.id.registerBtn)).perform(click());

        //wait for dialog to appear, then dismiss it
        onView(withText(R.string.nameNotValid)).perform(ViewActions.pressBack());
    }

    //Test that a blank confirm password field is rejected
    @Test
    public void test06(){
        onView(withId(R.id.registerNameTxt))
            .perform(typeText(name), closeSoftKeyboard());
        onView(withId(R.id.registerEmailTxt))
            .perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.registerPasswordTxt))
            .perform(typeText(pass), closeSoftKeyboard());
        //onView(withId(R.id.registerConfirmPassword))
        //  .perform(typeText(pass), closeSoftKeyboard());

        onView(withId(R.id.registerBtn)).perform(click());

        //wait for dialog to appear, then dismiss it
        onView(withText(R.string.confirmPasswordInvalid)).perform(ViewActions.pressBack());
    }

    //test that the registration page warns if the passwords don't match
    @Test
    public void test08(){
        onView(withId(R.id.registerNameTxt))
                .perform(typeText(name), closeSoftKeyboard());
        onView(withId(R.id.registerEmailTxt))
                .perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.registerPasswordTxt))
                .perform(typeText(pass), closeSoftKeyboard());
        onView(withId(R.id.registerConfirmPassword))
                .perform(typeText(pass+"1"), closeSoftKeyboard());

        onView(withId(R.id.registerBtn)).perform(click());

        //wait for dialog to appear, then dismiss it
        onView(withText(R.string.passwordsDontMatch)).perform(ViewActions.pressBack());
    }
}
