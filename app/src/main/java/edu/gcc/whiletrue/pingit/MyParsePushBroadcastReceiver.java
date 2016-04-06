package edu.gcc.whiletrue.pingit;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;

import com.parse.ParsePushBroadcastReceiver;

/**
 * Created by STEGNERBT1 on 3/31/2016.
 */
public class MyParsePushBroadcastReceiver extends ParsePushBroadcastReceiver {

    @Override
    protected Notification getNotification(Context context, Intent intent) {
        //Deactivate standard notification
        return null;
    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        //Implement
        //TODO I think this needs to open the app because right now tapping notification does nothing
    }

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        //Get user's preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String ringtonePreferenceString =
                preferences.getString("notification_sound_preference", "DEFAULT"); //Does this need to be handled in some way?

        //Build a new notification
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle("Testing Title"); //This should probably be imported from Parse
        builder.setContentText("Testing Text"); //Same as Title; omitting it just leaves it blank
        builder.setSmallIcon(R.raw.testicon); //This should be changed to our icon obviously, can then remove this from raw folder

        //Create soundUri and set sound:
        builder.setSound(Uri.parse(ringtonePreferenceString));

        notificationManager.notify("MyTag", 0, builder.build()); //I'm not sure what this actually does
    }
}