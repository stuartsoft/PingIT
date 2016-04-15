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
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class FAQUITest {

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

    //Open the Settings menu from the FAQ page
    @Test
    public void test25(){
        LoginUITest.loginTestUser();
        //tap FAQ tab
        onView(withText(R.string.faqSectionTitle)).perform(click());
        //assert that we are on the FAQ tab
        onView(withId(R.id.faq_container)).check(matches(isDisplayed()));

        //tap the Settings icon
        //openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withId(R.id.menu_home_settings)).perform(click());

        //assert that we are on the Settings page
        onView(withId(R.id.settingsFragmentContainer)).check(matches(isDisplayed()));

        //press logout before next test
        onView(withId(R.id.logoutBtn)).perform(click());
        onView(withText(R.string.dialogYes)).perform(click());//click yes to logout

    }

    //Switch to the Chat page from the FAQ page
    @Test
    public void test26() {
        LoginUITest.loginTestUser();

        //tap FAQ tab
        onView(withText(R.string.faqSectionTitle)).perform(click());
        //assert that we are on the FAQ tab
        onView(withId(R.id.faq_container)).check(matches(isDisplayed()));

        //tap Chat tab
        onView(withText(R.string.chatSectionTitle)).perform(click());
        //assert that we are on the Pings tab
        onView(withId(R.id.fragment_chat_page)).check(matches(isDisplayed()));

        LoginUITest.settingsAndLogout();

    }

    //Switch to the Pings page from the FAQ page
    @Test
    public void test27(){
        LoginUITest.loginTestUser();

        //tap FAQ tab
        onView(withText(R.string.faqSectionTitle)).perform(click());
        //assert that we are on the FAQ tab
        onView(withId(R.id.faq_container)).check(matches(isDisplayed()));

        //tap Pings tab
        onView(withText(R.string.pingsSectionTitle)).perform(click());
        //assert that we are on the Pings tab
        onView(withId(R.id.fragment_pings_page)).check(matches(isDisplayed()));

        LoginUITest.settingsAndLogout();

    }

    //Test that pressing the software Back button brings us back to the FAQ, Chat, or Pings page
    @Test
    public void test54() {
        LoginUITest.loginTestUser();

        //tap FAQ tab
        onView(withText(R.string.faqSectionTitle)).perform(click());
        //assert that we are on the FAQ tab
        onView(withId(R.id.faq_container)).check(matches(isDisplayed()));

        //tap the Settings icon
        //openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withId(R.id.menu_home_settings)).perform(click());

        //assert that we are on the Settings page
        onView(withId(R.id.settingsFragmentContainer)).check(matches(isDisplayed()));

        //Press the software or hardware Back button
        onView(withId(R.id.settingsFragmentContainer)).perform(ViewActions.pressBack());
        //assert that we are on the FAQ tab
        onView(withId(R.id.faq_container)).check(matches(isDisplayed()));

        LoginUITest.settingsAndLogout();

    }

    //Check that pressing the on-screen Up button on the settings page
    //brings us back to the previous tab on the home screen
    @Test
    public void test55(){
        LoginUITest.loginTestUser();

        //tap FAQ tab
        onView(withText(R.string.faqSectionTitle)).perform(click());
        //assert that we are on the FAQ tab
        onView(withId(R.id.faq_container)).check(matches(isDisplayed()));

        //tap the Settings icon
        //openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withId(R.id.menu_home_settings)).perform(click());

        //assert that we are on the Settings page
        onView(withId(R.id.settingsFragmentContainer)).check(matches(isDisplayed()));

        onView(withContentDescription("Navigate up")).perform(click());
        //assert that we are on the FAQ tab
        onView(withId(R.id.faq_container)).check(matches(isDisplayed()));

        LoginUITest.settingsAndLogout();

    }
}
