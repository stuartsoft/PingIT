package edu.gcc.whiletrue.pingit;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

/**
 * Created by STEGNERBT1 on 3/31/2016.
 */
public class MyParsePushBroadcastReceiver extends ParsePushBroadcastReceiver {

    @Override
    protected Notification getNotification(Context context, Intent intent) {
        Log.d("Testing", "Fired notification class");

        Notification n = super.getNotification(context, intent);
        n.sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + "R.raw.testsound.mp3");
        Log.d("Testing", "N equals " + n);
        return n;
    }
}