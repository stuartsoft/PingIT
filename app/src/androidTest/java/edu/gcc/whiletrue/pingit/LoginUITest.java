package edu.gcc.whiletrue.pingit;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
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
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by BOWMANRS1 on 2/12/2016.
 */

@RunWith(AndroidJUnit4.class)
public class LoginUITest {

    String email = "unittest@gmail.com";
    String pass = "justinrocks";
    String wrongEmail = "nobodyusesthisemail@stupid.com";
    String wrongPass = "qwerty123";

    @Rule//startup activity to test
    public ActivityTestRule<StartupActivity> mActivityRule = new ActivityTestRule<StartupActivity>(
            StartupActivity.class){
        @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            Intent result = new Intent(targetContext, StartupActivity.class);
            //add extra to indicate to the intent to start on the login screen
            result.putExtra("startFragment", 1);
            return result;
        }
    };

    //successfully log in with a valid account
    @Test
    public void test11() {
        //Enter credentials and sign in

        onView(withId(R.id.loginEmailTxt))
            .perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.loginPasswordTxt))
            .perform(typeText(pass), closeSoftKeyboard());
        onView(withId(R.id.loginBtn))
            .perform(click());

        //TODO: Assert that the HomeActivity has been launched to complete the test.
        onView(withId(R.id.faq_containter)).check(matches(isDisplayed()));
        //Is this  method OK or should it not rely on the FAQ page being first?
    }

    //Test that pressing the button at the bottom of the screen switches from Login to Register
    @Test
    public void test12() {
        onView(withId(R.id.switchToRegisterBtn)).perform(click());
        onView(withId(R.id.fragment_register)).check(matches(isDisplayed()));
    }

    //test that the login page warns if the user didn't enter their email
    @Test
    public void test13(){
        //onView(withId(R.id.loginEmailTxt))
        //  .perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.loginPasswordTxt))
            .perform(typeText(pass), closeSoftKeyboard());
        onView(withId(R.id.loginBtn))
            .perform(click());

        //wait for dialog to appear, then dismiss it
        onView(withText(R.string.emailNotValid)).perform(ViewActions.pressBack());
    }

    //Test that the login page rejects a blank password field
    @Test
    public void test14() {
        onView(withId(R.id.loginEmailTxt))
            .perform(typeText(email), closeSoftKeyboard());
        //onView(withId(R.id.loginPasswordTxt))
        //    .perform(typeText(pass), closeSoftKeyboard());
        onView(withId(R.id.loginBtn))
            .perform(click());

        //wait for dialog to appear, then dismiss it
        onView(withText(R.string.passwordNotValid)).perform(ViewActions.pressBack());
    }

    //Test that the app rejects logging in with an email address not in the database
    @Test
    public void test15a() {
        onView(withId(R.id.loginEmailTxt))
            .perform(typeText(wrongEmail), closeSoftKeyboard());
        onView(withId(R.id.loginPasswordTxt))
            .perform(typeText(pass), closeSoftKeyboard());
        onView(withId(R.id.loginBtn))
            .perform(click());

        //wait for dialog to appear, then dismiss it
        onView(withText(R.string.invalidCredentials)).perform(ViewActions.pressBack());
    }

    //Test that the app rejects an incorrect password for a valid user email when logging in
    @Test
    public void test15b() {
        onView(withId(R.id.loginEmailTxt))
                .perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.loginPasswordTxt))
                .perform(typeText(wrongPass), closeSoftKeyboard());
        onView(withId(R.id.loginBtn))
                .perform(click());

        //wait for dialog to appear, then dismiss it
        onView(withText(R.string.invalidCredentials)).perform(ViewActions.pressBack());
    }
}
