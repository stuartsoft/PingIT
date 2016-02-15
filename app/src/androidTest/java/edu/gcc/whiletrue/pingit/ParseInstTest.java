package edu.gcc.whiletrue.pingit;

import android.support.test.runner.AndroidJUnit4;
import com.parse.ParseUser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by BOWMANRS1 on 2/15/2016.
 */

@RunWith(AndroidJUnit4.class)
public class ParseInstTest {

    String name = "John Doe";
    String email = "unittest@gmail.com";
    String pass = "justinrocks";

    @Test
    public void testLogin() throws Exception{
        ParseUser.logOut();//log out first to make sure no one is logged in
        ParseUser.logIn(email, pass);
        ParseUser.logOut();//log out the user when terminating the application

    }

}
