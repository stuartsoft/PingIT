package edu.gcc.whiletrue.pingit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.parse.Parse;
import com.parse.ParseUser;

public class StartupActivity extends AppCompatActivity implements RegisterFragment.OnHeadlineSelectedListener, LoginFragment.OnHeadlineSelectedListener {

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.startupTitleRegister);
        setSupportActionBar(toolbar);

        fragmentManager = getSupportFragmentManager();

        onSwitchToRegister();

        //this intent call will skip the login activity for convenience
        //TODO remove this before production
        //Intent intent = new Intent(this, HomeActivity.class);
        //startActivity(intent);
        //finish();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this);

    }

    //This will be called by the foreground fragment when the user wants to switch to the Register view
    //The current fragment is replaced by the register fragment
    @Override
    public void onSwitchToRegister() {
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(R.string.startupTitleRegister);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fadein, R.anim.fadeout);
        RegisterFragment registerFrag = new RegisterFragment();
        fragmentTransaction.replace(R.id.startup_fragment_container,registerFrag);
        fragmentTransaction.commit();
    }

    //This will be called by the foreground fragment when the user wants to switch to the Login view
    //The current fragment is replaced by the login fragment
    @Override
    public void onSwitchToLogin() {
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(R.string.startupTitleLogin);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fadein, R.anim.fadeout);
        LoginFragment loginfrag = new LoginFragment();
        fragmentTransaction.replace(R.id.startup_fragment_container,loginfrag);
        fragmentTransaction.commit();
    }
}
