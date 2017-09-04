package com.edwin.android.chat_in.conversation;

import com.edwin.android.chat_in.chat.ConversationWrapper;

import java.util.List;

import io.reactivex.annotations.NonNull;

/**
 * Created by Edwin Ramirez Ventura on 8/28/2017.
 */

public interface ConversationMVP {

    interface View {
        void setPresenter(Presenter presenter);
        void showConversation(List<ConversationWrapper> conversationWrappers);
        void addConversation(ConversationWrapper conversationWrapper);
        void clearEditText();
        void setTitle(String title);
    }

    interface Presenter {
        void getConversation(int contactId);
        void sendMessage(@NonNull String message, @NonNull int recipientContactId);
        void keepSyncConversation(int contactId);
        void setTitle(int contactId);
        void destroy();
    }
}
