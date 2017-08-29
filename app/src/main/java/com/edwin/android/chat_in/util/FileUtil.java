package com.edwin.android.chat_in.util;

import android.os.Environment;

/**
 * Created by Edwin Ramirez Ventura on 8/29/2017.
 */

public class FileUtil {

    public static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY
                .equals(state);

    }
}
