package edu.gcc.whiletrue.pingit;

import android.app.Notification;
import android.content.ContentResolver;
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

        Uri soundURI = Uri.parse("android.resource://" + context.getPackageName() + "/" +  R.raw.testsound);
        Log.d("Testing", "Sound URI is " + soundURI);
        Notification n = super.getNotification(context, intent);
        //n.sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.testsound);
        n.sound = soundURI;

        n.color = 0xFF0000FF;

        //n.sound = Uri.parse("content://media/internal/audio/media/21");

        Log.d("Testing", "N equals " + n);
        Log.d("Testing", "N sound equals " + n.sound);

        return n;
    }
}