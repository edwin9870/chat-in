package com.edwin.android.chat_in.chat;

import com.edwin.android.chat_in.data.dto.ConversationDTO;

/**
 * Created by Edwin Ramirez Ventura on 8/14/2017.
 */

public interface ChatListener {
    void onClickContact(ConversationDTO contact);
}
