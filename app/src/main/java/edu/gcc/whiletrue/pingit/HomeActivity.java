package edu.gcc.whiletrue.pingit;


import android.content.Context;
import android.content.Intent;
<<<<<<< HEAD
import android.graphics.Color;
=======
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
>>>>>>> 3e0068546cda39ae01999577a4ae6adeb3bcf0a9
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
<<<<<<< HEAD
import android.support.v4.view.PagerTabStrip;
=======
import android.support.v4.app.FragmentTransaction;
>>>>>>> 3e0068546cda39ae01999577a4ae6adeb3bcf0a9
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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

import edu.gcc.whiletrue.pingit.chat.SendBirdMessagingAdapter;
import edu.gcc.whiletrue.pingit.chat.SendBirdMessagingFragment;
import edu.gcc.whiletrue.pingit.chat.StartChatFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements PingsLoadingFragment.PingsPageInterface, PingsLoadingFragment.networkStatusCallback{

    public Fragment pingsFragment;

    public Fragment chatFragment;
    private SendBirdMessagingAdapter mSendBirdMessagingAdapter;
    private SendBirdMessagingFragment mSendBirdMessagingFragment;
    private CountDownTimer mTimer;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initSendBird();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (pingsFragment == null)
            pingsFragment = PingsLoadingFragment.newInstance();

        if(chatFragment == null)
            chatFragment = StartChatFragment.newInstance();

        if(mSendBirdMessagingAdapter == null)
            mSendBirdMessagingAdapter = new SendBirdMessagingAdapter(this);

        if(mSendBirdMessagingFragment == null)
            mSendBirdMessagingFragment = SendBirdMessagingFragment.newInstance(mSendBirdMessagingAdapter);



        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    public boolean checkNetworkStatus(){
        ConnectivityManager cm = (ConnectivityManager)getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
    }

    @Override
    public void displayPingsList(ArrayList<Ping> data) {

        Fragment newPingsPage = PingsPageFragment.newInstance(data);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        RegisterFragment registerFrag = new RegisterFragment();
        fragmentTransaction.remove(pingsFragment);
        fragmentTransaction.commit();

        pingsFragment = newPingsPage;
        mSectionsPagerAdapter.notifyDataSetChanged();
        Log.w(getString(R.string.log_warning), "displayPingsList: " );
    }

    public void displayChat(String targetUserID){

        //setContentView(R.layout.activity_sendbird_messaging);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Fragment newChatPage = SendBirdMessagingFragment.newInstance(mSendBirdMessagingAdapter);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        RegisterFragment registerFrag = new RegisterFragment();
        fragmentTransaction.remove(chatFragment);
        fragmentTransaction.commit();

        chatFragment = mSendBirdMessagingFragment;
        mSectionsPagerAdapter.notifyDataSetChanged();

        String [] tuid = {targetUserID};
        SendBird.startMessaging(Arrays.asList(tuid));

    }

    @Override
    public void loadPings() {
        pingsFragment = PingsLoadingFragment.newInstance();
        mSectionsPagerAdapter.notifyDataSetChanged();
        Log.w(getString(R.string.log_warning), "loadPings: ");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_home_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position){
                case 0:
                    return FAQPageFragment.newInstance();
                case 1:
                    return chatFragment;
                default:
                    return pingsFragment;
            }
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.faqSectionTitle);
                case 1:
                    return getString(R.string.chatSectionTitle);
                case 2:
                    return getString(R.string.pingsSectionTitle);
            }
            return null;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SendBird.disconnect();
    }

    final String sendBirdAppID = "EEC4E7E6-2738-45D8-85F7-E0DF5D3584DE";
    final String channelUrl = "01b2d.PingIt";
    private boolean isForeground;
    private MessagingChannel mMessagingChannel;

    private void initSendBird() {

        String appKey = sendBirdAppID;
        String username = ParseUser.getCurrentUser().getUsername();
        String nickname = (String) ParseUser.getCurrentUser().get("friendlyName");

        SendBird.init(sendBirdAppID);
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
                if (isForeground) {
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

}
