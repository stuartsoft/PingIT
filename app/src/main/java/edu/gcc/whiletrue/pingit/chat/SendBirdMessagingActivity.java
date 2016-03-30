package edu.gcc.whiletrue.pingit.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sendbird.android.MessageListQuery;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdEventHandler;
import com.sendbird.android.SendBirdFileUploadEventHandler;
import com.sendbird.android.SendBirdNotificationHandler;
import com.sendbird.android.model.BroadcastMessage;
import com.sendbird.android.model.Channel;
import com.sendbird.android.model.FileInfo;
import com.sendbird.android.model.FileLink;
import com.sendbird.android.model.Mention;
import com.sendbird.android.model.Message;
import com.sendbird.android.model.MessageModel;
import com.sendbird.android.model.MessagingChannel;
import com.sendbird.android.model.ReadStatus;
import com.sendbird.android.model.SystemMessage;
import com.sendbird.android.model.TypeStatus;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import edu.gcc.whiletrue.pingit.Helper;
import edu.gcc.whiletrue.pingit.R;


public class SendBirdMessagingActivity extends FragmentActivity {
    private static final int REQUEST_MESSAGING_CHANNEL_LIST = 100;
    private static final int REQUEST_MEMBER_LIST = 200;

    private SendBirdMessagingFragment mSendBirdMessagingFragment;
    private SendBirdMessagingAdapter mSendBirdMessagingAdapter;

    private ImageButton mBtnClose;
    private ImageButton mBtnSettings;
    private TextView mTxtChannelUrl;
    private View mTopBarContainer;
    private CountDownTimer mTimer;
    private Button mBtnInvite;
    private View mSettingsContainer;
    private MessagingChannel mMessagingChannel;
    private Bundle mSendBirdInfo;

    private boolean isForeground;


    public static Bundle makeMessagingStartArgs(String appKey, String uuid, String nickname, String targetUserId) {
        return makeMessagingStartArgs(appKey, uuid, nickname, new String[]{targetUserId});
    }

    public static Bundle makeMessagingStartArgs(String appKey, String uuid, String nickname, String [] targetUserIds) {
        Bundle args = new Bundle();
        args.putBoolean("start", true);
        args.putString("appKey", appKey);
        args.putString("uuid", uuid);
        args.putString("nickname", nickname);
        args.putStringArray("targetUserIds", targetUserIds);
        return args;
    }

    public static Bundle makeMessagingJoinArgs(String appKey, String uuid, String nickname, String channelUrl) {
        Bundle args = new Bundle();
        args.putBoolean("join", true);
        args.putString("appKey", appKey);
        args.putString("uuid", uuid);
        args.putString("nickname", nickname);
        args.putString("channelUrl", channelUrl);
        return args;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.sendbird_slide_in_from_bottom, R.anim.sendbird_slide_out_to_top);
        setContentView(R.layout.activity_sendbird_messaging);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initFragment();

        initUIComponents();
        initSendBird(getIntent().getExtras());

        if(mSendBirdInfo.getBoolean("start")) {
            String [] targetUserIds = mSendBirdInfo.getStringArray("targetUserIds");
            SendBird.startMessaging(Arrays.asList(targetUserIds));
        } else if(mSendBirdInfo.getBoolean("join")) {
            String channelUrl = mSendBirdInfo.getString("channelUrl");
            SendBird.joinMessaging(channelUrl);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        resizeMenubar();
    }


