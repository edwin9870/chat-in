package com.edwin.android.chat_in.chat;

import javax.inject.Inject;

/**
 * Created by Edwin Ramirez Ventura on 8/22/2017.
 */

public class ChatPresenter implements ChatMVP.Presenter {

    private final ChatMVP.View mView;

    @Inject
    public ChatPresenter(ChatMVP.View mView) {
        this.mView = mView;
    }

    @Inject
    public void setupListener() {
        mView.setPresenter(this);
    }
}
