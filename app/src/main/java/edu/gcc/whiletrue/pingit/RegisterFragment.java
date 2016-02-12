package edu.gcc.whiletrue.pingit;


import android.app.Activity;
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
        final View view;
        view = v;

        switch (v.getId()){
            case R.id.switchToLoginBtn:
                mCallback.onSwitchToLogin();
                break;
            case R.id.registerBtn:
                if (!Objects.equals(passTxt.getText().toString(), passConfirmTxt.getText().toString())) {
                    Toast.makeText(view.getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                ParseUser.logOut();//make sure the user is logged out first

                ParseUser user = new ParseUser();
                user.setUsername(emailTxt.getText().toString().toLowerCase());
                user.setEmail(emailTxt.getText().toString());
                user.setPassword(passTxt.getText().toString());

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(fragmentContainer.getContext(),
                                    "Registration successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(view.getContext(), HomeActivity.class);
                            startActivity(intent);

                        }
                        else
                        Toast.makeText(fragmentContainer.getContext(),
                                e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                break;
            default:
                break;
        }
    }
}
