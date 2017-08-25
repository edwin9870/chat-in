package com.edwin.android.chat_in.data.sync;

import com.edwin.android.chat_in.configuration.di.ApplicationModule;
import com.edwin.android.chat_in.data.fcm.FcmModule;
import com.edwin.android.chat_in.data.repositories.DatabaseModule;
import com.edwin.android.chat_in.mainview.MainViewActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Edwin Ramirez Ventura on 8/25/2017.
 */
@Singleton
@Component(modules = {DatabaseModule.class, ApplicationModule.class, FcmModule.class})
public interface SyncComponent {
    SyncDatabase getSyncDatabase();
}
