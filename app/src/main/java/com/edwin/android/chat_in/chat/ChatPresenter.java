package com.edwin.android.chat_in.chat;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;

import com.edwin.android.chat_in.data.ChatInContract;
import com.edwin.android.chat_in.data.dto.ContactDTO;
import com.edwin.android.chat_in.data.entity.Contact;
import com.edwin.android.chat_in.data.repositories.ContactRepository;
import com.edwin.android.chat_in.data.repositories.ConversationRepository;
import com.edwin.android.chat_in.data.sync.SyncDatabase;
import com.edwin.android.chat_in.util.ResourceUtil;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Edwin Ramirez Ventura on 8/22/2017.
 */

public class ChatPresenter implements ChatMVP.Presenter {

    public static final String TAG = ChatPresenter.class.getSimpleName();
    private final ChatMVP.View mView;
    private final ConversationRepository mConversationRepository;
    private final ContactRepository mContactRepository;
    private final SyncDatabase mSyncDatabase;
    private Disposable chatDisposable;
    private ContentObserver conversationContentObserver;
    private ContentObserver mContantContentObserver;

    @Inject
    public ChatPresenter(ChatMVP.View mView,
                         ConversationRepository conversationRepository,
                         ContactRepository contactRepository,
                         SyncDatabase syncDatabase) {
        this.mView = mView;
        this.mConversationRepository = conversationRepository;
        mContactRepository = contactRepository;
        mSyncDatabase = syncDatabase;
    }

    @Inject
    public void setupListener() {
        mView.setPresenter(this);
    }

    @Override
    public void getChats() {
        Log.d(TAG, "Calling getChats");
        mConversationRepository.getLastMessages()
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

    @Override
    public void keepChatSync(Context context) {
        Log.d(TAG, "Calling keepChatSync");
        conversationContentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                Log.d(TAG, "New conversation changes, updating chat....");
                getChats();
            }
        };
        context.getContentResolver().registerContentObserver(ChatInContract.ConversationEntry.CONTENT_URI,
                true, conversationContentObserver);

        mContantContentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                Log.d(TAG, "Refreshing chat by contact change");
                getChats();
            }
        };
        context.getContentResolver().registerContentObserver(ChatInContract.ContactEntry.CONTENT_URI, true, mContantContentObserver);
    }

    @Override
    public void cleanResources(Context context) {
        Log.d(TAG, "Cleaning resources");
        if(chatDisposable != null && !chatDisposable.isDisposed()) {
            Log.d(TAG, "Disposing conversation");
            chatDisposable.dispose();
        }

        if(conversationContentObserver != null) {
            Log.d(TAG, "unregisterContentObserver conversationContentObserver");
            context.getContentResolver().unregisterContentObserver(conversationContentObserver);
        }

        if(mContantContentObserver != null) {
            Log.d(TAG, "unregisterContentObserver mContantContentObserver");
            context.getContentResolver().unregisterContentObserver(mContantContentObserver);
        }
    }
}
