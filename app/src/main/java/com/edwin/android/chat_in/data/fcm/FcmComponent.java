package com.edwin.android.chat_in.data.fcm;

import com.edwin.android.chat_in.configuration.di.ApplicationModule;
import com.edwin.android.chat_in.data.repositories.DatabaseModule;
import com.google.firebase.database.DatabaseReference;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Edwin Ramirez Ventura on 8/25/2017.
 */
@Singleton
@Component(modules = {FcmModule.class, DatabaseModule.class, ApplicationModule.class})
public interface FcmComponent {
    DatabaseReference getDatabaseReference();
    ConversationRepositoryFcm getConversationRepositoryFcm();
    ContactRepositoryFcm getContactRepositoryFcm();
}
