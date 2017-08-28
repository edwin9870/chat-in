package com.edwin.android.chat_in.conversation;

import com.edwin.android.chat_in.configuration.di.ApplicationModule;
import com.edwin.android.chat_in.data.repositories.DatabaseModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Edwin Ramirez Ventura on 8/28/2017.
 */

@Singleton
@Component(modules = {ConversationPresenterModule.class, DatabaseModule.class, ApplicationModule.class})
public interface ConversationComponent {
    ConversationPresenter getPresenter();
}
