package edu.gcc.whiletrue.pingit.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import edu.gcc.whiletrue.pingit.R;

public class UrlDownloadAsyncTask extends AsyncTask<Void, Void, Object> {
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

    public static void display(String url, final ImageView imageView, final boolean circle) {
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
                    if(circle) {
                        imageView.setImageDrawable(new RoundedDrawable((Bitmap) object));
                    } else {
                        imageView.setImageBitmap((Bitmap) object);
                    }
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

