package edu.gcc.whiletrue.pingit;

import android.support.test.runner.AndroidJUnit4;
import com.parse.ParseUser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ParseInstTest {

    String name = "John Doe";
    String email = "unittest@gmail.com";
    String pass = "justinrocks";

    @Before
    public void onBefore(){
        /*Don't need to instantiate parse. Since this is an instrumented test, parse initialization
        will be handled by the regular MainApplication class*/
    }

    @Test
    public void testLogin() throws Exception{
        ParseUser.logOut();//log out first to make sure no one is logged in
        ParseUser.logIn(email, pass);
        ParseUser.logOut();//log out the user when terminating the application

    }

}
