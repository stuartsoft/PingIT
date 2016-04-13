package edu.gcc.whiletrue.pingit.chat;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.sendbird.android.SendBird;
import com.sendbird.android.model.MessagingChannel;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

import edu.gcc.whiletrue.pingit.HomeActivity;
import edu.gcc.whiletrue.pingit.R;


public class StartChatFragment extends Fragment {

    final String appId = "EEC4E7E6-2738-45D8-85F7-E0DF5D3584DE";
    final String channelUrl = "01b2d.PingIt";
    private static final int REQUEST_SENDBIRD_CHAT_ACTIVITY = 100;
    private static final int REQUEST_SENDBIRD_CHANNEL_LIST_ACTIVITY = 101;
    private static final int REQUEST_SENDBIRD_MESSAGING_ACTIVITY = 200;
    private static final int REQUEST_SENDBIRD_MESSAGING_CHANNEL_LIST_ACTIVITY = 201;
    private static final int REQUEST_SENDBIRD_USER_LIST_ACTIVITY = 300;


    private SendBirdMessagingFragment mSendBirdMessagingFragment;
    private SendBirdMessagingAdapter mSendBirdMessagingAdapter;
    private MessagingChannel mMessagingChannel;

    private EditText mInitialMessage;


    public StartChatFragment() {
        // Required empty public constructor
    }

    public static StartChatFragment newInstance() {
        StartChatFragment fragment = new StartChatFragment();
        return fragment;
    }

    private TextView descProb;
    private Button startChat;
    private TextView numOnlineAdmins;
    private boolean foundChatTarget = false;
    private ProgressDialog findingChatTargets;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_page, container, false);

        return view;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findingChatTargets = new ProgressDialog(getContext());
        findingChatTargets.setTitle("Searching for online admins...");
        descProb = (TextView) view.findViewById(R.id.plzDescProb);
        numOnlineAdmins = (TextView) view.findViewById(R.id.numOnlineAdmins);
        mInitialMessage = (EditText) view.findViewById(R.id.initialMessage);
        //disabled until join button is found.
        descProb.setVisibility(View.INVISIBLE);
        mInitialMessage.setVisibility(View.INVISIBLE);
        mInitialMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (foundChatTarget) {
                    if (mInitialMessage.getText().toString().length() > 0) {
                        startChat.setEnabled(true);
                        startChat.setAlpha(1);//restore button
                    } else {
                        startChat.setEnabled(false);
                        startChat.setAlpha(1);//restore button
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        startChat = (Button) view.findViewById(R.id.launchChatButt);
        startChat.setText(R.string.ClickToRefresh);
        startChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshButton(true); //  willl change this to connect button if avalible
            }
        });
        refreshButton(false);
    }

    private void refreshButton(final boolean showSpinner){
        if(isAdded()) {
            if(showSpinner)findingChatTargets.show();
            ParseQuery query = ParseQuery.getQuery("AdminTimes");
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, -30);
            query.whereGreaterThan("timeOfLastAction", calendar.getTime());
            query.orderByDescending("timeOfLastAction");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(showSpinner)findingChatTargets.hide();
                    if(isAdded()) {
                        if (objects == null || objects.isEmpty()) {
                            startChat.setText(R.string.ClickToRefresh);
                            numOnlineAdmins.setText(R.string.NoOnlineAdmins);
                        } else {
                            foundChatTarget=true;
                            //disabled until join button is found.
                            descProb.setVisibility(View.VISIBLE);
                            mInitialMessage.setVisibility(View.VISIBLE);
                            if (objects.size() == 1) {
                                numOnlineAdmins.setText(R.string.OneOnlineAdmin);
                            } else {
                                numOnlineAdmins.setText(String.format(getString(R.string.fmtNumOnlineAdmins), objects.size()));
                            }
                            Random rand = new Random();
                            int connIndex = rand.nextInt(objects.size());
                            final String firstAdminUserName = objects.get(connIndex).get("userName").toString();
                            String firstAdminFriendlyName = objects.get(connIndex).get("friendlyName").toString();
                            startChat.setEnabled(false);
                            startChat.setAlpha(0.5f);//grey out the button
                            startChat.setText(String.format(getString(R.string.fmtClickToChatWith), firstAdminFriendlyName));
                            startChat.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        startMessaging(firstAdminUserName);
                                    } catch (Exception e) {
                                        Log.e("SendBird", e.getMessage());
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
    }



    private void startMessaging(String targetUserId) throws Exception{
            HomeActivity h = (HomeActivity) getActivity();
            h.displayChat(targetUserId, mInitialMessage.getText().toString());
    }
}
