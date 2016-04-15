package edu.gcc.whiletrue.pingit;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class PingsUITest {
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

    //Switch to the FAQ page from the Pings page
    @Test
    public void test28() {
        LoginUITest.loginTestUser();

        //tap Pings tab
        onView(withText(R.string.pingsSectionTitle)).perform(click());
        //assert that we are on the Pings tab
        onView(withId(R.id.fragment_pings_page)).check(matches(isDisplayed()));

        //tap FAQ tab
        onView(withText(R.string.faqSectionTitle)).perform(click());
        //assert that we are on the FAQ tab
        onView(withId(R.id.faq_container)).check(matches(isDisplayed()));

        LoginUITest.settingsAndLogout();
    }

    //Switch to the Chat page from the Pings page
    @Test
    public void test29() {
        LoginUITest.loginTestUser();

        //tap Pings tab
        onView(withText(R.string.pingsSectionTitle)).perform(click());
        //assert that we are on the Pings tab
        onView(withId(R.id.fragment_pings_page)).check(matches(isDisplayed()));

        //tap Chat tab
        onView(withText(R.string.chatSectionTitle)).perform(click());
        //assert that we are on the Pings tab
        onView(withId(R.id.fragment_chat_page)).check(matches(isDisplayed()));

        LoginUITest.settingsAndLogout();

    }

    //Open the Settings menu from the Pings page
    @Test
    public void test30(){
        LoginUITest.loginTestUser();

        //tap Pings tab
        onView(withText(R.string.pingsSectionTitle)).perform(click());
        //assert that we are on the Pings tab
        onView(withId(R.id.fragment_pings_page)).check(matches(isDisplayed()));

        //tap the Settings icon
        //openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withId(R.id.menu_home_settings)).perform(click());

        //assert that we are on the Settings page
        onView(withId(R.id.settingsFragmentContainer)).check(matches(isDisplayed()));

        //press logout before next test
        onView(withId(R.id.logoutBtn)).perform(click());
        onView(withText(R.string.dialogYes)).perform(click());//click yes to logout
    }
}