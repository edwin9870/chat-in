package com.edwin.android.chat_in.conversation;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edwin.android.chat_in.R;
import com.edwin.android.chat_in.chat.ChatFragment;
import com.edwin.android.chat_in.data.dto.ConversationDTO;
import com.edwin.android.chat_in.data.entity.Message;
import com.edwin.android.chat_in.util.FirebaseDatabaseUtil;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.ReplaySubject;

public class ConversationFragment extends Fragment {

    public static final String ARGUMENT_CHAT = "ARGUMENT_CHAT";
    public static final String TAG = ConversationFragment.class.getSimpleName();
    @BindView(R.id.recycler_conversation)
    RecyclerView mRecyclerView;
    Unbinder unbinder;
    private ConversationDTO mConversationDTO;
    private ConversationAdapter mAdapter;
    private DatabaseReference mDatabase;
    private List<Message> mMessages;
    private boolean meToTargetIsLoaded;
    private boolean targetToMeIsLoaded;

    public ConversationFragment() {

    }

    public static ConversationFragment newInstance(ConversationDTO conversationDTO) {
        ConversationFragment fragment = new ConversationFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARGUMENT_CHAT, conversationDTO);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mConversationDTO = getArguments().getParcelable(ARGUMENT_CHAT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);
        Log.d(TAG, "mConversationDTO received:" + mConversationDTO);
        unbinder = ButterKnife.bind(this, view);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final Date fragmentCreationDateTime = new Date();
        mAdapter = new ConversationAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setAdapter(mAdapter);

        //TODO: Fix this
        String conversationPath = FirebaseDatabaseUtil.Constants.CONVERSATION_ROOT_PATH + ChatFragment.MY_NUMBER + "_" ;
        Log.d(TAG, "conversationPath: " + conversationPath);
        mMessages = new ArrayList<>();
        final DatabaseReference meToTargetConversation = mDatabase.child(conversationPath);
        ReplaySubject<Message> newMessageSubject = ReplaySubject.create();

        Observable<Message> meToTargetObservable = Observable.create(e -> {
            meToTargetConversation.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "child dataSnapshot: " + dataSnapshot);
                    Message message = new Message();
                    message.setMessage(dataSnapshot.child("message").getValue(String.class));
                    message.setSend(new Date(Long.valueOf(dataSnapshot.getKey())*1000));
                    message.setMessageReceived(false);
                    Log.d(TAG, "meToTargetConversation. message to sent " + message);
                    e.onNext(message);

                    if(message.getSend().after(fragmentCreationDateTime)) {
                        newMessageSubject.onNext(message);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            meToTargetConversation.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "meToTargetConversation completed");
                    e.onComplete();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        });


        final DatabaseReference targetToMeConversation = meToTargetConversation
                .getParent().child("_" + ChatFragment.MY_NUMBER);
        final Observable<Message> targetToMeObservable = Observable.create(e -> {
            targetToMeConversation.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "targetToMeConversation.child dataSnapshot: " + dataSnapshot);
                    Message message = new Message();
                    message.setMessage(dataSnapshot.child("message").getValue(String.class));
                    message.setSend(new Date(Long.valueOf(dataSnapshot.getKey())*1000));
                    message.setMessageReceived(true);
                    Log.d(TAG, "message to be added: " + message);
                    e.onNext(message);
                    if(message.getSend().after(fragmentCreationDateTime)) {
                        newMessageSubject.onNext(message);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            targetToMeConversation.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "targetToMeConversation finish");
                    e.onComplete();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        });

        Observable<Message> messages = Observable
                .merge(meToTargetObservable, targetToMeObservable)
                .sorted((message, t1) -> message.getSend().compareTo(t1.getSend()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        messages.subscribeWith(new DisposableObserver<Message>() {
            @Override
            public void onNext(Message message) {
                mMessages.add(message);
            }
            @Override
            public void onError(Throwable e) {
            }
            @Override
            public void onComplete() {
                Log.d(TAG, "Calling onComplete");
                Log.d(TAG, "mMessages: "+ mMessages);
                mAdapter.setContact(mMessages);
            }
        });

        newMessageSubject.observeOn(AndroidSchedulers.mainThread()).subscribe(message -> {
            Log.d(TAG, "New message: " + message);
            mAdapter.message(message);
        });

        return view;
    }

    public void complete(boolean meToTarget) {
        Log.d(TAG, "complete called. meToTarget value: " + meToTarget);
        if (meToTarget) {
            meToTargetIsLoaded = true;
        } else {
            targetToMeIsLoaded = true;
        }

        if (meToTargetIsLoaded && targetToMeIsLoaded) {
            meToTargetIsLoaded = false;
            targetToMeIsLoaded = false;
            Log.d(TAG, "loading conversation");
            mAdapter.setContact(mMessages);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
