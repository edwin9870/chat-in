package com.edwin.android.chat_in.settings;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Edwin Ramirez Ventura on 8/29/2017.
 */

@Module
public class SettingsPresenterModule {

    private SettingsMVP.View mView;

    public SettingsPresenterModule(SettingsMVP.View view) {
        this.mView = view;
    }

    @Provides
    SettingsMVP.View provideSettingsView() {
        return mView;
    }
}
