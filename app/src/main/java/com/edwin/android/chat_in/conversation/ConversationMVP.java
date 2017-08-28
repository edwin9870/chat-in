package com.edwin.android.chat_in.conversation;

import com.edwin.android.chat_in.data.dto.ConversationDTO;

import java.util.List;

/**
 * Created by Edwin Ramirez Ventura on 8/28/2017.
 */

public interface ConversationMVP {

    interface View {
        void setPresenter(Presenter presenter);
        void showConversation(List<ConversationDTO> conversation);
    }

    interface Presenter {
        void getConversation(int contactId);
    }
}
