package com.exceedgulf.alainzoo.managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by indianic on 07/03/18.
 */

public class ImageDownloadManager {

    private static final String FORWARD_SLASH = "/";
    private static final String DOT = ".";

    public enum Task {
        DOWNLOAD, DELETE
    }

    public enum ImageSaveFailureReason {
        NETWORK, FILE
    }

    public interface Extensions {
        String PNG = "png";
        String JPEG = "jpeg";
        String WEBP = "webp";
    }

    public interface Callback {
        void onSuccess(ImageDownloadTask task);

        void onFailure(ImageSaveFailureReason reason);
    }

    private static final String LOG_TAG = ImageDownloadManager.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static ImageDownloadManager sInstance;

    private Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());
    private ThreadPoolExecutor threadPoolExecutor;
    private HashMap<ImageDownloadTask, Callback> callbacks = new HashMap<>();
    private Context context;

    private ImageDownloadManager(Context context) {

        if (sInstance != null) {
            throw new RuntimeException(
                    "Use getInstance() method to get the single instance of this class.");
        }

        int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
        this.context = context.getApplicationContext();
        threadPoolExecutor = new ThreadPoolExecutor(
                NUMBER_OF_CORES * 2,
                NUMBER_OF_CORES * 2,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
    }

    public static ImageDownloadManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) {
                    sInstance = new ImageDownloadManager(context);
                }
            }
        }
        return sInstance;
    }

    public void addTask(ImageDownloadTask task) {
        if (callbacks.containsKey(task)) {
            Log.e(LOG_TAG, "Have another task to process with same Tag. Rejecting");
            return;
        }

        threadPoolExecutor.execute(new ImageDownloadRunnable(task));
        callbacks.put(task, task.callback.get());
    }

    private class ImageDownloadRunnable implements Runnable {

        ImageDownloadTask imageDownloadTask;

        ImageDownloadRunnable(ImageDownloadTask task) {
            this.imageDownloadTask = task;
            if (task == null) {
                throw new InvalidParameterException("Task is null");
            }
        }

        @Override
        public void run() {
            switch (imageDownloadTask.task) {
                case DELETE: {
                    deleteFolder(new File(imageDownloadTask.folderPath));
                    postSuccess(imageDownloadTask);
                    break;
                }
                case DOWNLOAD: {
                    downloadImages(imageDownloadTask);
                    break;
                }
            }
        }

        private void downloadImages(final ImageDownloadTask task) {

            for (String url : task.urls) {
                Bitmap image = startDownload(url);
                if (null == image) {
                    postFailure(imageDownloadTask, ImageSaveFailureReason.NETWORK);
                    return;
                }
                if (!saveBitmapImage(image, imageDownloadTask.folderPath, url)) {
                    postFailure(task, ImageSaveFailureReason.FILE);
                    return;
                }
            }
            postSuccess(task);
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof ImageDownloadRunnable) {
                return imageDownloadTask.equals(((ImageDownloadRunnable) o).imageDownloadTask);
            }
            return super.equals(o);
        }
    }

    private void deleteFolder(File file) {
        try {
            if (file.isDirectory()) {

                // directory is empty, then delete it
                if (file.list().length == 0) {
                    file.delete();

                } else {

                    // list all the directory contents
                    String files[] = file.list();

                    for (String temp : files) {
                        // construct the file structure
                        File fileDelete = new File(file, temp);

                        // recursive delete
                        deleteFolder(fileDelete);
                    }

                    // check the directory again, if empty then delete it
                    if (file.list().length == 0) {
                        file.delete();
                    }
                }

            } else {
                // if file, then delete it
                file.delete();
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    public static String getFileNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/"), url.length());
    }

    public static String getHashCodeBasedFileName(String url) {
        return url.hashCode() + DOT + getExtension(getFileNameFromUrl(url));
    }

    public static String getExtension(String fileName) {
        String extension = "";
        if (fileName == null || fileName.isEmpty()) {
            return extension;
        }

        int indexOfLastDot = fileName.lastIndexOf(DOT);
        if (indexOfLastDot != -1 && indexOfLastDot < fileName.length())

        {
            extension = fileName.substring(indexOfLastDot + 1);
        }

        return extension;
    }


    public static boolean saveBitmapImage(Bitmap bitmap, String path, String url) {
        final File folder = new File(path);
        if (!folder.exists()) {
            if (!folder.mkdir()) {
                return false;
            }
        }
        try {
            final String fileName = getHashCodeBasedFileName(url);
            final String imagePath = path + FORWARD_SLASH + fileName;
            final File fileImages = new File(imagePath);
            if (!fileImages.exists()) {
                FileOutputStream fileOutputStream = new FileOutputStream(imagePath);
                bitmap.compress(getCompressFormatFromFileName(fileName), 100, fileOutputStream);
                fileOutputStream.close();
            } else {
                Log.e("CameraFrame", "Exist");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Bitmap.CompressFormat getCompressFormatFromFileName(String fileName) {
        switch (getExtension(fileName)) {
            case Extensions.WEBP:
                return Bitmap.CompressFormat.WEBP;
            case Extensions.JPEG:
                return Bitmap.CompressFormat.JPEG;
            default:
                return Bitmap.CompressFormat.PNG;
        }
    }

    private Bitmap startDownload(String url) {

        try {
            RequestCreator requestCreator = Picasso.with(context).load(Uri.parse(url));
            requestCreator.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE);
            return requestCreator.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }
    }

    public static class ImageDownloadTask {
        private Object tag;
        public Task task;
        public List<String> urls;
        public String folderPath;
        public WeakReference<Callback> callback;

        public ImageDownloadTask(Object tag, Task task, List<String> urls, String folderPath,
                                 Callback callback) {
            this.tag = tag;
            this.task = task;
            this.urls = urls;
            this.folderPath = folderPath;
            this.callback = new WeakReference<>(callback);
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof ImageDownloadTask) {
                return this.tag.equals(((ImageDownloadTask) o).tag);
            }
            return super.equals(o);
        }

        @Override
        public int hashCode() {
            return tag.hashCode();
        }

    }

    private void postSuccess(final ImageDownloadTask task) {
        final Callback callback = task.callback.get();
        if (callback != null) {
            MAIN_HANDLER.post(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccess(task);
                }
            });
        }
        callbacks.remove(task);
    }

    private void postFailure(ImageDownloadTask task, final ImageSaveFailureReason error) {
        final Callback callback = task.callback.get();
        if (callback != null) {
            MAIN_HANDLER.post(new Runnable() {
                @Override
                public void run() {
                    callback.onFailure(error);
                }
            });
        }

        callbacks.remove(task);
    }
}
