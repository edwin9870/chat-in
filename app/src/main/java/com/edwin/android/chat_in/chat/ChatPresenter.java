package com.edwin.android.chat_in.chat;

import android.content.Context;
import android.util.Log;

import com.edwin.android.chat_in.data.dto.ContactDTO;
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

    @Override
    public void keepChatSync(Context context) {
        Log.d(TAG, "Calling keepChatSync");
        chatDisposable = mSyncDatabase.getNewConversations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(conversationDTO -> {
                    Log.d(TAG, "New conversation: " + conversationDTO + ", updating chats");
                    getChats();
                });
    }

    @Override
    public void destroy() {
        Log.d(TAG, "Cleaning resources");
        if(chatDisposable != null && !chatDisposable.isDisposed()) {
            Log.d(TAG, "Disposing conversation");
            chatDisposable.dispose();
        }
    }
}
