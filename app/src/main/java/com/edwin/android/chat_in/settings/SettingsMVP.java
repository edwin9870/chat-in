package com.edwin.android.chat_in.settings;

import android.net.Uri;

/**
 * Created by Edwin Ramirez Ventura on 8/29/2017.
 */

public interface SettingsMVP {
    interface View {
        void setPresenter(Presenter presenter);
    }

    interface Presenter {
        void uploadImage(String fullPathImage);
    }
}
