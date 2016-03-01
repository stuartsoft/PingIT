package edu.gcc.whiletrue.pingit;

import android.app.Application;

import com.parse.ParseUser;
import com.squareup.leakcanary.LeakCanary;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * Created by BOWMANRS1 on 1/23/2016.
 */
public class MainApplication extends Application{

    @Override public void onCreate(){
        super.onCreate();
        //Fabric.with(this, new Crashlytics());
        //LeakCanary.install(this);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        ParseUser.logOut();//log out the user when terminating the application
    }
}
