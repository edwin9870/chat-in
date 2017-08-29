package com.edwin.android.chat_in.contact;

import com.edwin.android.chat_in.configuration.di.ApplicationModule;
import com.edwin.android.chat_in.data.repositories.DatabaseModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Edwin Ramirez Ventura on 8/29/2017.
 */

@Singleton
@Component(modules = {ContactPresenterModule.class, DatabaseModule.class, ApplicationModule.class})
public interface ContactComponent {
    ContactPresenter getPresenter();
}
