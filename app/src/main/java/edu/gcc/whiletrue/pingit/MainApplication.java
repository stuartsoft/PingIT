package edu.gcc.whiletrue.pingit;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.parse.Parse;
import com.parse.ParseUser;

import io.fabric.sdk.android.Fabric;

public class MainApplication extends Application{

    public String chatTarget = null;
    public int currentPage = 0;

    @Override public void onCreate(){
        super.onCreate();
        //LeakCanary.install(this);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);

        Fabric.with(this, new Crashlytics());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        ParseUser.logOut();//log out the user when terminating the application
    }
}
