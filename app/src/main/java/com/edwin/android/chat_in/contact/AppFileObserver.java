package com.edwin.android.chat_in.contact;

import android.support.annotation.Nullable;
import android.util.Log;

import com.edwin.android.chat_in.util.FileUtil;

/**
 * Created by Edwin Ramirez Ventura on 9/8/2017.
 */

public class AppFileObserver extends android.os.FileObserver {

    public AppFileObserver(String path) {
        super(path);
    }

    @Override
    public void onEvent(int event, @Nullable String path) {
        if(path.contains(FileUtil.IMAGES_DIRECTORY_NAME)) {
            Log.d(AppFileObserver.class.getSimpleName(), "File create, path: " + path);
        }
    }
}
