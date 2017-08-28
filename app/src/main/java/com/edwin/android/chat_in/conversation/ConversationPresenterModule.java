package com.edwin.android.chat_in.conversation;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Edwin Ramirez Ventura on 8/28/2017.
 */

@Module
public class ConversationPresenterModule {

    private ConversationMVP.View mView;

    public ConversationPresenterModule(ConversationMVP.View view) {
        this.mView = view;
    }

    @Provides
    ConversationMVP.View provideConversationView() {
        return mView;
    }
}
