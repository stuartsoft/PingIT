package edu.gcc.whiletrue.pingit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.prefs.Preferences;

import static android.support.v4.app.ActivityCompat.finishAffinity;

/**
 * A placeholder fragment containing a simple view.
 */

public class SettingsActivityFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener, View.OnClickListener {

    private Context fragmentContext;
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
            //TODO find better solution to handling a blank ringtone
        }
        catch(Exception e){}

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout)super.onCreateView(inflater, container, savedInstanceState);

        fragmentContext = inflater.getContext();

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
        TextView logoutText = (TextView)dialogView.findViewById(R.id.signInDialogText);
        logoutText.setText(R.string.signingOutDialogMsg);
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

            Intent intent = new Intent(fragmentContext, StartupActivity.class);
            //add an extra to indicate to the startup activity to show the login screen first
            intent.putExtra("startFragment", 1);
            startActivity(intent);//start the login/registration activity
            finishAffinity(getActivity());//finishes all activities in the stack
        }
    }

    /*public void onSubmitClicked(View v)
    {
        String enteredName = nameEditText.getText().toString();
        if(TextUtils.isEmpty(pass) || pass.length() < [YOUR MIN LENGTH])
        {
            passwordEditText.setError("You must have x characters in your password");
            return;
        }

        //continue processing

    }*/

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.d("Changed:", "+ onPreferenceChange(preference:" + preference + ", newValue:" + newValue + ")"); //Doesn't fire
        Boolean rv = true;
        String source = newValue.toString();
            if(source.matches("")){
                rv = false;
            }
        Log.d("Changed:", "- onPreferenceChange()");
        return rv;
    } //TODO Delete if this doesn't work, which it probably won't since Ben is an idiot

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //Update a preference's summary as soon as a user changes it
        Preference pref = findPreference(key);

        Log.d("Testing", "Changed a preference.");

        if (pref instanceof EditTextPreference) {
            Log.d("Testing", "Changed text preference.");

            EditTextPreference textPref = (EditTextPreference) pref;
            //Convert their entered text from Pref to String; trim so "    " counts as blank
            String enteredNameString = textPref.getText().trim();

            if (enteredNameString.equals("")) {
                Log.d("Testing", "Entered blank name.");

                AlertDialog.Builder builder = new AlertDialog.Builder(fragmentContext);
                builder.setTitle(R.string.app_name);
                builder.setPositiveButton(R.string.dialogConfirm, null);

                Log.d("Testing", "Passed first part.");

                builder.setMessage("A blank name is invalid. Please enter a valid name.");
                AlertDialog errorDialog = builder.create();
                errorDialog.show();

                //TODO Need to actually prevent the setting from changing, not just summary
                //Possible way: https://tinyurl.com/z8g4tke
                //Another way: https://tinyurl.com/hvaewqb
            }

            else pref.setSummary(textPref.getText()); //Name is okay; update it
        }

        else if (pref instanceof RingtonePreference) {
            Uri ringtoneUri = Uri.parse(sharedPreferences.getString(key, ""));
            Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), ringtoneUri);
            String name = ringtone.getTitle(getActivity());

            RingtonePreference ringtonePref = (RingtonePreference) findPreference(key);
            ringtonePref.setSummary(name);
        }
    }
}