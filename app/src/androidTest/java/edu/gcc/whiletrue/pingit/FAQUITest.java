package edu.gcc.whiletrue.pingit;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by stuart on 2/20/16.
 */

@RunWith(AndroidJUnit4.class)
public class FAQUITest {
    @Rule//startup activity to test
    public ActivityTestRule<HomeActivity> mActivityRule = new ActivityTestRule<>(
            HomeActivity.class);

    //Open the Settings menu from the FAQ page
    @Test
    public void test25(){
        //tap FAQ tab
        onView(withText(R.string.faqSectionTitle)).perform(click());
        //assert that we are on the FAQ tab
        onView(withId(R.id.fragment_faqpage)).check(matches(isDisplayed()));

        //tap the Settings icon
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Settings"))
            .perform(click());

        //assert that we are on the Settings page
        onView(withId(R.id.settingsFragmentContainer)).check(matches(isDisplayed()));
    }

    //Check that pressing the up button on the settings page brings us back to the previous tab on the home screen
    @Test
    public void test55(){
        //tap FAQ tab
        onView(withText(R.string.faqSectionTitle)).perform(click());
        //assert that we are on the FAQ tab
        onView(withId(R.id.fragment_faqpage)).check(matches(isDisplayed()));
        //tap the Settings icon
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Settings"))
                .perform(click());
        //assert that we are on the Settings page
        onView(withId(R.id.settingsFragmentContainer)).check(matches(isDisplayed()));

        onView(withContentDescription("Navigate up")).perform(click());
        //assert that we are on the FAQ tab
        onView(withId(R.id.fragment_faqpage)).check(matches(isDisplayed()));
    }

    //go from faq page to pings page
    @Test
    public void test27(){
        //tap FAQ tab
        onView(withText(R.string.faqSectionTitle)).perform(click());
        //assert that we are on the FAQ tab
        onView(withId(R.id.fragment_faqpage)).check(matches(isDisplayed()));
        //tap Pings tab
        onView(withText(R.string.pingsSectionTitle)).perform(click());
        //assert that we are on the Pings tab
        onView(withId(R.id.fragment_pings_page)).check(matches(isDisplayed()));

    }
}
