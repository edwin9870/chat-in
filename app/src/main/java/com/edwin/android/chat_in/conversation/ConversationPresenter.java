package com.edwin.android.chat_in.conversation;

import android.util.Log;

import com.edwin.android.chat_in.data.dto.ConversationDTO;
import com.edwin.android.chat_in.data.fcm.ConversationRepositoryFcm;
import com.edwin.android.chat_in.data.repositories.ContactRepository;
import com.edwin.android.chat_in.data.repositories.ConversationRepository;

import java.util.Date;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Edwin Ramirez Ventura on 8/28/2017.
 */

public class ConversationPresenter implements ConversationMVP.Presenter {

    public static final String TAG = ConversationPresenter.class.getSimpleName();
    private final ConversationMVP.View mView;
    private final ConversationRepository mConversationRepository;
    private final ContactRepository mContactRepository;
    private final ConversationRepositoryFcm mConversationRepositoryFcm;

    @Inject
    public ConversationPresenter(ConversationMVP.View view, ConversationRepository
            conversationRepository, ContactRepository contactRepository, ConversationRepositoryFcm conversationRepositoryFcm) {
        this.mView = view;
        this.mConversationRepository = conversationRepository;
        this.mContactRepository = contactRepository;
        mConversationRepositoryFcm = conversationRepositoryFcm;
    }

    @Inject
    public void setupListener() {
        mView.setPresenter(this);
    }

    @Override
    public void getConversation(int contactId) {
        mConversationRepository.getConversations(contactId)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .toList().subscribe(mView::showConversation);
    }

    @Override
    public void sendMessage(String message, int recipientContactId) {
        Log.d(TAG, "Message to sent:" + message);
        if(message == null || message.isEmpty()) {
            Log.d(TAG, "Avoid to send a empty message");
            return;
        }
        final ConversationDTO conversation = new ConversationDTO();
        conversation.setMessage(message);
        conversation.setMessageDate(new Date().getTime());
        conversation.setSenderContactId(ContactRepository.OWNER_CONTACT_ID);
        conversation.setRecipientContactId(recipientContactId);
        Log.d(TAG, "Conversation to send: " + conversation);

        mConversationRepositoryFcm.persist(conversation)
                .subscribeOn(Schedulers.computation())
                .subscribe();
        mConversationRepository.persist(conversation)
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(aLong -> {
            Log.d(TAG, "Start to add conversation and clear editText");
            mView.addConversation(conversation);
            mView.clearEditText();
        });




    }
}
