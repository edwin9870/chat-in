package com.edwin.android.chat_in.settings;

import com.edwin.android.chat_in.configuration.di.ApplicationModule;
import com.edwin.android.chat_in.data.fcm.FcmModule;
import com.edwin.android.chat_in.data.repositories.DatabaseModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Edwin Ramirez Ventura on 8/29/2017.
 */

@Singleton
@Component(modules = {SettingsPresenterModule.class, DatabaseModule.class, ApplicationModule.class, FcmModule.class})
public interface SettingsComponent {
    SettingsPresenter getPresenter();
}