    private void resizeMenubar() {
        ViewGroup.LayoutParams lp = mTopBarContainer.getLayoutParams();
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            lp.height = (int) (28 * getResources().getDisplayMetrics().density);
        } else {
            lp.height = (int) (48 * getResources().getDisplayMetrics().density);
        }
        mTopBarContainer.setLayoutParams(lp);
    }

    @Override
    protected void onResume() {
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
    protected void onPause() {
        super.onPause();

        isForeground = false;

        if (mTimer != null) {
            mTimer.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SendBird.disconnect();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.sendbird_slide_in_from_top, R.anim.sendbird_slide_out_to_bottom);
    }



    private void initFragment() {
        mSendBirdMessagingFragment = new SendBirdMessagingFragment();

        mSendBirdMessagingAdapter = new SendBirdMessagingAdapter(this);
        mSendBirdMessagingFragment.setSendBirdMessagingAdapter(mSendBirdMessagingAdapter);

        /*
        mSendBirdMessagingFragment.setSendBirdChatHandler(new SendBirdChatFragment.SendBirdChatHandler() {

            @Override
            public void onChannelListClicked() {
                startActivityForResult(new Intent(SendBirdMessagingActivity.this, SendBirdMessagingChannelListActivity.class), REQUEST_MESSAGING_CHANNEL_LIST);
            }
        });
        */

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mSendBirdMessagingFragment)
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_MESSAGING_CHANNEL_LIST) {
            if(resultCode == RESULT_OK && data != null) {
                SendBird.joinMessaging(data.getStringExtra("channelUrl"));
            }
        } else if(requestCode == REQUEST_MEMBER_LIST) {
            if(resultCode == RESULT_OK && data != null) {
                try {
                    SendBird.inviteMessaging(SendBird.getCurrentChannel().getUrl(), Arrays.asList(data.getStringArrayExtra("userIds")));
                } catch (IOException e) {
                    // Not Connected.
                }
            }
        }

    }

    private static String getDisplayMemberNames(List<MessagingChannel.Member> members) {
        if(members.size() < 2) {
            return "No Members";
        } else if(members.size() == 2) {
            StringBuffer names = new StringBuffer();
            for (MessagingChannel.Member member : members) {
                if (member.getId().equals(SendBird.getUserId())) {
                    continue;
                }

                names.append(", " + member.getName());
            }
            return names.delete(0, 2).toString();
        } else {
            return "Group " + members.size();
        }
    }

    private void initSendBird(Bundle extras) {
        mSendBirdInfo = extras;

        String appKey = extras.getString("appKey");
        String uuid = extras.getString("uuid");
        String nickname = extras.getString("nickname");

        SendBird.init(appKey);
        SendBird.login(uuid,nickname);
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
        mTxtChannelUrl.setText(getDisplayMemberNames(messagingChannel.getMembers()));

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

    private void initUIComponents() {

        mTopBarContainer = findViewById(R.id.top_bar_container);
        mTxtChannelUrl = (TextView)findViewById(R.id.txt_channel_url);

        mSettingsContainer = findViewById(R.id.settings_container);
        mSettingsContainer.setVisibility(View.GONE);

        mBtnClose = (ImageButton)findViewById(R.id.btn_close);
        mBtnSettings = (ImageButton)findViewById(R.id.btn_settings);

        mBtnInvite = (Button)findViewById(R.id.btn_invite);

        /*
        mBtnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SendBirdMessagingActivity.this, SendBirdUserListActivity.class);
                Bundle args = null;
                args = SendBirdUserListActivity.makeSendBirdArgs(SendBird.getAppId(), SendBird.getUserId(), SendBird.getUserName());
                intent.putExtras(args);
                startActivityForResult(intent, REQUEST_MEMBER_LIST);
                mSettingsContainer.setVisibility(View.GONE);
            }
        });
        */

        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBtnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSettingsContainer.getVisibility() != View.VISIBLE) {
                    mSettingsContainer.setVisibility(View.VISIBLE);
                } else {
                    mSettingsContainer.setVisibility(View.GONE);
                }
            }
        });

        resizeMenubar();
    }


    private static String getDisplayDateTime(Context context, long milli) {
        Date date = new Date(milli);

        if(System.currentTimeMillis() - milli < 60 * 60 * 24 * 1000l) {
            return DateFormat.getTimeFormat(context).format(date);
        }

        return DateFormat.getDateFormat(context).format(date) + " " + DateFormat.getTimeFormat(context).format(date);
    }
    private static void displayUrlImage(ImageView imageView, String url) {
        UrlDownloadAsyncTask.display(url, imageView, false);
    }
    private static void displayUrlImage(ImageView imageView, String url, boolean circle) {
        UrlDownloadAsyncTask.display(url, imageView, true);
    }

    private static void downloadUrl(FileLink fileLink, Context context) throws IOException {
        String url = fileLink.getFileInfo().getUrl();
        String name = fileLink.getFileInfo().getName();
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File downloadFile = File.createTempFile("SendBird", name.substring(name.lastIndexOf(".")), downloadDir);
        UrlDownloadAsyncTask.download(url, downloadFile, context);
    }





}
