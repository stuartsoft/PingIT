package edu.gcc.whiletrue.pingit;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

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
    protected void onPushReceive(Context context, Intent intent) {
        //Get user's preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String ringtonePreferenceString =
                preferences.getString("notification_sound_preference", "DEFAULT"); //Does this need to be handled in some way?

        try {
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            String msg = json.getString("alert");
            //String title = json.getString("title");


            //Build a new notification
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setContentTitle("Ping.It"); //This should probably be imported from Parse
            builder.setContentText(msg); //Same as Title; omitting it just leaves it blank
            builder.setSmallIcon(R.raw.white_logo); //This should be changed to our icon obviously, can then remove this from raw folder
            builder.setColor(0xF44336); //ping.it red

            Intent cIntent = new Intent(context, HomeActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, cIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);

            //Create soundUri and set sound:
            builder.setSound(Uri.parse(ringtonePreferenceString));

            notificationManager.notify("PingIt", 0 , builder.build()); //I'm not sure what this actually does

        } catch (JSONException e) {
            Log.d("failedPush", "JSONException: " + e.getMessage());
        }


    }
}