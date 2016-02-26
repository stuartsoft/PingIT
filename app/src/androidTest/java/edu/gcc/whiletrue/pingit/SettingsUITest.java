package edu.gcc.whiletrue.pingit;

import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.Matcher;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.PreferenceMatchers.isEnabled;
import static android.support.test.espresso.matcher.PreferenceMatchers.withKey;
import static android.support.test.espresso.matcher.PreferenceMatchers.withSummaryText;
import static android.support.test.espresso.matcher.PreferenceMatchers.withTitleText;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagKey;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

/**
 * Created by BOWMANRS1 on 2/23/2016.
 */

@RunWith(AndroidJUnit4.class)
public class SettingsUITest {

    String enteredName = "Stuart Bowman";
    String prefsToggleKey = getTargetContext().getString(R.string.prefs_notification_resend_toggle_key);
    String prefsDelayKey = getTargetContext().getString(R.string.prefs_notification_resend_delay_key);
    String displayNameKey = getTargetContext().getString(R.string.prefs_display_name_key);
    String clearPingsKey = getTargetContext().getString(R.string.prefs_clear_pings_key);

    @Rule//startup activity to test
    public ActivityTestRule<SettingsActivity> mActivityRule = new ActivityTestRule<>(
            SettingsActivity.class);

    //Test that a user can open the Ringtone dialogue option in the Settings menu
    /*@Test
    public void test40() {
        onData(withKey("notification_sound_preference")).perform(click());

        //wait for dialog to appear, then dismiss it
        pressBack();
        //TODO: Fix this; test freezes once dialogue is opened
    }*/

    //Ensure the switch moves from off to on when tapped. Switch starts off.
    @Test
    public void test42() {
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

    }

    //Ensure the switch moves from on to off when tapped. Switch starts on.
    @Test
    public void test43() {
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
    }

    //Test that tapping Notification Resend Delay launches a dialog box
    @Test
    public void test44() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getTargetContext());

        Boolean notificationResendOn = settings.getBoolean(prefsToggleKey, false);
        if (!notificationResendOn)//if notification resend delay is off, turn it on so we can set the delay
            onData(withKey(prefsToggleKey)).perform(click());

        onData(withKey(prefsDelayKey)).perform(click());//open delay setting

        //wait for dialog to appear, then dismiss it
        onView(withText(R.string.prefs_notification_resend_delay_title)).perform(ViewActions.pressBack());
    }

    //select an option from the list of notification resend delays
    @Test
    public void test45(){
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
        assertEquals(selectedOption,settings.getString(prefsDelayKey, ""));
    }

    //just test that the dialog box appears to change the display name. Then cancel the dialog
    @Test
    public void test46(){
        onData(withKey(displayNameKey)).perform(click());
        //type a name
        //onView(withText("Display Name")).perform(typeTextIntoFocusedView("Stuart Bowman"), closeSoftKeyboard());

        //wait for dialog to appear, then dismiss it
        pressBack();
        //TODO: Ensure this is a thorough enough test. This also hangs quite a bit
    }

    //Open the Name box, type a new name, and hit Okay. The new name should be displayed.
    /*@Test
    public void test47(){
        onData(withKey("display_name")).perform(click());
        //type a name
        onView(withText("Display Name")).perform(typeTextIntoFocusedView(enteredName),
                closeSoftKeyboard());
        //Submit the name
        onView(withText("OK")).perform(click());

        //onData(withKey("display_name")).check(matches(isDisplayed()));
        onData(withSummaryText(enteredName)).check(matches(isDisplayed()));

        //TODO: Fix this
    }*/

    //Test that tapping Clear Pings brings up a dialog box
    @Test
    public void test48() {
        onData(withKey(clearPingsKey)).perform(click());
        //wait for dialog to appear, then dismiss it
        onView(withText(R.string.prefs_clear_pings_dialogtitle)).perform(ViewActions.pressBack());
    }

    //Test that the user can cancel clearing his pings
    @Test
    public void test50() {
        onData(withKey(clearPingsKey)).perform(click());
        //wait for dialog to appear, then cancel.
        onView(withText(R.string.dialogNo)).perform(click());
    }

    //tap logout button and make sure confirmation dialog appears
    @Test
    public void test51() {
        onView(withId(R.id.logoutBtn)).perform(click());
        //wait for dialog to appear, then dismiss it
        onView(withText(R.string.confirmLogoutMsg)).perform(ViewActions.pressBack());
    }

    //Tap Log Out button, confirm logout, and make sure the user is returned to the Login screen
    @Test
    public void test52() {
        onView(withId(R.id.logoutBtn)).perform(click());
        //wait for dialog to appear, then confirm logout
        onView(withText(R.string.dialogYes)).perform(click());

        onView(withId(R.id.fragment_login)).check(matches(isDisplayed()));
    }

    //Tap Log Out button, but cancel the action by tapping No
    @Test
    public void test53() {
        onView(withId(R.id.logoutBtn)).perform(click());
        //wait for dialog to appear, then cancel logout action
        onView(withText(R.string.dialogNo)).perform(click());

        onView(withId(R.id.settingsFragmentContainer)).check(matches(isDisplayed()));
    }
}
