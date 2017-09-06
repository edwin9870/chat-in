package com.edwin.android.chat_in.conversation;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.edwin.android.chat_in.chat.ConversationWrapper;
import com.edwin.android.chat_in.configuration.MyApp;
import com.edwin.android.chat_in.data.ChatInContract;
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
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
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
    private ContentObserver mConversationContentObserver;
    private Disposable mShowAllConversationDisposable;
    private Disposable mChangeTitleDisposable;

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
        mShowAllConversationDisposable = mConversationRepository.getConversations(contactId)
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
    public void keepSyncConversation(Context context, int contactId) {
        mSyncDatabase.syncConversation();
        mConversationContentObserver = new ContentObserver(new Handler()) {

            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange);
                Log.d(TAG, "Uri: "+ uri);
                String id = uri.getPathSegments().get(1);
                mConversationRepository.getConversationById(Long.valueOf(id))
                                    .filter(conversationWrapper ->
                                            conversationWrapper.getSenderContactId() !=
                                                    ContactRepository.OWNER_CONTACT_ID)
                                    .map(conversationDTO -> convertToWrapper(conversationDTO))
                                    .subscribeOn(Schedulers.computation())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(conversationWrapper -> {
                                        Log.d(TAG, "Adding conversation: "+ conversationWrapper);
                                        mView.addConversation(conversationWrapper);
                                    });
                            }
        };
        context.getContentResolver().registerContentObserver(ChatInContract.ConversationEntry.CONTENT_URI, true,
                mConversationContentObserver);
    }

    @Override
    public void setTitle(int contactId) {
        mChangeTitleDisposable = mContactRepository.getContactById(contactId)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(contactDTO -> {
                            if (contactDTO.getUserName() != null && !contactDTO.getUserName()
                                    .isEmpty()) {
                                mView.setTitle(contactDTO.getUserName());
                            } else {
                                mView.setTitle(contactDTO.getNumber());
                            }
                        }

                );
    }

    @Override
    public void destroy(Context context) {
        Log.d(TAG, "Calling onDestroy");

        if(mConversationContentObserver != null) {
            Log.d(TAG, "Unregister mConversationContentObserver");
            context.getContentResolver().unregisterContentObserver(mConversationContentObserver);
        }

        if(mShowAllConversationDisposable != null && !mShowAllConversationDisposable.isDisposed()) {
            Log.d(TAG, "Disposing mShowAllConversationDisposable");
            mShowAllConversationDisposable.dispose();
        }

        if(mChangeTitleDisposable != null && !mChangeTitleDisposable.isDisposed()) {
            Log.d(TAG, "Disposing mChangeTitleDisposable");
            mChangeTitleDisposable.dispose();
        }

    }
}
