package edu.gcc.whiletrue.pingit.chat;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sendbird.android.MessageListQuery;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdFileUploadEventHandler;
import com.sendbird.android.model.FileInfo;
import com.sendbird.android.model.MessageModel;

import java.io.Console;
import java.io.File;
import java.util.List;

import edu.gcc.whiletrue.pingit.Helper;
import edu.gcc.whiletrue.pingit.R;

public class SendBirdMessagingFragment extends Fragment {
    private static final int REQUEST_PICK_IMAGE = 100;

    public ListView mListView;
    public SendBirdMessagingAdapter mAdapter;
    public EditText mEtxtMessage;
    public Button mBtnSend;
    public ImageButton mBtnChannel;
    //        private ImageButton mBtnInvite;
    public ImageButton mBtnUpload;
    public ProgressBar mProgressBtnUpload;
    public SendBirdChatHandler mHandler;

    private boolean inflated = false;
    public boolean isInflated(){return inflated;}

    public static interface SendBirdChatHandler {
        public void onChannelListClicked();
    }

    public void setSendBirdChatHandler(SendBirdChatHandler handler) {
        mHandler = handler;
    }

    public SendBirdMessagingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sendbird_fragment_messaging, container, false);
        initUIComponents(rootView);
        return rootView;
    }


    private void initUIComponents(View rootView) {
        mListView = (ListView)rootView.findViewById(R.id.list);
        turnOffListViewDecoration(mListView);
        mListView.setAdapter(mAdapter);

        mBtnChannel = (ImageButton)rootView.findViewById(R.id.btn_channel);
        mBtnSend = (Button)rootView.findViewById(R.id.btn_send);
        mBtnUpload = (ImageButton)rootView.findViewById(R.id.btn_upload);
        mProgressBtnUpload = (ProgressBar)rootView.findViewById(R.id.progress_btn_upload);
        mEtxtMessage = (EditText)rootView.findViewById(R.id.etxt_message);

        mBtnSend.setEnabled(false);
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });


        mBtnChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHandler != null) {
                    mHandler.onChannelListClicked();
                }
            }
        });

        mBtnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PICK_IMAGE);
            }
        });

        mEtxtMessage.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER) {
                    if(event.getAction() == KeyEvent.ACTION_DOWN) {
                        send();
                    }
                    return true; // Do not hide keyboard.
                }

                return false;
            }
        });
        mEtxtMessage.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        mEtxtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mBtnSend.setEnabled(s.length() > 0);

                if(s.length() > 0) {
                    SendBird.typeStart();
                } else {
                    SendBird.typeEnd();
                }
            }
        });
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Helper.hideKeyboard(getActivity());
                return false;
            }
        });
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == SCROLL_STATE_IDLE) {
                    if(view.getFirstVisiblePosition() == 0 && view.getChildCount() > 0 && view.getChildAt(0).getTop() == 0) {
                        SendBird.queryMessageList(SendBird.getChannelUrl()).prev(mAdapter.getMinMessageTimestamp(), 30, new MessageListQuery.MessageListQueryResult() {
                            @Override
                            public void onResult(List<MessageModel> messageModels) {
                                if(messageModels.size() <= 0) {
                                    return;
                                }

                                for(MessageModel model : messageModels) {
                                    mAdapter.addMessageModel(model);
                                }
                                mAdapter.notifyDataSetChanged();
                                mListView.setSelection(messageModels.size());
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
                    } else if(view.getLastVisiblePosition() == mListView.getAdapter().getCount() - 1 && view.getChildCount() > 0) {
                        SendBird.queryMessageList(SendBird.getChannelUrl()).next(mAdapter.getMaxMessageTimestamp(), 30, new MessageListQuery.MessageListQueryResult() {
                            @Override
                            public void onResult(List<MessageModel> messageModels) {
                                if(messageModels.size() <= 0) {
                                    return;
                                }

                                for(MessageModel model : messageModels) {
                                    mAdapter.addMessageModel(model);
                                }
                                mAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    private void showUploadProgress(boolean tf) {
        if(tf) {
            mBtnUpload.setEnabled(false);
            mBtnUpload.setVisibility(View.INVISIBLE);
            mProgressBtnUpload.setVisibility(View.VISIBLE);
        } else {
            mBtnUpload.setEnabled(true);
            mBtnUpload.setVisibility(View.VISIBLE);
            mProgressBtnUpload.setVisibility(View.GONE);
        }
    }

    private void turnOffListViewDecoration(ListView listView) {
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setHorizontalFadingEdgeEnabled(false);
        listView.setVerticalFadingEdgeEnabled(false);
        listView.setHorizontalScrollBarEnabled(false);
        listView.setVerticalScrollBarEnabled(true);
        listView.setSelector(new ColorDrawable(0x00ffffff));
        listView.setCacheColorHint(0x00000000); // For Gingerbread scrolling bug fix
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == REQUEST_PICK_IMAGE && data != null && data.getData() != null) {
                upload(data.getData());
            }
        }
    }

    private void send() {
        SendBird.send(mEtxtMessage.getText().toString());
        mEtxtMessage.setText("");

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Helper.hideKeyboard(getActivity());
        }
    }

    private void upload(Uri uri) {
        try {
            Cursor cursor = getActivity().getContentResolver().query(uri,
                    new String[] {
                            MediaStore.Images.Media.DATA,
                            MediaStore.Images.Media.MIME_TYPE,
                            MediaStore.Images.Media.SIZE,
                    },
                    null, null, null);
            cursor.moveToFirst();
            final String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            final String mime = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));
            final int size = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
            cursor.close();

            if(path == null) {
                Toast.makeText(getActivity(), "Uploading file must be located in local storage.", Toast.LENGTH_LONG).show();
            } else {
                showUploadProgress(true);
                SendBird.uploadFile(new File(path), mime, size, "", new SendBirdFileUploadEventHandler() {
                    @Override
                    public void onUpload(FileInfo fileInfo, Exception e) {
                        showUploadProgress(false);
                        if (e != null) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Fail to upload the file.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        SendBird.sendFile(fileInfo);
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Fail to upload the file.", Toast.LENGTH_LONG).show();
        }
    }


    public void setSendBirdMessagingAdapter(SendBirdMessagingAdapter adapter) {
        mAdapter = adapter;
        if(mListView != null) {
            mListView.setAdapter(adapter);
        }
    }
}
