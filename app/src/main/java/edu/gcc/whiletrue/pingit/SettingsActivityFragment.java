package edu.gcc.whiletrue.pingit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.TextView;
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

    private SignOutTask signOutTask;
    private AlertDialog confirmSignOutDialog;
    private AlertDialog signOutDialog;

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

        //create the signout dialog to display while the signout thread is running later
        AlertDialog.Builder builder = new AlertDialog.Builder(inflater.getContext());
        builder.setTitle(R.string.app_name);
        LinearLayout dialogView = (LinearLayout)inflater.inflate(R.layout.dialog_signin, null);
        //change dialog message for logout...
        TextView t = (TextView)dialogView.findViewById(R.id.signInDialogText);
        t.setText(R.string.signingOutDialogMsg);
        builder.setView(dialogView);//assign the modified view to the alert dialog
        builder.setNegativeButton(R.string.dialogCancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                signOutTask.cancel(true);//cancel the signout background thread
            }
        });
        signOutDialog = builder.create();//finalize and create the alert dialog for use later

        //create signout confirmation dialog for use later
        builder = new AlertDialog.Builder(inflater.getContext());
        builder.setTitle(R.string.app_name);
        builder.setMessage(R.string.confirmLogoutMsg);
        builder.setPositiveButton(R.string.dialogYes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //show signout dialog with progress spinner while Parse executes in the background
                signOutDialog.show();
                signOutTask = new SignOutTask();
                signOutTask.execute();//attempt to signout in the background
            }
        });
        builder.setNegativeButton(R.string.dialogNo, null);
        confirmSignOutDialog = builder.create();

        return view;
    }

    @Override
    public void onClick(View v) {
        final View view;
        view = v;//final reference to the view that called onClick

        switch (v.getId()){
            case R.id.logoutBtn:
                confirmSignOutDialog.show();
                break;
            default:
                break;
        }
    }

    private class SignOutTask extends AsyncTask<String, Void, Integer>{
        @Override
        protected Integer doInBackground(String... params) {
            ParseUser.logOut();
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            Intent intent = new Intent(getContext(), StartupActivity.class);
            //add an extra to indicate to the startup activity to show the login screen first
            intent.putExtra("startFragment", 1);
            startActivity(intent);//start the login/registration activity
            finishAffinity(getActivity());//finishes all activities in the stack
        }
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);

        //Log.w(getString(R.string.log_warning), "onSharedPreferenceChanged: Any change!");

        if (pref instanceof EditTextPreference) {
            EditTextPreference textPref = (EditTextPreference) pref;
            pref.setSummary(textPref.getText());
        }

        else if (pref instanceof RingtonePreference) {
            //Log.w(getString(R.string.log_warning), "onSharedPreferenceChanged: Tone change!");
            Uri ringtoneUri = Uri.parse(sharedPreferences.getString(key, ""));
            Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), ringtoneUri);
            String name = ringtone.getTitle(getActivity());

            RingtonePreference ringtonePref = (RingtonePreference) findPreference(key);
            ringtonePref.setSummary(name);
        }
    }
}