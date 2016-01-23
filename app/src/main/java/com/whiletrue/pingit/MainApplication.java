package com.whiletrue.pingit;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * Created by BOWMANRS1 on 1/23/2016.
 */
public class MainApplication extends Application{

    @Override public void onCreate(){
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        LeakCanary.install(this);
        throw new RuntimeException("This is a crash");
    }

}
