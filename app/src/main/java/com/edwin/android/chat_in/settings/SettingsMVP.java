package com.edwin.android.chat_in.settings;

import android.net.Uri;

import java.io.File;

/**
 * Created by Edwin Ramirez Ventura on 8/29/2017.
 */

public interface SettingsMVP {
    interface View {
        void setPresenter(Presenter presenter);
        void showImageProfile(int resourceId);
        void showImageProfile(File file);
    }

    interface Presenter {
        void uploadImage(String fullPathImage);
        void loadProfileImage();
    }
}
