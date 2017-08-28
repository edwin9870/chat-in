package com.edwin.android.chat_in.chat;

import com.edwin.android.chat_in.configuration.di.ApplicationModule;
import com.edwin.android.chat_in.data.repositories.DatabaseComponent;
import com.edwin.android.chat_in.data.repositories.DatabaseModule;
import com.edwin.android.chat_in.util.FragmentScoped;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Edwin Ramirez Ventura on 8/22/2017.
 */

@Singleton
@Component(modules = {ChatPresenterModule.class, DatabaseModule.class, ApplicationModule.class})
public interface ChatComponent {
    ChatPresenter getChatPresenter();
}
