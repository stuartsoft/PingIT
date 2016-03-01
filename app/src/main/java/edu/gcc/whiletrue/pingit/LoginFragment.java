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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import java.util.Objects;


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

        //create the signin dialog to display while the signin thread is running later
        AlertDialog.Builder builder = new AlertDialog.Builder(fragmentContainer.getContext());
        builder.setTitle(R.string.app_name);
        builder.setView(inflater.inflate(R.layout.dialog_signin, null));
        builder.setNegativeButton(R.string.dialogCancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                signInTask.cancel(true);//cancel the signin background thread
            }
        });
        signInDialog = builder.create();

        return view;
    }

    //handles all onClicke events for this fragment
    @Override
    public void onClick(View v) {
        final View view = v;//final reference to the view that called onClick

        switch (v.getId()){
            case R.id.switchToRegisterBtn:
                mCallback.onSwitchToRegister();
                break;
            case R.id.loginBtn:
                //start building an alert dialog incase there was an issue with registration credentials
                AlertDialog.Builder builder = new AlertDialog.Builder(fragmentContainer.getContext());
                builder.setTitle(R.string.app_name);
                builder.setPositiveButton(R.string.dialogConfirm, null);

                String dialogMsg = "";

                //sanitize and validate registration credentials before sending to parse
                if (Objects.equals(emailTxt.getText().toString(), ""))
                    dialogMsg = getString(R.string.emailNotValid);
                else if (Objects.equals(passTxt.getText().toString(),""))
                    dialogMsg = getString(R.string.passwordNotValid);
                
                if (dialogMsg != ""){
                    builder.setMessage(dialogMsg);
                    AlertDialog errorDialog = builder.create();
                    errorDialog.show();
                    return;
                }

                InputMethodManager imm = (InputMethodManager)getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                signInDialog.show();//show login dialog with progress spinner while Parse executes in the background
                signInTask = new SignInTask(emailTxt.getText().toString().toLowerCase(), passTxt.getText().toString(), view,fragmentContainer.getContext());
                signInTask.execute();//attempt to login in the background

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

        //this is the actual background process
        @Override
        protected Integer doInBackground(String... params) {
            if (!mCallback.checkNetworkStatus())
                return -1;//no internet, couldn't even attempt to login

            ParseUser.logOut();//make sure the user is logged out first

            try {
                ParseUser.logIn(email,pass);
                ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                installation.put("user",ParseUser.getCurrentUser());
                installation.saveInBackground();
            } catch (ParseException e) {return e.getCode();}//return exception code
            return 0;//no issues
        }

        //this is run on UI thread after the background thread completes
        @Override
        protected void onPostExecute(Integer errorCode) {
            super.onPostExecute(errorCode);
            signInDialog.dismiss();

            if (errorCode == 0){//Login succeeded! Open home activity!
                Toast.makeText(fragmentContainer.getContext(), R.string.loginSuccessMsg, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(view.getContext(), HomeActivity.class);
                startActivity(intent);
            }else{
                //Something went wrong, display a new dialog explaining what happened

                AlertDialog.Builder builder = new AlertDialog.Builder(fragmentContainer.getContext());
                builder.setTitle(R.string.app_name);
                builder.setPositiveButton(R.string.dialogConfirm, null);

                switch (errorCode){
                    case -1://no internet connection
                        builder.setMessage(R.string.noNetworkConnectionMsg);
                        break;
                    case 101://login credentials issue
                        builder.setMessage(R.string.invalidCredentials);
                        break;
                    default://handles all other parse exceptions
                        builder.setMessage("Error (" + errorCode + ") ");
                        break;
                }

                //build and display alert dialog for the user
                AlertDialog errorDialog = builder.create();
                errorDialog.show();
            }
        }
    }
}
