package edu.gcc.whiletrue.pingit;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseUser;

public class MainApplication extends Application{

    public String chatTarget = null;
    public int currentPage = 0;

    @Override public void onCreate(){
        super.onCreate();
        //Fabric.with(this, new Crashlytics());
        //LeakCanary.install(this);

        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        ParseUser.logOut();//log out the user when terminating the application
    }
}
