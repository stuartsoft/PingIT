package edu.gcc.whiletrue.pingit.chat;

/**
 * Created by nalta on 3/29/2016.
 */

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sendbird.android.SendBird;
import com.sendbird.android.model.BroadcastMessage;
import com.sendbird.android.model.FileLink;
import com.sendbird.android.model.Message;
import com.sendbird.android.model.MessageModel;
import com.sendbird.android.model.MessagingChannel;
import com.sendbird.android.model.SystemMessage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import edu.gcc.whiletrue.pingit.R;

public class SendBirdMessagingAdapter extends BaseAdapter {
    private static final int TYPE_UNSUPPORTED = 0;
    private static final int TYPE_MESSAGE = 1;
    private static final int TYPE_SYSTEM_MESSAGE = 2;
    private static final int TYPE_FILELINK = 3;
    private static final int TYPE_BROADCAST_MESSAGE = 4;
    private static final int TYPE_TYPING_INDICATOR = 5;

    private final Context mContext;
    private final LayoutInflater mInflater;
    private final ArrayList<Object> mItemList;

    public Hashtable<String, Long> mReadStatus;
    private Hashtable<String, Long> mTypeStatus;
    private List<MessagingChannel.Member> mMembers;
    private long mMaxMessageTimestamp = Long.MIN_VALUE;
    private long mMinMessageTimestamp = Long.MAX_VALUE;

    private ProgressDialog progress;

