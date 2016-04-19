package edu.gcc.whiletrue.pingit;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class LoginUITest {

    String email = "unittest@gcc.edu";
    String pass = "justinrocks123";
    String wrongEmail = "nobodyusesthisemail@gcc.edu";
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

    //publically available static method for tapping the settings page and logging out between tests
    public static void settingsAndLogout(){
        //tap the Settings icon
        //openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withId(R.id.menu_home_settings)).perform(click());

        //assert that we are on the Settings page
        onView(withId(R.id.settingsFragmentContainer)).check(matches(isDisplayed()));

        onView(withId(R.id.logoutBtn)).perform(click());
        onView(withText(R.string.dialogYes)).perform(click());//click yes to logout
    }

    //publicly available static method for logging in before tests that require
    // a valid parse user to run
    public static void loginTestUser(){
        //Enter credentials and sign in
        String email = "unittest@gcc.edu";
        String pass = "justinrocks123";

        onView(withId(R.id.loginEmailTxt))
                .perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.loginPasswordTxt))
                .perform(typeText(pass), closeSoftKeyboard());
        onView(withId(R.id.loginBtn))
                .perform(click());

        onView(withId(R.id.faq_container)).check(matches(isDisplayed()));
    }

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

        onView(withId(R.id.faq_container)).check(matches(isDisplayed()));
        settingsAndLogout();
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
