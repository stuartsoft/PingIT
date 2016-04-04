package edu.gcc.whiletrue.pingit;

import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;

/**
 * Created by BOWMANRS1 on 2/12/2016.
 */

@RunWith(AndroidJUnit4.class)
public class RegisterUITest {

    String name = "John Doe";
    String email = "unittestX@gmail.com";
    String pass = "justinrocks1";
    String shortPass = "poo";
    String invalidEmail = "Leeroy Jenkins";

    @Rule//startup activity to test
    public ActivityTestRule<StartupActivity> mActivityRule = new ActivityTestRule<>(
            StartupActivity.class);

    //Regular, successful sign up
    @Test
    public void test01() {
        String disposableEmail = "asdf@gmail.com";
        /*this is the email used only for testing registration, because it will be repeatidly
        created and deleted, so it must be separate from the regular email
         */

        // Click fields, type info, and create an account
        onView(withId(R.id.registerNameTxt))
            .perform(typeText(name), closeSoftKeyboard());
        onView(withId(R.id.registerEmailTxt))
            .perform(typeText(disposableEmail), closeSoftKeyboard());
        onView(withId(R.id.registerPasswordTxt))
            .perform(typeText(pass), closeSoftKeyboard());
        onView(withId(R.id.registerConfirmPassword))
            .perform(typeText(pass), closeSoftKeyboard());

        onView(withId(R.id.registerBtn)).perform(click());

        //check that we are on the home activity
        onView(withId(R.id.main_content)).check(matches(isDisplayed()));

        //Delete the user so we can run this test again without fail.
        ParseUser user = ParseUser.getCurrentUser();
        try {
            user.delete();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    //Test that tapping the button at the bottom of the screen switches to the Login screen
    @Test
    public void test02() {
        onView(withId(R.id.switchToLoginBtn)).perform(click());
        onView(withId(R.id.fragment_login)).check(matches(isDisplayed()));
    }

    //test that the registration page warns if the user didn't enter their name
    @Test
    public void test03() {
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

    //Test that a blank email field is rejected
    @Test
    public void test04() {
        onView(withId(R.id.registerNameTxt))
                .perform(typeText(name), closeSoftKeyboard());
        //onView(withId(R.id.registerEmailTxt))
        //        .perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.registerPasswordTxt))
                .perform(typeText(pass), closeSoftKeyboard());
        onView(withId(R.id.registerConfirmPassword))
          .perform(typeText(pass), closeSoftKeyboard());

        onView(withId(R.id.registerBtn)).perform(click());

        //wait for dialog to appear, then dismiss it
        onView(withText(R.string.emailNotValid)).perform(ViewActions.pressBack());
    }

    //Test that a blank password field is rejected
    @Test
    public void test05() {
        onView(withId(R.id.registerNameTxt))
                .perform(typeText(name), closeSoftKeyboard());
        onView(withId(R.id.registerEmailTxt))
                .perform(typeText(email), closeSoftKeyboard());
        //onView(withId(R.id.registerPasswordTxt))
        //        .perform(typeText(pass), closeSoftKeyboard());
        onView(withId(R.id.registerConfirmPassword))
          .perform(typeText(pass), closeSoftKeyboard());

        onView(withId(R.id.registerBtn)).perform(click());

        //wait for dialog to appear, then dismiss it
        onView(withText(R.string.passwordsDontMatch)).perform(ViewActions.pressBack());
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
        onView(withText(R.string.passwordsDontMatch)).perform(ViewActions.pressBack());
    }

    //Test that the user entered a valid email address
    @Test
    public void test07() {
        onView(withId(R.id.registerNameTxt))
                .perform(typeText(name), closeSoftKeyboard());
        onView(withId(R.id.registerEmailTxt))
                .perform(typeText(invalidEmail), closeSoftKeyboard());
        onView(withId(R.id.registerPasswordTxt))
                .perform(typeText(pass), closeSoftKeyboard());
        onView(withId(R.id.registerConfirmPassword))
          .perform(typeText(pass), closeSoftKeyboard());

        onView(withId(R.id.registerBtn)).perform(click());

        //wait for dialog to appear, then dismiss it
        String expectedMsg = getTargetContext().getString(R.string.isNotAValidEmail);
        onView(withText(containsString(expectedMsg))).perform(ViewActions.pressBack());
    }

    //test that the registration page warns if the passwords don't match
    @Test
    public void test08() {
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

    //Test that the user's password is at least 6 characters
    @Test
    public void test09() {
        onView(withId(R.id.registerNameTxt))
                .perform(typeText(name), closeSoftKeyboard());
        onView(withId(R.id.registerEmailTxt))
                .perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.registerPasswordTxt))
                .perform(typeText(shortPass), closeSoftKeyboard());
        onView(withId(R.id.registerConfirmPassword))
                .perform(typeText(shortPass), closeSoftKeyboard());

        onView(withId(R.id.registerBtn)).perform(click());

        //wait for dialog to appear, then dismiss it
        onView(withText(R.string.passwordTooShort)).perform(ViewActions.pressBack());
    }
}
