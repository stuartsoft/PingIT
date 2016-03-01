package edu.gcc.whiletrue.pingit;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sendbird.android.MessageListQuery;
import com.sendbird.android.MessagingChannelListQuery;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdEventHandler;
import com.sendbird.android.SendBirdFileUploadEventHandler;
import com.sendbird.android.model.BroadcastMessage;
import com.sendbird.android.model.Channel;
import com.sendbird.android.model.FileInfo;
import com.sendbird.android.model.FileLink;
import com.sendbird.android.model.Message;
import com.sendbird.android.model.MessageModel;
import com.sendbird.android.model.MessagingChannel;
import com.sendbird.android.model.ReadStatus;
import com.sendbird.android.model.SystemMessage;
import com.sendbird.android.model.TypeStatus;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;



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
        startChat.setEnabled(false);
        startChat.setText("Searching for online admins...");
        findOnlineAdmins(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects == null || objects.isEmpty()) {
                    startChat.setText("No online admins.");
                } else {
                    String firstAdminUserName = objects.get(0).get("userName").toString();
                    String firstAdminFriendlyName = objects.get(0).get("friendlyName").toString();
                    startChat.setEnabled(true);
                    startChat.setText("Click to chat with " + firstAdminFriendlyName);
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

    private void findOnlineAdmins(FindCallback<ParseObject> callback){
        ParseQuery query = ParseQuery.getQuery("AdminTimes");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, -30);
        query.whereGreaterThan("timeOfLastAction", calendar.getTime());
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

    private void joinMessaging(String channelUrl) {
        Intent intent = new Intent(getContext(), SendBirdMessagingActivity.class);
        Bundle args = SendBirdMessagingActivity.makeMessagingJoinArgs(appId, userId, userName, channelUrl);
        intent.putExtras(args);

        startActivityForResult(intent, REQUEST_SENDBIRD_MESSAGING_ACTIVITY);
    }



}
