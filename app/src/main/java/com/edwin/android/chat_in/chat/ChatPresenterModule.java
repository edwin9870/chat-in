package com.edwin.android.chat_in.chat;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Edwin Ramirez Ventura on 8/22/2017.
 */

@Module
public class ChatPresenterModule {

    private ChatMVP.View mView;

    public ChatPresenterModule(ChatMVP.View view) {
        this.mView = view;
    }

    @Provides
    ChatMVP.View provideChatView() {
        return mView;
    }
}
