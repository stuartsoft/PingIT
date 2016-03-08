package edu.gcc.whiletrue.pingit;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.List;



public class ChatPageFragment extends Fragment {

    final String appId = "EEC4E7E6-2738-45D8-85F7-E0DF5D3584DE";
    final String channelUrl = "01b2d.PingIt";
    private static final int REQUEST_SENDBIRD_CHAT_ACTIVITY = 100;
    private static final int REQUEST_SENDBIRD_CHANNEL_LIST_ACTIVITY = 101;
    private static final int REQUEST_SENDBIRD_MESSAGING_ACTIVITY = 200;
    private static final int REQUEST_SENDBIRD_MESSAGING_CHANNEL_LIST_ACTIVITY = 201;
    private static final int REQUEST_SENDBIRD_USER_LIST_ACTIVITY = 300;


    String userId = ParseUser.getCurrentUser().getUsername();
    String userName = (String) ParseUser.getCurrentUser().get("friendlyName");


    public ChatPageFragment() {
        // Required empty public constructor
    }

    public static ChatPageFragment newInstance() {
        ChatPageFragment fragment = new ChatPageFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(userName == null) userName = "John Doe";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_page, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Button startChat = (Button) view.findViewById(R.id.launchChatButt);
        startChat.setText(R.string.ClickToRefresh);

        startChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findOnlineAdmins(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (objects == null || objects.isEmpty()) {
                            startChat.setText(R.string.NoOnlineAdmins);
                        } else {
                            String firstAdminUserName = objects.get(0).get("userName").toString();
                            String firstAdminFriendlyName = objects.get(0).get("friendlyName").toString();
                            startChat.setEnabled(true);
                            startChat.setText(String.format(getString(R.string.fmtClickToChatWith), firstAdminFriendlyName));
                            final String[] users = {firstAdminUserName};
                            startChat.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startMessaging(users);
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private void findOnlineAdmins(FindCallback<ParseObject> callback){
        ParseQuery query = ParseQuery.getQuery("AdminTimes");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, -30);
        query.whereGreaterThan("timeOfLastAction", calendar.getTime());
        query.orderByDescending("timeOfLastAction");
        query.findInBackground(callback);
    }

    private void startChat(String channelUrl) {
        Intent intent = new Intent(getContext(), SendBirdChatActivity.class);
        Bundle args = SendBirdChatActivity.makeSendBirdArgs(appId, userId, userName, channelUrl);

        intent.putExtras(args);

        startActivityForResult(intent, REQUEST_SENDBIRD_CHAT_ACTIVITY);
    }

    private void startMessaging(String [] targetUserIds) {
        Intent intent = new Intent(getContext(), SendBirdMessagingActivity.class);
        Bundle args = SendBirdMessagingActivity.makeMessagingStartArgs(appId, userId, userName, targetUserIds);
        intent.putExtras(args);

        startActivityForResult(intent, REQUEST_SENDBIRD_MESSAGING_ACTIVITY);
    }



}
