package com.edwin.android.chat_in.chat;

import android.util.Log;

import com.edwin.android.chat_in.data.dto.ContactDTO;
import com.edwin.android.chat_in.data.repositories.ContactRepository;
import com.edwin.android.chat_in.data.repositories.ConversationRepository;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Edwin Ramirez Ventura on 8/22/2017.
 */

public class ChatPresenter implements ChatMVP.Presenter {

    public static final String TAG = ChatPresenter.class.getSimpleName();
    private final ChatMVP.View mView;
    private final ConversationRepository mConversationRepository;
    private final ContactRepository mContactRepository;

    @Inject
    public ChatPresenter(ChatMVP.View mView, ConversationRepository conversationRepository, ContactRepository contactRepository) {
        this.mView = mView;
        this.mConversationRepository = conversationRepository;
        mContactRepository = contactRepository;
    }

    @Inject
    public void setupListener() {
        mView.setPresenter(this);
    }

    @Override
    public void getChats() {
        Log.d(TAG, "Calling getChats");
        mConversationRepository.getLastMessages()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .map(conversationDTO -> {
                    int contactId = conversationDTO.getRecipientContactId() == ContactRepository.OWNER_CONTACT_ID
                            ? conversationDTO.getSenderContactId()
                            : conversationDTO.getRecipientContactId();
                    final ContactDTO contact = mContactRepository.getContactById
                            (contactId).blockingGet();
                    return new ConversationWrapper(conversationDTO, contact);
                }).toList()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mView::showChats);

    }
}
