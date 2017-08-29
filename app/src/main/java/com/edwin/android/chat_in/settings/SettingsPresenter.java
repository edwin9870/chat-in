package com.edwin.android.chat_in.settings;

import javax.inject.Inject;

/**
 * Created by Edwin Ramirez Ventura on 8/29/2017.
 */

public class SettingsPresenter implements SettingsMVP.Presenter {

    private final SettingsMVP.View mView;

    @Inject
    public SettingsPresenter(SettingsMVP.View view) {
        mView = view;
    }

    @Inject
    public void setupListener() {
        mView.setPresenter(this);
    }
}
