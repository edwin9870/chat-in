package com.edwin.android.chat_in.contact;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Edwin Ramirez Ventura on 8/29/2017.
 */

@Singleton
@Component(modules = {ContactPresenterModule.class})
public interface ContactComponent {
    ContactPresenter getPresenter();
}
