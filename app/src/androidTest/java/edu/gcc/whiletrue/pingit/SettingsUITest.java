package edu.gcc.whiletrue.pingit;

import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.matcher.PreferenceMatchers.withKey;
import static android.support.test.espresso.matcher.PreferenceMatchers.withTitleText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagKey;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by BOWMANRS1 on 2/23/2016.
 */

@RunWith(AndroidJUnit4.class)
public class SettingsUITest {


    @Rule//startup activity to test
    public ActivityTestRule<SettingsActivity> mActivityRule = new ActivityTestRule<>(
            SettingsActivity.class);

    //just test that the dialog box appears to change the display name. Then cancel the dialog
    @Test
    public void test46(){
        onData(withKey("display_name")).perform(click());
        //type a name
        //onView(withText("Display Name")).perform(typeTextIntoFocusedView("Stuart Bowman"), closeSoftKeyboard());

        //wait for dialog to appear, then dismiss it
        pressBack();
    }

    //tap logout button and make sure confirmation dialog appears
    @Test
    public void test51() {
        onView(withId(R.id.logoutBtn)).perform(click());
        //wait for dialog to appear, then dismiss it
        onView(withText(R.string.confirmLogoutMsg)).perform(ViewActions.pressBack());
    }

}
