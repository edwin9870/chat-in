package com.edwin.android.chat_in.conversation;

import com.edwin.android.chat_in.data.repositories.ContactRepository;
import com.edwin.android.chat_in.data.repositories.ConversationRepository;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Edwin Ramirez Ventura on 8/28/2017.
 */

public class ConversationPresenter implements ConversationMVP.Presenter {

    private final ConversationMVP.View mView;
    private final ConversationRepository mConversationRepository;
    private final ContactRepository mContactRepository;

    @Inject
    public ConversationPresenter(ConversationMVP.View view, ConversationRepository
            conversationRepository, ContactRepository contactRepository) {
        this.mView = view;
        this.mConversationRepository = conversationRepository;
        this.mContactRepository = contactRepository;
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
}
