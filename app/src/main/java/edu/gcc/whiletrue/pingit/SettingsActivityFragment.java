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
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.InputFilter;
import android.preference.SwitchPreference;
import android.support.v7.app.NotificationCompat;
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
import android.widget.Toast;

import com.parse.Parse;
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

        String dispNameKey = getString(R.string.prefs_display_name_key);
        EditTextPreference editTextPref = (EditTextPreference) findPreference(dispNameKey);
        editTextPref.setSummary(sp.getString(dispNameKey, ""));

        try{
        // Set the summary of the Notification Sound preference to the tone's friendly name.
            String notKey = getString(R.string.prefs_notification_sound_key);
            Uri ringtoneUri = Uri.parse(sp.getString(notKey, ""));
            Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), ringtoneUri);
            String name = ringtone.getTitle(getActivity());
            if(name.trim().equals(""))name = "Blank Name";
            Log.d("Testing", "Ringtone is" + ringtone);


        RingtonePreference ringtonePref = (RingtonePreference)
                findPreference(notKey);
        ringtonePref.setSummary(name);
            //TODO find better solution to handling a blank ringtone
        }
        catch(Exception e){
            Toast.makeText(getActivity(), "Failed to set ringtone.", Toast.LENGTH_SHORT).show();
        }

    }

    private String defaultFName;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout)super.onCreateView(inflater, container, savedInstanceState);

        fragmentContext = inflater.getContext();

        //set displayname to parse name

        EditTextPreference etp = (EditTextPreference)findPreference(getString(R.string.prefs_display_name_key));
        String fname = ParseUser.getCurrentUser().get("friendlyName").toString();
        etp.setText(fname);
        defaultFName = fname;
        //etp.getEditText().setFilters(new InputFilter[]{new InputFilterMinMax(1, 20)});



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
            try {
                ParseUser.logOut();
            }
            catch(Exception e){
                Log.e(getString(R.string.log_error), getString(R.string.userNotLoggedIn));
                return -1;//error code
            }
            return 0;//log out success
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            //remove persistant login
            SecurePreferences preferences = new SecurePreferences(fragmentContext,getString(R.string.pref_login),SecurePreferences.generateDeviceUUID(fragmentContext),true);
            preferences.clear();

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


        if(key.equals(getString(R.string.prefs_notification_sound_key))){
            Uri ringtoneUri = Uri.parse(sharedPreferences.getString(key, ""));
            Log.d("Testing", "Ringtone key is" + ringtoneUri);

            Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), ringtoneUri);
            String name = ringtone.getTitle(getActivity());

            RingtonePreference ringtonePref = (RingtonePreference) findPreference(key);
            ringtonePref.setSummary(name);
        }else if(key.equals(getString(R.string.prefs_notification_resend_toggle_key))){

        }else if(key.equals(getString(R.string.prefs_notification_resend_delay_key))){

        }else if(key.equals(getString(R.string.prefs_display_name_key))){
            String newName = ((EditTextPreference) pref).getText().trim();
            if(newName.equals("")){//override
                ((EditTextPreference) pref).setText(defaultFName);
                Toast.makeText(getActivity(), R.string.str_blank_name_msg, Toast.LENGTH_SHORT).show();
            }
            else {
                defaultFName = newName;
                pref.setSummary(newName);
                ParseUser u = ParseUser.getCurrentUser();
                u.put("friendlyName", newName);
                u.saveInBackground();
            }
        }else if(key.equals(getString(R.string.prefs_clear_pings_key))){
            //will not run
        }
        /*
        else if (pref instanceof SwitchPreference) {
            Log.d("Testing", "Changed switch preference.");
            Log.d("Testing", "Switch key is" + key);

        }

        else if (pref instanceof ListPreference) {
            Log.d("Testing", "Changed list preference.");
            Log.d("Testing", "List key is" + key);

        }
        */
    }

}