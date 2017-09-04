package com.edwin.android.chat_in.conversation;

import android.support.annotation.NonNull;
import android.util.Log;

import com.edwin.android.chat_in.chat.ConversationWrapper;
import com.edwin.android.chat_in.data.dto.ContactDTO;
import com.edwin.android.chat_in.data.dto.ConversationDTO;
import com.edwin.android.chat_in.data.fcm.ConversationRepositoryFcm;
import com.edwin.android.chat_in.data.repositories.ContactRepository;
import com.edwin.android.chat_in.data.repositories.ConversationRepository;
import com.edwin.android.chat_in.data.sync.SyncDatabase;

import java.util.Date;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
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
    private final SyncDatabase mSyncDatabase;

    @Inject
    public ConversationPresenter(ConversationMVP.View view,
                                 ConversationRepository conversationRepository,
                                 ContactRepository contactRepository,
                                 ConversationRepositoryFcm conversationRepositoryFcm,
                                 SyncDatabase syncDatabase) {
        this.mView = view;
        this.mConversationRepository = conversationRepository;
        this.mContactRepository = contactRepository;
        mConversationRepositoryFcm = conversationRepositoryFcm;
        mSyncDatabase = syncDatabase;
    }

    @Inject
    public void setupListener() {
        mView.setPresenter(this);
    }

    @Override
    public void getConversation(int contactId) {
        mConversationRepository.getConversations(contactId)
                .map(this::convertToWrapper)
                .toList()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mView::showConversation);
    }

    @NonNull
    private ConversationWrapper convertToWrapper(ConversationDTO conversationDTO) {
        int contactId1 = conversationDTO.getRecipientContactId() == ContactRepository.OWNER_CONTACT_ID
                ? conversationDTO.getSenderContactId()
                : conversationDTO.getRecipientContactId();
        final ContactDTO contact = mContactRepository.getContactById
                (contactId1).blockingGet();
        return new ConversationWrapper(conversationDTO, contact);
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
            Single.create((SingleOnSubscribe<ConversationWrapper>) e -> e.onSuccess(convertToWrapper(conversation)))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(conversationWrapper -> {
                    mView.addConversation(conversationWrapper);
                    mView.clearEditText();
                });

        });

    }

    @Override
    public void keepSyncConversation(int contactId) {
        mSyncDatabase.sync();
        mSyncDatabase.getNewConversations()
                .map(this::convertToWrapper)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(conversationWrapper -> {
                    Log.d(TAG, "New conversationWrapper received: "+ conversationWrapper);
                    if(conversationWrapper.getConversation().getSenderContactId() != ContactRepository.OWNER_CONTACT_ID) {
                        Log.d(TAG, "If sender is not the owner, refresh chat");
                        mView.addConversation(conversationWrapper);
                    }
                });
    }

    @Override
    public void setTitle(int contactId) {
        mContactRepository.getContactById(contactId)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(contactDTO -> mView.setTitle(contactDTO.getUserName()));
    }
}
