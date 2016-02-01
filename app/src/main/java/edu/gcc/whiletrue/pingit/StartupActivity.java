package edu.gcc.whiletrue.pingit;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

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

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        RegisterFragment registerFrag = new RegisterFragment();
        fragmentTransaction.add(R.id.startup_fragment_container, registerFrag);
        fragmentTransaction.commit();

    }

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
