package com.edwin.android.chat_in.data.repositories;

import com.edwin.android.chat_in.configuration.di.ApplicationModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Edwin Ramirez Ventura on 8/25/2017.
 */

@Singleton
@Component(modules = {DatabaseModule.class, ApplicationModule.class})
public interface DatabaseComponent {
    ContactRepository getContactRepository();
    ConversationRepository getConversationRepository();
}