    public SendBirdMessagingAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mItemList = new ArrayList<Object>();
        mReadStatus = new Hashtable<String, Long>();
        mTypeStatus = new Hashtable<String, Long>();
    }

    public void setProgressDialog(ProgressDialog p){progress = p;}

    @Override
    public int getCount() {
        return mItemList.size() + ((mTypeStatus.size() <= 0) ? 0 : 1);
    }

    @Override
    public Object getItem(int position) {
        if(position >= mItemList.size()) {
            ArrayList<String> names = new ArrayList<String>();
            for(MessagingChannel.Member member : mMembers) {
                if(mTypeStatus.containsKey(member.getId())) {
                    names.add(member.getName());
                }
            }

            return names;
        }
        return mItemList.get(position);
    }

    public void clear() {
        mMaxMessageTimestamp = Long.MIN_VALUE;
        mMinMessageTimestamp = Long.MAX_VALUE;

        mReadStatus.clear();
        mTypeStatus.clear();
        mItemList.clear();
    }

    public void resetReadStatus(Hashtable<String, Long> readStatus) {
        mReadStatus = readStatus;
    }

    public void setReadStatus(String userId, long timestamp) {
        if(mReadStatus.get(userId) == null || mReadStatus.get(userId) < timestamp) {
            mReadStatus.put(userId, timestamp);
        }
    }

    public void setTypeStatus(String userId, long timestamp) {
        if(userId.equals(SendBird.getUserId())) {
            return;
        }

        if(timestamp <= 0) {
            mTypeStatus.remove(userId);
        } else {
            mTypeStatus.put(userId, timestamp);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addMessageModel(MessageModel messageModel) {
        if(messageModel.isPast()) {
            mItemList.add(0, messageModel);
        } else {
            mItemList.add(messageModel);
        }
        updateMessageTimestamp(messageModel);
    }

    private void updateMessageTimestamp(MessageModel model) {
        mMaxMessageTimestamp = mMaxMessageTimestamp < model.getTimestamp() ? model.getTimestamp() : mMaxMessageTimestamp;
        mMinMessageTimestamp = mMinMessageTimestamp > model.getTimestamp() ? model.getTimestamp() : mMinMessageTimestamp;
    }

    public long getMaxMessageTimestamp() {
        return mMaxMessageTimestamp == Long.MIN_VALUE ? Long.MAX_VALUE : mMaxMessageTimestamp;
    }

    public long getMinMessageTimestamp() {
        return mMinMessageTimestamp == Long.MAX_VALUE ? Long.MIN_VALUE : mMinMessageTimestamp;
    }

    public void setMembers(List<MessagingChannel.Member> members) {
        mMembers = members;
    }


    @Override
    public int getItemViewType(int position) {
        if(position >= mItemList.size()) {
            return TYPE_TYPING_INDICATOR;
        }

        Object item = mItemList.get(position);
        if(item instanceof Message) {
            return TYPE_MESSAGE;
        } else if(item instanceof FileLink) {
            return TYPE_FILELINK;
        } else if(item instanceof SystemMessage) {
            return TYPE_SYSTEM_MESSAGE;
        } else if(item instanceof BroadcastMessage) {
            return TYPE_BROADCAST_MESSAGE;
        }

        return TYPE_UNSUPPORTED;
    }

    @Override
    public int getViewTypeCount() {
        return 6;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        final Object item = getItem(position);

        if(convertView == null || ((ViewHolder)convertView.getTag()).getViewType() != getItemViewType(position)) {
            viewHolder = new ViewHolder();
            viewHolder.setViewType(getItemViewType(position));

            switch(getItemViewType(position)) {
                case TYPE_UNSUPPORTED:
                    convertView = new View(mInflater.getContext());
                    convertView.setTag(viewHolder);
                    break;
                case TYPE_MESSAGE: {
                    TextView tv;
                    ImageView iv;
                    View v;

                    convertView = mInflater.inflate(R.layout.sendbird_view_messaging_message, parent, false);

                    v = convertView.findViewById(R.id.left_container);
                    viewHolder.setView("left_container", v);
                    iv = (ImageView) convertView.findViewById(R.id.img_left_thumbnail);
                    viewHolder.setView("left_thumbnail", iv);
                    tv = (TextView) convertView.findViewById(R.id.txt_left);
                    viewHolder.setView("left_message", tv);
                    tv = (TextView) convertView.findViewById(R.id.txt_left_name);
                    viewHolder.setView("left_name", tv);
                    tv = (TextView) convertView.findViewById(R.id.txt_left_time);
                    viewHolder.setView("left_time", tv);

                    v = convertView.findViewById(R.id.right_container);
                    viewHolder.setView("right_container", v);
                    iv = (ImageView) convertView.findViewById(R.id.img_right_thumbnail);
                    viewHolder.setView("right_thumbnail", iv);
                    tv = (TextView) convertView.findViewById(R.id.txt_right);
                    viewHolder.setView("right_message", tv);
                    tv = (TextView) convertView.findViewById(R.id.txt_right_name);
                    viewHolder.setView("right_name", tv);
                    tv = (TextView) convertView.findViewById(R.id.txt_right_time);
                    viewHolder.setView("right_time", tv);
                    tv = (TextView) convertView.findViewById(R.id.txt_right_status);
                    viewHolder.setView("right_status", tv);

                    convertView.setTag(viewHolder);
                    break;
                }
                case TYPE_SYSTEM_MESSAGE: {
                    convertView = mInflater.inflate(R.layout.sendbird_view_system_message, parent, false);
                    viewHolder.setView("message", convertView.findViewById(R.id.txt_message));
                    convertView.setTag(viewHolder);
                    break;
                }
                case TYPE_BROADCAST_MESSAGE: {
                    convertView = mInflater.inflate(R.layout.sendbird_view_system_message, parent, false);
                    viewHolder.setView("message", convertView.findViewById(R.id.txt_message));
                    convertView.setTag(viewHolder);
                    break;
                }
                case TYPE_FILELINK: {
                    TextView tv;
                    ImageView iv;
                    View v;

                    convertView = mInflater.inflate(R.layout.sendbird_view_messaging_filelink, parent, false);

                    v = convertView.findViewById(R.id.left_container);
                    viewHolder.setView("left_container", v);
                    iv = (ImageView) convertView.findViewById(R.id.img_left_thumbnail);
                    viewHolder.setView("left_thumbnail", iv);
                    iv = (ImageView) convertView.findViewById(R.id.img_left);
                    viewHolder.setView("left_image", iv);
                    tv = (TextView) convertView.findViewById(R.id.txt_left_name);
                    viewHolder.setView("left_name", tv);
                    tv = (TextView) convertView.findViewById(R.id.txt_left_time);
                    viewHolder.setView("left_time", tv);

                    v = convertView.findViewById(R.id.right_container);
                    viewHolder.setView("right_container", v);
                    iv = (ImageView) convertView.findViewById(R.id.img_right_thumbnail);
                    viewHolder.setView("right_thumbnail", iv);
                    iv = (ImageView) convertView.findViewById(R.id.img_right);
                    viewHolder.setView("right_image", iv);
                    tv = (TextView) convertView.findViewById(R.id.txt_right_name);
                    viewHolder.setView("right_name", tv);
                    tv = (TextView) convertView.findViewById(R.id.txt_right_time);
                    viewHolder.setView("right_time", tv);
                    tv = (TextView) convertView.findViewById(R.id.txt_right_status);
                    viewHolder.setView("right_status", tv);

                    convertView.setTag(viewHolder);

                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new AlertDialog.Builder(mContext)
                                    .setTitle("PingIT")
                                    .setMessage("Do you want to download this file?")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            try {
                                                downloadUrl((FileLink)item, mContext);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .create()
                                    .show();
                        }
                    });
                    break;
                }
                case TYPE_TYPING_INDICATOR: {
                    convertView = mInflater.inflate(R.layout.sendbird_view_typing_indicator, parent, false);
                    viewHolder.setView("message", convertView.findViewById(R.id.txt_message));
                    convertView.setTag(viewHolder);
                    break;
                }
            }
        }


        viewHolder = (ViewHolder) convertView.getTag();
        switch(getItemViewType(position)) {
            case TYPE_UNSUPPORTED:
                break;
            case TYPE_MESSAGE:
                Message message = (Message)item;
                if(message.getSenderId().equals(SendBird.getUserId())) {
                    viewHolder.getView("left_container", View.class).setVisibility(View.GONE);
                    viewHolder.getView("right_container", View.class).setVisibility(View.VISIBLE);

                    displayUrlImage(viewHolder.getView("right_thumbnail", ImageView.class), message.getSenderImageUrl(), true);
                    viewHolder.getView("right_name", TextView.class).setText(message.getSenderName());
                    viewHolder.getView("right_message", TextView.class).setText(message.getMessage());
                    viewHolder.getView("right_time", TextView.class).setText(getDisplayDateTime(mContext, message.getTimestamp()));

                    int readCount = 0;
                    for(String key : mReadStatus.keySet()) {
                        if(key.equals(message.getSenderId())) {
                            readCount += 1;
                            continue;
                        }

                        if(mReadStatus.get(key) >= message.getTimestamp()) {
                            readCount += 1;
                        }
                    }
                    if(readCount < mReadStatus.size()) {
                        if(mReadStatus.size() - readCount > 1) {
                            viewHolder.getView("right_status", TextView.class).setText("Unread " + (mReadStatus.size() - readCount));
                        } else {
                            viewHolder.getView("right_status", TextView.class).setText("Unread");
                        }
                    } else {
                        viewHolder.getView("right_status", TextView.class).setText("");
                    }
                } else {
                    viewHolder.getView("left_container", View.class).setVisibility(View.VISIBLE);
                    viewHolder.getView("right_container", View.class).setVisibility(View.GONE);

                    displayUrlImage(viewHolder.getView("left_thumbnail", ImageView.class), message.getSenderImageUrl(), true);
                    viewHolder.getView("left_name", TextView.class).setText(message.getSenderName());
                    viewHolder.getView("left_message", TextView.class).setText(message.getMessage());
                    viewHolder.getView("left_time", TextView.class).setText(getDisplayDateTime(mContext, message.getTimestamp()));
                }
                break;
            case TYPE_SYSTEM_MESSAGE:
                SystemMessage systemMessage = (SystemMessage)item;
                viewHolder.getView("message", TextView.class).setText(Html.fromHtml(systemMessage.getMessage()));
                break;
            case TYPE_BROADCAST_MESSAGE:
                BroadcastMessage broadcastMessage = (BroadcastMessage)item;
                viewHolder.getView("message", TextView.class).setText(Html.fromHtml(broadcastMessage.getMessage()));
                break;
            case TYPE_FILELINK:
                FileLink fileLink = (FileLink)item;

                if(fileLink.getSenderId().equals(SendBird.getUserId())) {
                    viewHolder.getView("left_container", View.class).setVisibility(View.GONE);
                    viewHolder.getView("right_container", View.class).setVisibility(View.VISIBLE);

                    displayUrlImage(viewHolder.getView("right_thumbnail", ImageView.class), fileLink.getSenderImageUrl(), true);
                    viewHolder.getView("right_name", TextView.class).setText(fileLink.getSenderName());
                    if(fileLink.getFileInfo().getType().toLowerCase().startsWith("image")) {
                        displayUrlImage(viewHolder.getView("right_image", ImageView.class), fileLink.getFileInfo().getUrl());
                    } else {
                        viewHolder.getView("right_image", ImageView.class).setImageResource(R.drawable.sendbird_icon_file);
                    }
                    viewHolder.getView("right_time", TextView.class).setText(getDisplayDateTime(mContext, fileLink.getTimestamp()));
                    int readCount = 0;
                    for(String key : mReadStatus.keySet()) {
                        if(key.equals(fileLink.getSenderId())) {
                            continue;
                        }

                        if(mReadStatus.get(key) < fileLink.getTimestamp()) {
                            readCount += 1;
                        }
                    }
                    if(readCount < mReadStatus.size() - 1) {
                        viewHolder.getView("right_status", TextView.class).setText("Unread");
                    } else {
                        viewHolder.getView("right_status", TextView.class).setText("");
                    }
                } else {
                    viewHolder.getView("left_container", View.class).setVisibility(View.VISIBLE);
                    viewHolder.getView("right_container", View.class).setVisibility(View.GONE);

                    displayUrlImage(viewHolder.getView("left_thumbnail", ImageView.class), fileLink.getSenderImageUrl(), true);
                    viewHolder.getView("left_name", TextView.class).setText(fileLink.getSenderName());
                    if(fileLink.getFileInfo().getType().toLowerCase().startsWith("image")) {
                        displayUrlImage(viewHolder.getView("left_image", ImageView.class), fileLink.getFileInfo().getUrl());
                    } else {
                        viewHolder.getView("left_image", ImageView.class).setImageResource(R.drawable.sendbird_icon_file);
                    }
                    viewHolder.getView("left_time", TextView.class).setText(getDisplayDateTime(mContext, fileLink.getTimestamp()));
                }
                break;

            case TYPE_TYPING_INDICATOR: {
                int itemCount = ((List)item).size();
                String typeMsg = ((List)item).get(0)
                        + ((itemCount > 1) ? " +" + (itemCount - 1) : "")
                        + ((itemCount > 1) ? " are " : " is ")
                        + "typing...";
                viewHolder.getView("message", TextView.class).setText(typeMsg);
                break;
            }
        }

        return convertView;
    }

    public boolean checkTypeStatus() {
        /**
         * Clear an old type status.
         */
        for(String key : mTypeStatus.keySet()) {
            Long ts = mTypeStatus.get(key);
            if(System.currentTimeMillis() - ts > 10 * 1000L) {
                mTypeStatus.remove(key);
                return true;
            }
        }

        return false;
    }


    private class ViewHolder {
        private Hashtable<String, View> holder = new Hashtable<String, View>();
        private int type;

        public int getViewType() {
            return this.type;
        }

        public void setViewType(int type) {
            this.type = type;
        }
        public void setView(String k, View v) {
            holder.put(k, v);
        }

        public View getView(String k) {
            return holder.get(k);
        }

        public <T> T getView(String k, Class<T> type) {
            return type.cast(getView(k));
        }
    }


    private static void displayUrlImage(ImageView imageView, String url) {
        UrlDownloadAsyncTask.display(url, imageView);
    }

    private static void downloadUrl(FileLink fileLink, Context context) throws IOException {
        String url = fileLink.getFileInfo().getUrl();
        String name = fileLink.getFileInfo().getName();
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File downloadFile = File.createTempFile("SendBird", name.substring(name.lastIndexOf(".")), downloadDir);
        UrlDownloadAsyncTask.download(url, downloadFile, context);
    }

    private static class UrlDownloadAsyncTask extends AsyncTask<Void, Void, Object> {
        private static LRUCache cache = new LRUCache((int) (Runtime.getRuntime().maxMemory() / 16)); // 1/16th of the maximum memory.
        private final UrlDownloadAsyncTaskHandler handler;
        private String url;

        public static void download(String url, final File downloadFile, final Context context) {
            UrlDownloadAsyncTask task = new UrlDownloadAsyncTask(url, new UrlDownloadAsyncTaskHandler() {
                @Override
                public void onPreExecute() {
                    Toast.makeText(context, "Start downloading", Toast.LENGTH_SHORT).show();
                }

                @Override
                public Object doInBackground(File file) {
                    if(file == null) {
                        return null;
                    }

                    try {
                        BufferedInputStream in = null;
                        BufferedOutputStream out = null;

                        //create output directory if it doesn't exist
                        File dir = downloadFile.getParentFile();
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }

                        in = new BufferedInputStream(new FileInputStream(file));
                        out = new BufferedOutputStream(new FileOutputStream(downloadFile));

                        byte[] buffer = new byte[1024 * 100];
                        int read;
                        while ((read = in.read(buffer)) != -1) {
                            out.write(buffer, 0, read);
                        }
                        in.close();
                        out.flush();
                        out.close();

                        return downloadFile;
                    } catch(IOException e) {
                        e.printStackTrace();
                    }

                    return null;
                }

                @Override
                public void onPostExecute(Object object, UrlDownloadAsyncTask task) {
                    if(object != null && object instanceof File) {
                        Toast.makeText(context, "Finish downloading: " + ((File)object).getAbsolutePath(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "Error downloading", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                task.execute();
            }
        }

        public static void display(String url, final ImageView imageView) {
            UrlDownloadAsyncTask task = null;

            if(imageView.getTag() != null && imageView.getTag() instanceof UrlDownloadAsyncTask) {
                try {
                    task = (UrlDownloadAsyncTask) imageView.getTag();
                    task.cancel(true);
                } catch(Exception e) {}

                imageView.setTag(null);
            }

            task = new UrlDownloadAsyncTask(url, new UrlDownloadAsyncTaskHandler() {
                @Override
                public void onPreExecute() {
                }

                @Override
                public Object doInBackground(File file) {
                    if(file == null) {
                        return null;
                    }

                    Bitmap bm = null;
                    try {
                        int targetHeight = 256;
                        int targetWidth = 256;

                        BufferedInputStream bin = new BufferedInputStream(new FileInputStream(file));
                        bin.mark(bin.available());

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeStream(bin, null, options);

                        Boolean scaleByHeight = Math.abs(options.outHeight - targetHeight) >= Math.abs(options.outWidth - targetWidth);

                        if(options.outHeight * options.outWidth >= targetHeight * targetWidth) {
                            double sampleSize = scaleByHeight
                                    ? options.outHeight / targetHeight
                                    : options.outWidth / targetWidth;
                            options.inSampleSize = (int)Math.pow(2d, Math.floor(Math.log(sampleSize)/Math.log(2d)));
                        }

                        try {
                            bin.reset();
                        } catch(IOException e) {
                            bin = new BufferedInputStream(new FileInputStream(file));
                        }

                        // Do the actual decoding
                        options.inJustDecodeBounds = false;
                        bm = BitmapFactory.decodeStream(bin, null, options);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return bm;
                }

                @Override
                public void onPostExecute(Object object, UrlDownloadAsyncTask task) {
                    if(object != null && object instanceof Bitmap && imageView.getTag() == task) {
                        imageView.setImageBitmap((Bitmap)object);
                    } else {
                        imageView.setImageResource(R.drawable.sendbird_img_placeholder);
                    }
                }
            });

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                task.execute();
            }

            imageView.setTag(task);
        }

        public UrlDownloadAsyncTask(String url, UrlDownloadAsyncTaskHandler handler) {
            this.handler = handler;
            this.url = url;
        }

        public interface UrlDownloadAsyncTaskHandler {
            public void onPreExecute();
            public Object doInBackground(File file);
            public void onPostExecute(Object object, UrlDownloadAsyncTask task);
        }

        @Override
        protected void onPreExecute() {
            if(handler != null) {
                handler.onPreExecute();
            }
        }

        protected Object doInBackground(Void... args) {
            File outFile = null;
            try {
                if(cache.get(url) != null && new File(cache.get(url)).exists()) { // Cache Hit
                    outFile = new File(cache.get(url));
                } else { // Cache Miss, Downloading a file from the url.
                    outFile = File.createTempFile("sendbird-download", ".tmp");
                    OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outFile));

                    InputStream input = new BufferedInputStream(new URL(url).openStream());
                    byte[] buf = new byte[1024 * 100];
                    int read = 0;
                    while ((read = input.read(buf, 0, buf.length)) >= 0) {
                        outputStream.write(buf, 0, read);
                    }

                    outputStream.flush();
                    outputStream.close();
                    cache.put(url, outFile.getAbsolutePath());
                }



            } catch(IOException e) {
                e.printStackTrace();

                if(outFile != null) {
                    outFile.delete();
                }

                outFile = null;
            }


            if(handler != null) {
                return handler.doInBackground(outFile);
            }

            return outFile;
        }

        protected void onPostExecute(Object result) {
            if(handler != null) {
                handler.onPostExecute(result, this);
            }
        }

        private static class LRUCache {
            private final int maxSize;
            private int totalSize;
            private ConcurrentLinkedQueue<String> queue;
            private ConcurrentHashMap<String, String> map;

            public LRUCache(final int maxSize) {
                this.maxSize = maxSize;
                this.queue	= new ConcurrentLinkedQueue<String>();
                this.map	= new ConcurrentHashMap<String, String>();
            }

            public String get(final String key) {
                if (map.containsKey(key)) {
                    queue.remove(key);
                    queue.add(key);
                }

                return map.get(key);
            }

            public synchronized void put(final String key, final String value) {
                if(key == null || value == null) {
                    throw new NullPointerException();
                }

                if (map.containsKey(key)) {
                    queue.remove(key);
                }

                queue.add(key);
                map.put(key, value);
                totalSize = totalSize + getSize(value);

                while (totalSize >= maxSize) {
                    String expiredKey = queue.poll();
                    if (expiredKey != null) {
                        totalSize = totalSize - getSize(map.remove(expiredKey));
                    }
                }
            }

            private int getSize(String value) {
                return value.length();
            }
        }
    }

    private static String getDisplayDateTime(Context context, long milli) {
        Date date = new Date(milli);

        if(System.currentTimeMillis() - milli < 60 * 60 * 24 * 1000l) {
            return DateFormat.getTimeFormat(context).format(date);
        }

        return DateFormat.getDateFormat(context).format(date) + " " + DateFormat.getTimeFormat(context).format(date);
    }

    private static void displayUrlImage(ImageView imageView, String url, boolean circle) {
        UrlDownloadAsyncTask.display(url, imageView);
    }
}