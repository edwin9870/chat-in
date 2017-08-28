package com.edwin.android.chat_in.chat;

import android.util.Log;

import com.edwin.android.chat_in.data.dto.ConversationDTO;
import com.edwin.android.chat_in.data.repositories.ConversationRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Edwin Ramirez Ventura on 8/22/2017.
 */

public class ChatPresenter implements ChatMVP.Presenter {

    public static final String TAG = ChatPresenter.class.getSimpleName();
    private final ChatMVP.View mView;
    private final ConversationRepository mConversationRepository;

    @Inject
    public ChatPresenter(ChatMVP.View mView, ConversationRepository conversationRepository) {
        this.mView = mView;
        this.mConversationRepository = conversationRepository;
    }

    @Inject
    public void setupListener() {
        mView.setPresenter(this);
    }

    @Override
    public void getChats() {
        Log.d(TAG, "Calling getChats");
        mConversationRepository.getLastMessages()
                .observeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .toList().subscribe(new Consumer<List<ConversationDTO>>() {
            @Override
            public void accept(List<ConversationDTO> conversations) throws Exception {
                Log.d(TAG, "ConversationDTOS list: "+ conversations);
                mView.showChats(conversations);
            }
        });

    }
}
