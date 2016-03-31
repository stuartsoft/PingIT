package edu.gcc.whiletrue.pingit;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.StringTokenizer;

public class SettingsActivity extends AppCompatActivity {

    ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        user = ParseUser.getCurrentUser();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String switchPrefKey = getString(R.string.prefs_notification_resend_toggle_key);
        String listPrefKey = getString(R.string.prefs_notification_resend_delay_key);

        Boolean switchStateBool = preferences.getBoolean(switchPrefKey, false);

        if (switchStateBool) {
            String listStateString = preferences.getString(listPrefKey, "");

            StringTokenizer tokenizer = new StringTokenizer(listStateString);
            String resendValueString = tokenizer.nextToken();
            String resendMinutesOrHours = tokenizer.nextToken();
            int resendValueInt = Integer.parseInt(resendValueString);

            if (resendMinutesOrHours.contains("hour")) resendValueInt *= 60;

            user.put("resendDelay", resendValueInt);
            user.saveInBackground();
        }
        else {
            Log.d("Testing", "Changed switch to off");

            user.put("resendDelay", 0);
            user.saveInBackground();
        }
    }
}