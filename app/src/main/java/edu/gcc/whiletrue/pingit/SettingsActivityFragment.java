package edu.gcc.whiletrue.pingit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.RingtonePreference;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import static android.support.v4.app.ActivityCompat.finishAffinity;

/**
 * A placeholder fragment containing a simple view.
 */

public class SettingsActivityFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener, View.OnClickListener {

    public SettingsActivityFragment() {
    }

    @Override
    public void onStart() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        // Set the summary of the Name preference to the user's friendly name.
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();

        EditTextPreference editTextPref = (EditTextPreference) findPreference("display_name");
        editTextPref.setSummary(sp.getString("display_name", ""));

        try{
        // Set the summary of the Notification Sound preference to the tone's friendly name.
        Uri ringtoneUri = Uri.parse(sp.getString("notification_sound_preference", ""));
        Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), ringtoneUri);
        String name = ringtone.getTitle(getActivity());

        RingtonePreference ringtonePref = (RingtonePreference)
                findPreference("notification_sound_preference");
        ringtonePref.setSummary(name);
        }
        catch(Exception e){}

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout)super.onCreateView(inflater, container, savedInstanceState);

        //append the footerview below the settings, like the logout button
        FrameLayout footerView = (FrameLayout)inflater.inflate(R.layout.footer_settings, null);
        Button logoutBtn = (Button)footerView.findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(this);
        view.addView(footerView);//add footer to the linearlayout hierarchy

        return view;
    }

    @Override
    public void onClick(View v) {
        final View view;
        view = v;//final reference to the view that called onClick

        switch (v.getId()){
            case R.id.logoutBtn:
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null){
                            Intent intent = new Intent(view.getContext(), StartupActivity.class);
                            startActivity(intent);//start the login/registration activity
                            finishAffinity(getActivity());//finishes all activities in the stack
                        }
                        else{
                            Toast.makeText(view.getContext().getApplicationContext(),
                                    "Error (" + e.getMessage() + ")", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            default:
                break;
        }
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);

        Log.w(getString(R.string.log_warning), "onSharedPreferenceChanged: Any change!");

        if (pref instanceof EditTextPreference) {
            EditTextPreference textPref = (EditTextPreference) pref;
            pref.setSummary(textPref.getText());
        }

        else if (pref instanceof RingtonePreference) {
            Log.w(getString(R.string.log_warning), "onSharedPreferenceChanged: Tone change!");
            Uri ringtoneUri = Uri.parse(sharedPreferences.getString(key, ""));
            Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), ringtoneUri);
            String name = ringtone.getTitle(getActivity());

            RingtonePreference ringtonePref = (RingtonePreference) findPreference(key);
            ringtonePref.setSummary(name);
        }
    }
}