package com.edwin.android.chat_in.contact;

import javax.inject.Inject;

/**
 * Created by Edwin Ramirez Ventura on 8/29/2017.
 */

public class ContactPresenter implements ContactMVP.Presenter {

    private final ContactMVP.View mView;

    @Inject
    public ContactPresenter(ContactMVP.View mView) {
        this.mView = mView;
    }

    @Inject
    public void setupListener() {
        mView.setPresenter(this);
    }
}
