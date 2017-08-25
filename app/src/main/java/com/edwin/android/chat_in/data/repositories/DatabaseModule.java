package com.edwin.android.chat_in.data.repositories;

import android.content.ContentResolver;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Edwin Ramirez Ventura on 8/25/2017.
 */

@Module
public class DatabaseModule {

    @Provides @Singleton
    ContentResolver provideContentResolver(Context applicationContext) {
        return applicationContext.getContentResolver();
    }
}
