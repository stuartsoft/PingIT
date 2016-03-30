package edu.gcc.whiletrue.pingit.chat;


import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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

    private CountDownTimer mTimer;

    private boolean isForeground;

    public StartChatFragment() {
        // Required empty public constructor
    }

    public static StartChatFragment newInstance() {
        StartChatFragment fragment = new StartChatFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mSendBirdMessagingFragment = new SendBirdMessagingFragment();

        mSendBirdMessagingAdapter = new SendBirdMessagingAdapter(getContext());
        mSendBirdMessagingFragment.setSendBirdMessagingAdapter(mSendBirdMessagingAdapter);

        initSendBird();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_page, container, false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        isForeground = true;
        SendBird.markAsRead();

        if(mTimer != null) {
            mTimer.cancel();
        }
        mTimer = new CountDownTimer(60 * 60 * 24 * 7 * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(mSendBirdMessagingAdapter != null) {
                    if(mSendBirdMessagingAdapter.checkTypeStatus()) {
                        mSendBirdMessagingAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFinish() {
            }
        };
        mTimer.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        isForeground = false;

        if (mTimer != null) {
            mTimer.cancel();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SendBird.disconnect();
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
                                    try {
                                        startMessaging(users);
                                    } catch (Exception e) {
                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG);
                                    }
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


    private void startMessaging(String [] targetUserIds) throws Exception{
        if(ParseUser.getCurrentUser()!=null) {

            String userId = ParseUser.getCurrentUser().getUsername();
            String userName = (String) ParseUser.getCurrentUser().get("friendlyName");
            if (userName == null) userName = "John Doe";
            Intent intent = new Intent(getContext(), SendBirdMessagingActivity.class);
            Bundle args = SendBirdMessagingActivity.makeMessagingStartArgs(appId, userId, userName, targetUserIds);
            intent.putExtras(args);

            startActivityForResult(intent, REQUEST_SENDBIRD_MESSAGING_ACTIVITY);


            //getFragmentManager().beginTransaction()
             //       .replace(R.id.fragment_chat_page, mSendBirdMessagingFragment)
             //       .commit();

            //Toast.makeText(getContext(),"Attempted to start chat...",Toast.LENGTH_LONG).show();

        }
        else throw new Exception("Attempted to start chat without being logged in.");
    }



    private void initSendBird() {
        String appKey = appId;
        String username = ParseUser.getCurrentUser().getUsername();
        String nickname = (String) ParseUser.getCurrentUser().get("friendlyName");


        SendBird.init(appKey);
        SendBird.login(username, nickname);
        SendBird.registerNotificationHandler(new SendBirdNotificationHandler() {
            @Override
            public void onMessagingChannelUpdated(MessagingChannel messagingChannel) {
                if (mMessagingChannel != null && mMessagingChannel.getId() == messagingChannel.getId()) {
                    updateMessagingChannel(messagingChannel);
                }
            }

            @Override
            public void onMentionUpdated(Mention mention) {

            }
        });

        SendBird.setEventHandler(new SendBirdEventHandler() {
            @Override
            public void onConnect(Channel channel) {
            }

            @Override
            public void onError(int code) {
                Log.e("SendBird", "Error code: " + code);
            }

            @Override
            public void onChannelLeft(Channel channel) {
            }

            @Override
            public void onMessageReceived(Message message) {
                if(isForeground){
                    SendBird.markAsRead();
                }
                mSendBirdMessagingAdapter.addMessageModel(message);
            }

            @Override
            public void onSystemMessageReceived(SystemMessage systemMessage) {
                switch (systemMessage.getCategory()) {
                    case SystemMessage.CATEGORY_TOO_MANY_MESSAGES:
                        systemMessage.setMessage("Too many messages. Please try later.");
                        break;
                    case SystemMessage.CATEGORY_MESSAGING_USER_BLOCKED:
                        systemMessage.setMessage("Blocked.");
                        break;
                    case SystemMessage.CATEGORY_MESSAGING_USER_DEACTIVATED:
                        systemMessage.setMessage("Deactivated.");
                        break;
                }

                mSendBirdMessagingAdapter.addMessageModel(systemMessage);
            }

            @Override
            public void onBroadcastMessageReceived(BroadcastMessage broadcastMessage) {
                mSendBirdMessagingAdapter.addMessageModel(broadcastMessage);
            }

            @Override
            public void onFileReceived(FileLink fileLink) {
                mSendBirdMessagingAdapter.addMessageModel(fileLink);
            }

            @Override
            public void onReadReceived(ReadStatus readStatus) {
                mSendBirdMessagingAdapter.setReadStatus(readStatus.getUserId(), readStatus.getTimestamp());
            }

            @Override
            public void onTypeStartReceived(TypeStatus typeStatus) {
                mSendBirdMessagingAdapter.setTypeStatus(typeStatus.getUserId(), System.currentTimeMillis());
            }

            @Override
            public void onTypeEndReceived(TypeStatus typeStatus) {
                mSendBirdMessagingAdapter.setTypeStatus(typeStatus.getUserId(), 0);
            }

            @Override
            public void onAllDataReceived(SendBird.SendBirdDataType type, int count) {
                mSendBirdMessagingAdapter.notifyDataSetChanged();
                mSendBirdMessagingFragment.mListView.setSelection(mSendBirdMessagingAdapter.getCount() - 1);
            }

            @Override
            public void onMessageDelivery(boolean sent, String message, String data, String tempId) {
                if (!sent) {
                    mSendBirdMessagingFragment.mEtxtMessage.setText(message);
                }
            }

            @Override
            public void onMessagingStarted(final MessagingChannel messagingChannel) {
                mSendBirdMessagingAdapter.clear();
                updateMessagingChannel(messagingChannel);

                SendBird.queryMessageList(messagingChannel.getUrl()).load(Long.MAX_VALUE, 30, 10, new MessageListQuery.MessageListQueryResult() {
                    @Override
                    public void onResult(List<MessageModel> messageModels) {
                        for (MessageModel model : messageModels) {
                            mSendBirdMessagingAdapter.addMessageModel(model);
                        }
                        mSendBirdMessagingAdapter.notifyDataSetChanged();
                        mSendBirdMessagingFragment.mListView.setSelection(30);

                        SendBird.markAsRead(messagingChannel.getUrl());
                        SendBird.join(messagingChannel.getUrl());
                        SendBird.connect(mSendBirdMessagingAdapter.getMaxMessageTimestamp());
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            }

            @Override
            public void onMessagingUpdated(MessagingChannel messagingChannel) {
                updateMessagingChannel(messagingChannel);
            }

            @Override
            public void onMessagingEnded(MessagingChannel messagingChannel) {
            }

            @Override
            public void onAllMessagingEnded() {
            }

            @Override
            public void onMessagingHidden(MessagingChannel messagingChannel) {
            }

            @Override
            public void onAllMessagingHidden() {
            }

        });
    }


    private void updateMessagingChannel(MessagingChannel messagingChannel) {
        mMessagingChannel = messagingChannel;
        //mTxtChannelUrl.setText(getDisplayMemberNames(messagingChannel.getMembers()));

        Hashtable<String, Long> readStatus = new Hashtable<String, Long>();
        for (MessagingChannel.Member member : messagingChannel.getMembers()) {
            Long currentStatus = mSendBirdMessagingAdapter.mReadStatus.get(member.getId());
            if(currentStatus == null) {
                currentStatus = 0L;
            }
            readStatus.put(member.getId(), Math.max(currentStatus, messagingChannel.getLastReadMillis(member.getId())));
        }
        mSendBirdMessagingAdapter.resetReadStatus(readStatus);

        mSendBirdMessagingAdapter.setMembers(messagingChannel.getMembers());
        mSendBirdMessagingAdapter.notifyDataSetChanged();
    }

}
