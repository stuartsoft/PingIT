package edu.gcc.whiletrue.pingit.chat;


import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sendbird.android.MessageListQuery;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdEventHandler;
import com.sendbird.android.SendBirdNotificationHandler;
import com.sendbird.android.model.BroadcastMessage;
import com.sendbird.android.model.Channel;
import com.sendbird.android.model.FileLink;
import com.sendbird.android.model.Mention;
import com.sendbird.android.model.Message;
import com.sendbird.android.model.MessageModel;
import com.sendbird.android.model.MessagingChannel;
import com.sendbird.android.model.ReadStatus;
import com.sendbird.android.model.SystemMessage;
import com.sendbird.android.model.TypeStatus;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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




    public StartChatFragment() {
        // Required empty public constructor
    }

    public static StartChatFragment newInstance() {
        StartChatFragment fragment = new StartChatFragment();
        return fragment;
    }

    private Timer refreshTimer;
    private Button startChat;

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
        refreshTimer.cancel();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startChat = (Button) view.findViewById(R.id.launchChatButt);
        startChat.setText(R.string.NoOnlineAdmins);
        refreshTimer = new Timer();
        refreshTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                refreshOnlineAdmins();
            }
        }, 0, 1000);
    }

    private void refreshOnlineAdmins(){
        if(getActivity() != null && startChat != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ParseQuery query = ParseQuery.getQuery("AdminTimes");
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.SECOND, -30);
                    query.whereGreaterThan("timeOfLastAction", calendar.getTime());
                    query.orderByDescending("timeOfLastAction");
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (objects == null || objects.isEmpty()) {
                                startChat.setText(R.string.NoOnlineAdmins);
                            } else {
                                refreshTimer.cancel();
                                final String firstAdminUserName = objects.get(0).get("userName").toString();
                                String firstAdminFriendlyName = objects.get(0).get("friendlyName").toString();
                                startChat.setEnabled(true);
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
                    });
                }
            });
        }
    }


    private void startMessaging(String targetUserId) throws Exception{
            HomeActivity h = (HomeActivity) getActivity();
            h.displayChat(targetUserId);
    }
}
