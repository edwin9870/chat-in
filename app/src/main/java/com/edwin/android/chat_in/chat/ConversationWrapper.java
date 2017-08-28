package com.edwin.android.chat_in.chat;

import com.edwin.android.chat_in.data.dto.ContactDTO;
import com.edwin.android.chat_in.data.dto.ConversationDTO;

import java.util.List;

/**
 * Created by Edwin Ramirez Ventura on 8/28/2017.
 */

public class ConversationWrapper {
    private ConversationDTO conversation;
    private ContactDTO contact;

    public ConversationWrapper() {
    }

    public ConversationWrapper(ConversationDTO conversation, ContactDTO contact) {
        this.conversation = conversation;
        this.contact = contact;
    }

    public ConversationDTO getConversation() {
        return conversation;
    }

    public void setConversation(ConversationDTO conversation) {
        this.conversation = conversation;
    }

    public ContactDTO getContact() {
        return contact;
    }

    public void setContact(ContactDTO contact) {
        this.contact = contact;
    }
}
