package com.edwin.android.chat_in.chat;

import com.edwin.android.chat_in.data.dto.ConversationDTO;

import java.util.List;

/**
 * Created by Edwin Ramirez Ventura on 8/22/2017.
 */

public interface ChatMVP {

    interface View {
        void setPresenter(Presenter presenter);
        void showChats(List<ConversationDTO> conversations);
    }

    interface Presenter {
        void getChats();
    }
}
