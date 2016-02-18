package edu.gcc.whiletrue.pingit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginFragment extends Fragment implements View.OnClickListener{

    private ViewGroup fragmentContainer;
    private OnHeadlineSelectedListener mCallback;

    private EditText emailTxt;
    private EditText passTxt;
    private AlertDialog signInDialog;
    private SignInTask signInTask;

    // Container Activity must implement this interface
    public interface OnHeadlineSelectedListener {
        public void onSwitchToRegister();
        public boolean checkNetworkStatus();

    }

    public LoginFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnHeadlineSelectedListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        fragmentContainer = container;

        Button btnTemp = (Button) view.findViewById(R.id.loginBtn);
        btnTemp.setOnClickListener(this);

        Button btnSwitchToRegister = (Button) view.findViewById(R.id.switchToRegisterBtn);
        btnSwitchToRegister.setOnClickListener(this);

        emailTxt = (EditText) view.findViewById(R.id.loginEmailTxt);
        passTxt = (EditText) view.findViewById(R.id.loginPasswordTxt);

        //create the login dialog now for use later when the user presses login
        AlertDialog.Builder builder = new AlertDialog.Builder(fragmentContainer.getContext());
        builder.setTitle(R.string.app_name);
        builder.setView(inflater.inflate(R.layout.dialog_signin, null));
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                signInTask.cancel(true);}
        });
        signInDialog = builder.create();

        return view;
    }

    @Override
    public void onClick(View v) {
        final View view = v;//final reference to the view that called onClick

        switch (v.getId()){
            case R.id.switchToRegisterBtn:
                mCallback.onSwitchToRegister();
                break;
            case R.id.loginBtn:
                signInTask = new SignInTask(emailTxt.getText().toString().toLowerCase(), passTxt.getText().toString(), view,fragmentContainer.getContext());
                signInTask.execute();
                break;
            default:
                break;
        }
    }

    private class SignInTask extends AsyncTask<String, Void, Integer> {
        String email;
        String pass;
        final View view;
        Context context;

        SignInTask(String email, String pass, View view, Context context){
            this.email = email;
            this.pass = pass;
            this.view = view;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //show login dialog with progress spinner while parse executes in the background
            signInDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            if (!mCallback.checkNetworkStatus())
                return -1;//no internet, couldn't even attempt to login

            ParseUser.logOut();//make sure the user is logged out first

            try {ParseUser.logIn(email,pass);
            } catch (ParseException e) {return e.getCode();}//return exception code
            return 0;//no issues
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            signInDialog.dismiss();

            if (integer == 0){//login succeeded! Open home activity!
                Toast.makeText(fragmentContainer.getContext(), "Login Successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(view.getContext(), HomeActivity.class);
                startActivity(intent);
            }else{//handle the exception

                AlertDialog.Builder builder = new AlertDialog.Builder(fragmentContainer.getContext());
                builder.setTitle(R.string.app_name);
                builder.setPositiveButton("Okay", null);

                switch (integer){
                    case -1://no internet connection
                        builder.setMessage(R.string.noNetworkConnectionMsg);
                        break;
                    case 101://login credentials issue
                        builder.setMessage("Username or password are incorrect. Please try again.");
                        break;
                    default://handles all other parse exceptions
                        builder.setMessage("Error (" + integer + ") ");
                        break;
                }

                //build and display alert dialog for the user
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }
}
