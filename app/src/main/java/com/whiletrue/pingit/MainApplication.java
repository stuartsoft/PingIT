package com.whiletrue.pingit;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by BOWMANRS1 on 1/23/2016.
 */
public class MainApplication extends Application{

    @Override public void onCreate(){
        super.onCreate();
        LeakCanary.install(this);
    }

}
