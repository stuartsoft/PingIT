package edu.gcc.whiletrue.pingit;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.Objects;


public class RegisterFragment extends Fragment implements View.OnClickListener {

    private ViewGroup fragmentContainer;
    private OnHeadlineSelectedListener mCallback;

    private EditText nameTxt;
    private EditText emailTxt;
    private EditText passTxt;
    private EditText passConfirmTxt;
    private SignUpTask signUpTask;
    private AlertDialog signUpDialog;

    // Container Activity must implement this interface
    public interface OnHeadlineSelectedListener {
        public void onSwitchToLogin();
        public boolean checkNetworkStatus();
    }

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {mCallback = (OnHeadlineSelectedListener) getActivity();
        } catch (ClassCastException e) { throw new ClassCastException(getActivity().toString()
                    + " must implement OnHeadlineSelectedListener");}
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        fragmentContainer = container;

        Button btnTemp = (Button) view.findViewById(R.id.registerBtn);
        btnTemp.setOnClickListener(this);

        Button btnSwitchToLogin = (Button) view.findViewById(R.id.switchToLoginBtn);
        btnSwitchToLogin.setOnClickListener(this);

        nameTxt = (EditText) view.findViewById(R.id.registerNameTxt);
        emailTxt = (EditText) view.findViewById(R.id.registerEmailTxt);
        passTxt = (EditText) view.findViewById(R.id.registerPasswordTxt);
        passConfirmTxt = (EditText) view.findViewById(R.id.registerConfirmPassword);

        //create the register dialog now for use later when the user presses Register

        AlertDialog.Builder builder = new AlertDialog.Builder(fragmentContainer.getContext());
        builder.setTitle(R.string.app_name);
        builder.setView(inflater.inflate(R.layout.dialog_signin, null));
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                signUpTask.cancel(true);}
        });
        signUpDialog = builder.create();

        return view;
    }

    @Override
    public void onClick(View v) {

        final View view = v; //creating an Intent or Toast requires a view casted as 'final'

        switch (v.getId()){//identify what item was pressed

            case R.id.switchToLoginBtn:
                mCallback.onSwitchToLogin();
                break;
            case R.id.registerBtn:
                if (!Objects.equals(passTxt.getText().toString(), passConfirmTxt.getText().toString())) {
                    Toast.makeText(view.getContext(), R.string.passwordsDontMatch, Toast.LENGTH_SHORT).show();
                    return;
                }

                //create a ParseUser with the provided credentials
                ParseUser user = new ParseUser();
                user.put("friendlyName", nameTxt.getText().toString());
                user.setUsername(emailTxt.getText().toString().toLowerCase());
                user.setEmail(emailTxt.getText().toString());
                user.setPassword(passTxt.getText().toString());

                signUpTask = new SignUpTask(user, view,fragmentContainer.getContext());
                signUpTask.execute();

                break;
            default:
                break;
        }
    }

    private class SignUpTask extends AsyncTask<String, Void, Integer> {
        ParseUser user;
        final View view;
        Context context;

        public SignUpTask(ParseUser user, View view, Context context) {
            this.user = user;
            this.view = view;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            signUpDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            if (!mCallback.checkNetworkStatus())
                return -1;//no internet, couldn't even attempt to login

            ParseUser.logOut();//make sure the user is logged out first

            try {user.signUp();
            } catch (ParseException e) {return e.getCode();}//return exception code
            return 0;//no issues
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            signUpDialog.dismiss();

            if (integer == 0){//login succeeded! Open home activity!
                Toast.makeText(fragmentContainer.getContext(), R.string.registerSuccessMsg, Toast.LENGTH_SHORT).show();
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
                    case ParseException.EMAIL_TAKEN:
                    case ParseException.USERNAME_TAKEN:
                        builder.setMessage(user.getEmail() + context.getString(R.string.emailAlreadyInUse));
                        break;
                    case ParseException.INVALID_EMAIL_ADDRESS:
                        builder.setMessage("\"" + user.getEmail() + "\"" +
                                context.getString(R.string.emailNotValid));
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
