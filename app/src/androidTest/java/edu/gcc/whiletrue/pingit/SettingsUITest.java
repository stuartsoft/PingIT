package edu.gcc.whiletrue.pingit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.PreferenceMatchers.withKey;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class SettingsUITest {

    String enteredName = "Stuart Bowman";
    String prefsToggleKey = getTargetContext().getString(R.string.prefs_notification_resend_toggle_key);
    String prefsDelayKey = getTargetContext().getString(R.string.prefs_notification_resend_delay_key);
    String displayNameKey = getTargetContext().getString(R.string.prefs_display_name_key);
    String clearPingsKey = getTargetContext().getString(R.string.prefs_clear_pings_key);

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

    private void openSettings(){
        //tap the Settings icon
        onView(withId(R.id.menu_home_settings)).perform(click());

        //assert that we are on the Settings page
        onView(withId(R.id.settingsFragmentContainer)).check(matches(isDisplayed()));
    }


    //Ensure the switch moves from off to on when tapped. Switch starts off.
    @Test
    public void test42() {
        LoginUITest.loginTestUser();
        openSettings();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getTargetContext());

        Boolean notificationResendOn = settings.getBoolean(prefsToggleKey, false);
        if (notificationResendOn)//if the switch is on then turn it off
            onData(withKey(prefsToggleKey)).perform(click());

        //switch is now OFF
        //assert the settings value matches the expected value
        assertEquals(false, settings.getBoolean(prefsToggleKey, false));
        //assert that the correct message is displayed in the row
        onView(withText(R.string.prefs_notification_resend_toggle_summaryoff)).check(matches(isDisplayed()));

        //test turning ON the switch
        onData(withKey(prefsToggleKey)).perform(click());
        //assert the value changed in the preferences
        assertEquals(true, settings.getBoolean(prefsToggleKey, false));
        //assert that the correct message is displayed in the row
        onView(withText(R.string.prefs_notification_resend_toggle_summaryon)).check(matches(isDisplayed()));

        //press logout before next test
        onView(withId(R.id.logoutBtn)).perform(click());
        onView(withText(R.string.dialogYes)).perform(click());//click yes to logout
    }

    //Ensure the switch moves from on to off when tapped. Switch starts on.
    @Test
    public void test43() {
        LoginUITest.loginTestUser();
        openSettings();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getTargetContext());

        Boolean notificationResendOn = settings.getBoolean(prefsToggleKey, false);
        if (!notificationResendOn)//if the switch is off then turn it on
            onData(withKey(prefsToggleKey)).perform(click());

        //switch is now ON
        //assert the settings value matches the expected value
        assertEquals(true, settings.getBoolean(prefsToggleKey, false));
        //assert that the correct message is displayed in the row
        onView(withText(R.string.prefs_notification_resend_toggle_summaryon)).check(matches(isDisplayed()));

        //test turning OFF the switch
        onData(withKey(prefsToggleKey)).perform(click());
        //assert the value changed in the preferences
        assertEquals(false, settings.getBoolean(prefsToggleKey, false));
        //assert that the correct message is displayed in the row
        onView(withText(R.string.prefs_notification_resend_toggle_summaryoff)).check(matches(isDisplayed()));

        //press logout before next test
        onView(withId(R.id.logoutBtn)).perform(click());
        onView(withText(R.string.dialogYes)).perform(click());//click yes to logout
    }

    //Test that tapping Notification Resend Delay launches a dialog box
    @Test
    public void test44() {
        LoginUITest.loginTestUser();
        openSettings();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getTargetContext());

        Boolean notificationResendOn = settings.getBoolean(prefsToggleKey, false);
        if (!notificationResendOn)//if notification resend delay is off, turn it on so we can set the delay
            onData(withKey(prefsToggleKey)).perform(click());

        onData(withKey(prefsDelayKey)).perform(click());//open delay setting

        //wait for dialog to appear, then dismiss it
        onView(withText(R.string.prefs_notification_resend_delay_title)).perform(ViewActions.pressBack());

        //press logout before next test
        onView(withId(R.id.logoutBtn)).perform(click());
        onView(withText(R.string.dialogYes)).perform(click());//click yes to logout

    }

    //select an option from the list of notification resend delays
    @Test
    public void test45(){
        LoginUITest.loginTestUser();
        openSettings();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getTargetContext());

        Boolean notificationResendOn = settings.getBoolean(prefsToggleKey, false);
        if (!notificationResendOn)//if notification resend delay is off, turn it on so we can set the delay
            onData(withKey(prefsToggleKey)).perform(click());

        onData(withKey(prefsDelayKey)).perform(click());//open delay setting

        //select the first time delay option
        String selectedOption = "1 minute";
        onView(withText(selectedOption)).perform(click());

        //assert that we are on the settings page
        onView(withId(R.id.settingsFragmentContainer)).check(matches(isDisplayed()));
        //assert that the selected option is displayed
        onView(withText(selectedOption)).check(matches(isDisplayed()));
        //assert that the saved preferences value matches the UI selection
        assertEquals(selectedOption, settings.getString(prefsDelayKey, ""));

        //press logout before next test
        onView(withId(R.id.logoutBtn)).perform(click());
        onView(withText(R.string.dialogYes)).perform(click());//click yes to logout
    }


    //just test that the dialog box appears to change the display name. Then cancel the dialog
    @Test
    public void test46(){
        LoginUITest.loginTestUser();
        openSettings();

        onData(withKey(displayNameKey)).perform(click());
        //type a name
        //onView(withText("Display Name")).perform(typeTextIntoFocusedView("Stuart Bowman"), closeSoftKeyboard());

        //wait for dialog to appear, then dismiss it
        onView(withText("Display Name")).perform(closeSoftKeyboard());//close keyboard
        pressBack();//dismiss dialog

        //assert that we are on the Settings page
        onView(withId(R.id.settingsFragmentContainer)).check(matches(isDisplayed()));

        //press logout before next test
        onView(withId(R.id.logoutBtn)).perform(click());
        onView(withText(R.string.dialogYes)).perform(click());//click yes to logout

    }

    //Test that tapping Clear Pings brings up a dialog box
    @Test
    public void test48() {
        LoginUITest.loginTestUser();
        openSettings();

        onData(withKey(clearPingsKey)).perform(click());
        //wait for dialog to appear, then dismiss it
        onView(withText(R.string.prefs_clear_pings_dialogtitle)).perform(ViewActions.pressBack());

        //press logout before next test
        onView(withId(R.id.logoutBtn)).perform(click());
        onView(withText(R.string.dialogYes)).perform(click());//click yes to logout

    }

    //clear all pings and then check that they're actually displaying as cleared on pings page
    @Test
    public void test49(){
        LoginUITest.loginTestUser();

        //go to pings page
        onView(withText(R.string.pingsSectionTitle)).perform(click());

        openSettings();

        onData(withKey(clearPingsKey)).perform(click());
        //wait for dialog to appear, then cancel.
        onView(withText(R.string.prefs_clear_pings_positive)).perform(click());

        //check that toast comes up
        //onView(withText(R.string.PingsCleared)).check(matches(isDisplayed()));

        pressBack();//go back to homeActivity

        //pings page should now be reloading or say it has no pings
        try {
            onView(withText(R.string.noPings)).check(matches(isDisplayed()));
        }catch(Exception e){
            //otherwise, try this
            onView(withText(R.string.loadingPings)).check(matches(isDisplayed()));
        }

        openSettings();

        //press logout before next test
        onView(withId(R.id.logoutBtn)).perform(click());
        onView(withText(R.string.dialogYes)).perform(click());//click yes to logout
    }

    //Test that the user can cancel clearing his pings
    @Test
    public void test50() {
        LoginUITest.loginTestUser();
        openSettings();

        onData(withKey(clearPingsKey)).perform(click());
        //wait for dialog to appear, then cancel.
        onView(withText(R.string.dialogNo)).perform(click());

        //press logout before next test
        onView(withId(R.id.logoutBtn)).perform(click());
        onView(withText(R.string.dialogYes)).perform(click());//click yes to logout

    }

    //tap logout button and make sure confirmation dialog appears
    @Test
    public void test51() {
        LoginUITest.loginTestUser();
        openSettings();

        onView(withId(R.id.logoutBtn)).perform(click());
        //wait for dialog to appear, then dismiss it
        onView(withText(R.string.confirmLogoutMsg)).perform(ViewActions.pressBack());

        //press logout before next test
        onView(withId(R.id.logoutBtn)).perform(click());
        onView(withText(R.string.dialogYes)).perform(click());//click yes to logout

    }

    //Tap Log Out button, confirm logout, and make sure the user is returned to the Login screen
    @Test
    public void test52() {
        LoginUITest.loginTestUser();
        openSettings();

        onView(withId(R.id.logoutBtn)).perform(click());
        //wait for dialog to appear, then confirm logout
        onView(withText(R.string.dialogYes)).perform(click());

        onView(withId(R.id.fragment_login)).check(matches(isDisplayed()));

    }

    //Tap Log Out button, but cancel the action by tapping No
    @Test
    public void test53() {
        LoginUITest.loginTestUser();
        openSettings();

        onView(withId(R.id.logoutBtn)).perform(click());
        //wait for dialog to appear, then cancel logout action
        onView(withText(R.string.dialogNo)).perform(click());

        onView(withId(R.id.settingsFragmentContainer)).check(matches(isDisplayed()));

        //press logout before next test
        onView(withId(R.id.logoutBtn)).perform(click());
        onView(withText(R.string.dialogYes)).perform(click());//click yes to logout

    }

}
