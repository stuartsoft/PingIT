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
                preferences.getString("notification_sound_preference", "DEFAULT");

        try {
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            String msg = json.getString("alert");

            //Build a new notification
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setContentTitle("Ping.IT");
            builder.setContentText(msg);
            builder.setSmallIcon(R.raw.white_logo);
            builder.setColor(0xF44336); //ping.it red

            Intent cIntent = new Intent(context, HomeActivity.class);
            cIntent.putExtra("pingsFragment", true);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, cIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);

            //Create soundUri and set sound:
            builder.setSound(Uri.parse(ringtonePreferenceString));

            notificationManager.notify("Ping.IT", 0 , builder.build());

        } catch (JSONException e) {
            Log.d("failedPush", "JSONException: " + e.getMessage());
        }


    }
}