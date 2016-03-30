package edu.gcc.whiletrue.pingit.chat;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sendbird.android.MessageListQuery;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdEventHandler;
import com.sendbird.android.model.BroadcastMessage;
import com.sendbird.android.model.Channel;
import com.sendbird.android.model.FileLink;
import com.sendbird.android.model.Message;
import com.sendbird.android.model.MessageModel;
import com.sendbird.android.model.MessagingChannel;
import com.sendbird.android.model.ReadStatus;
import com.sendbird.android.model.SystemMessage;
import com.sendbird.android.model.TypeStatus;

import java.util.List;

import edu.gcc.whiletrue.pingit.R;


public class SendBirdChatActivity extends FragmentActivity {
    public static final int REQUEST_CHANNEL_LIST = 100;


    private SendBirdChatFragment mSendBirdChatFragment;
    private SendBirdChatAdapter mSendBirdChatAdapter;

    private ImageButton mBtnClose;
    private ImageButton mBtnSettings;
    private TextView mTxtChannelUrl;
    private View mTopBarContainer;
    private View mSettingsContainer;
    private Button mBtnLeave;
    private String mChannelUrl;
    private boolean mDoNotDisconnect;


    public static Bundle makeSendBirdArgs(String appKey, String uuid, String nickname, String channelUrl) {
        Bundle args = new Bundle();
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
        setContentView(R.layout.activity_sendbird_chat);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initFragment();

        initUIComponents();
        initSendBird(getIntent().getExtras());

        SendBird.queryMessageList(mChannelUrl).prev(Long.MAX_VALUE, 50, new MessageListQuery.MessageListQueryResult() {
            @Override
            public void onResult(List<MessageModel> messageModels) {
                for (MessageModel model : messageModels) {
                    mSendBirdChatAdapter.addMessageModel(model);
                }


                mSendBirdChatAdapter.notifyDataSetChanged();
                mSendBirdChatFragment.mListView.setSelection(mSendBirdChatAdapter.getCount());
                SendBird.join(mChannelUrl);
                SendBird.connect(mSendBirdChatAdapter.getMaxMessageTimestamp());
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        resizeMenubar();
    }


    private void resizeMenubar() {
        ViewGroup.LayoutParams lp = mTopBarContainer.getLayoutParams();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            lp.height = (int) (28 * getResources().getDisplayMetrics().density);
        } else {
            lp.height = (int) (48 * getResources().getDisplayMetrics().density);
        }
        mTopBarContainer.setLayoutParams(lp);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mDoNotDisconnect) {
            SendBird.disconnect();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.sendbird_slide_in_from_top, R.anim.sendbird_slide_out_to_bottom);
    }

    private void initFragment() {
        mSendBirdChatFragment = new SendBirdChatFragment();

        mSendBirdChatAdapter = new SendBirdChatAdapter(this);
        mSendBirdChatFragment.setSendBirdChatAdapter(mSendBirdChatAdapter);

        /*
        mSendBirdChatFragment.setSendBirdChatHandler(new SendBirdChatFragment.SendBirdChatHandler() {

            @Override
            public void onChannelListClicked() {
                Intent intent = new Intent(SendBirdChatActivity.this, SendBirdChannelListActivity.class);
                intent.putExtras(getIntent().getExtras());
                startActivityForResult(intent, REQUEST_CHANNEL_LIST);
            }
        });
        */

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mSendBirdChatFragment)
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHANNEL_LIST) {
            if (resultCode == RESULT_OK && data != null) {
                mChannelUrl = data.getStringExtra("channelUrl");


                mSendBirdChatAdapter.clear();
                mSendBirdChatAdapter.notifyDataSetChanged();

                SendBird.queryMessageList(mChannelUrl).prev(Long.MAX_VALUE, 50, new MessageListQuery.MessageListQueryResult() {
                    @Override
                    public void onResult(List<MessageModel> messageModels) {
                        for (MessageModel model : messageModels) {
                            mSendBirdChatAdapter.addMessageModel(model);
                        }


                        mSendBirdChatAdapter.notifyDataSetChanged();
                        mSendBirdChatFragment.mListView.setSelection(mSendBirdChatAdapter.getCount());
                        SendBird.join(mChannelUrl);
                        SendBird.connect(mSendBirdChatAdapter.getMaxMessageTimestamp());
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            }
        }
    }

    private void initSendBird(Bundle extras) {
        String appKey = extras.getString("appKey");
        String uuid = extras.getString("uuid");
        String nickname = extras.getString("nickname");
        mChannelUrl = extras.getString("channelUrl");

        SendBird.init(appKey);
        SendBird.login(uuid, nickname);
        SendBird.setEventHandler(new SendBirdEventHandler() {
            @Override
            public void onConnect(Channel channel) {
                mTxtChannelUrl.setText("#" + channel.getUrlWithoutAppPrefix());
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
                mSendBirdChatAdapter.addMessageModel(message);
            }

            @Override
            public void onSystemMessageReceived(SystemMessage systemMessage) {
                mSendBirdChatAdapter.addMessageModel(systemMessage);
            }

            @Override
            public void onBroadcastMessageReceived(BroadcastMessage broadcastMessage) {
                mSendBirdChatAdapter.addMessageModel(broadcastMessage);
            }

            @Override
            public void onFileReceived(FileLink fileLink) {
                mSendBirdChatAdapter.addMessageModel(fileLink);
            }

            @Override
            public void onAllDataReceived(SendBird.SendBirdDataType type, int count) {
                mSendBirdChatAdapter.notifyDataSetChanged();
                mSendBirdChatFragment.mListView.setSelection(mSendBirdChatAdapter.getCount());
            }

            @Override
            public void onMessageDelivery(boolean sent, String message, String data, String id) {
                if (!sent) {
                    mSendBirdChatFragment.mEtxtMessage.setText(message);
                }
            }

            @Override
            public void onReadReceived(ReadStatus readStatus) {
            }

            @Override
            public void onTypeStartReceived(TypeStatus typeStatus) {
            }

            @Override
            public void onTypeEndReceived(TypeStatus typeStatus) {
            }

            @Override
            public void onMessagingStarted(MessagingChannel messagingChannel) {
            }

            @Override
            public void onMessagingUpdated(MessagingChannel messagingChannel) {
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

    private void initUIComponents() {
        mTopBarContainer = findViewById(R.id.top_bar_container);
        mTxtChannelUrl = (TextView) findViewById(R.id.txt_channel_url);

        mSettingsContainer = findViewById(R.id.settings_container);
        mSettingsContainer.setVisibility(View.GONE);

        mBtnLeave = (Button) findViewById(R.id.btn_leave);

        mBtnLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSettingsContainer.setVisibility(View.GONE);
                SendBird.leave(SendBird.getChannelUrl());
                finish();
            }
        });

        mBtnClose = (ImageButton) findViewById(R.id.btn_close);
        mBtnSettings = (ImageButton) findViewById(R.id.btn_settings);

        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBtnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSettingsContainer.getVisibility() != View.VISIBLE) {
                    mSettingsContainer.setVisibility(View.VISIBLE);
                } else {
                    mSettingsContainer.setVisibility(View.GONE);
                }
            }
        });

        resizeMenubar();
    }




}
