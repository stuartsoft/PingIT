package edu.gcc.whiletrue.pingit;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
                    Toast.makeText(view.getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                ParseUser.logOut();//make sure the user is logged out first

                //create a ParseUser with the provided credentials
                ParseUser user = new ParseUser();
                user.put("friendlyName", nameTxt.getText().toString());
                user.setUsername(emailTxt.getText().toString().toLowerCase());
                user.setEmail(emailTxt.getText().toString());
                user.setPassword(passTxt.getText().toString());

                //check for network connection before attempting to register
                if (!mCallback.checkNetworkStatus()){
                    Toast.makeText(fragmentContainer.getContext(),
                            getString(R.string.noNetworkConnectionMsg), Toast.LENGTH_SHORT).show();
                    break;
                }

                //attempt to actually register the user
                registerUser(user,view);

                break;
            default:
                break;
        }
    }

    private void registerUser(final ParseUser user, final View view){
        //singUpInBackground is handled in a parallel background thread, separate from UI thread (duh)
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {//Sign up was successful! Open the home activity
                    Toast.makeText(fragmentContainer.getContext(),
                            getString(R.string.registerSuccessMsg), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(view.getContext(), HomeActivity.class));
                }
                else{//user could not be signed up
                    //begin building alert dialog for the exception
                    AlertDialog.Builder builder = new AlertDialog.Builder(fragmentContainer.getContext());
                    builder.setTitle(R.string.app_name);
                    builder.setPositiveButton("Okay", null);

                    switch (e.getCode()){//handle various exceptions
                        case ParseException.EMAIL_TAKEN:
                        case ParseException.USERNAME_TAKEN:
                            builder.setMessage(user.getEmail() +
                                    " is already in use. Please enter a different email.");
                            break;
                        case ParseException.INVALID_EMAIL_ADDRESS:
                            builder.setMessage("\""+user.getEmail() + "\"" +
                                    " is not a valid email. Please enter a valid email.");
                            break;
                        default://handles all other parse exceptions
                            builder.setMessage("Error (" + e.getCode() + ") " + e.getMessage());
                            break;
                    }

                    //build and display alert dialog for the user
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }

        });
    }
}
