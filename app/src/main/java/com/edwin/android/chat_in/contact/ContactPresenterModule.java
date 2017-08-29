package com.edwin.android.chat_in.contact;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Edwin Ramirez Ventura on 8/29/2017.
 */

@Module
public class ContactPresenterModule {

    private ContactMVP.View mView;

    public ContactPresenterModule(ContactMVP.View view) {
        this.mView = view;
    }

    @Provides
    ContactMVP.View provideContactView() {
        return mView;
    }
}
