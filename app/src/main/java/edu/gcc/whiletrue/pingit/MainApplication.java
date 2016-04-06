package edu.gcc.whiletrue.pingit;

import android.app.Application;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

import com.parse.Parse;
import com.parse.ParseUser;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by BOWMANRS1 on 1/23/2016.
 */
public class MainApplication extends Application{

    @Override public void onCreate(){
        super.onCreate();
        //Fabric.with(this, new Crashlytics());
        LeakCanary.install(this);

        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        ParseUser.logOut();//log out the user when terminating the application
    }
}
