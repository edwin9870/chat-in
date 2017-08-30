package com.edwin.android.chat_in.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Edwin Ramirez Ventura on 8/29/2017.
 */

public class FileUtil {



    public static final int IMAGE_QUALITY = 80;
    public static final String IMAGES_DIRECTORY_NAME = "images";
    public static final String TAG = FileUtil.class.getSimpleName();

    public static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY
                .equals(state);

    }

    @Nullable
    public static String saveImage(Context context, URL url) {
        Bitmap image = getBitmap(url);
        String fileName = getFileName(url.toString());
        try {
            saveImage(context, image, fileName);
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String saveImage(Context context, Uri uri) throws IOException {
        Log.d(TAG, "Image's uri: " + uri);
        final String fileName = getFileName(uri.toString());
        final Bitmap image = getBitmap(context, uri);
        saveImage(context, image, fileName);

        return fileName;
    }

    public static void saveImage(Context context, Uri uri, String fileNameToSave) throws IOException {
        Log.d(TAG, "Image's uri: " + uri);
        final Bitmap image = getBitmap(context, uri);
        saveImage(context, image, fileNameToSave);
    }


    private static void saveImage(Context context, Bitmap image, String fileName) throws IOException {
        FileOutputStream outputStream;
        ContextWrapper cw = new ContextWrapper(context);
        final File imagesDirectory = cw.getDir(IMAGES_DIRECTORY_NAME, Context.MODE_PRIVATE);
        File imageFile = new File(imagesDirectory, fileName);
        outputStream = new FileOutputStream(imageFile);
        switch (getFileExtension(fileName).toUpperCase()) {
            case "JPEG":
            image.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, outputStream);
            break;
            case "JPG":
                image.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, outputStream);
                break;
            case "PNG":
                image.compress(Bitmap.CompressFormat.PNG, IMAGE_QUALITY, outputStream);
                break;
        }
        outputStream.close();
    }


    @Nullable
    private static Bitmap getBitmap(URL url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    private static Bitmap getBitmap(Context context, Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    @NonNull
    private static String getFileName(String uri) {
        return uri.substring(uri.lastIndexOf('/') + 1);
    }

    /**
     * Return the file's extension. Ex: jpg
     * @param fileName
     * @return
     */
    @NonNull
    public static String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") +1,
                fileName.length());
    }

    @NonNull
    public static File getImageFile(Context context, String fileName) {
        ContextWrapper cw = new ContextWrapper(context);
        final File imagesDirectory = cw.getDir(IMAGES_DIRECTORY_NAME, Context.MODE_PRIVATE);
        return new File(imagesDirectory, fileName);
    }

}
