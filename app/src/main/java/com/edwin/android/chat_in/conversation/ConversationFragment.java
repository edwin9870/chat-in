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
import com.edwin.android.chat_in.entity.Contact;
import com.edwin.android.chat_in.entity.Message;
import com.edwin.android.chat_in.entity.dto.Chat;
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

public class ConversationFragment extends Fragment {

    public static final String ARGUMENT_CHAT = "ARGUMENT_CHAT";
    public static final String TAG = ConversationFragment.class.getSimpleName();
    @BindView(R.id.recycler_conversation)
    RecyclerView mRecyclerView;
    Unbinder unbinder;
    private Chat mChat;
    private ConversationAdapter mAdapter;
    private DatabaseReference mDatabase;
    private List<Message> mMessages;
    private boolean meToTargetIsLoaded;
    private boolean targetToMeIsLoaded;

    public ConversationFragment() {

    }

    public static ConversationFragment newInstance(Chat chat) {
        ConversationFragment fragment = new ConversationFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARGUMENT_CHAT, chat);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mChat = getArguments().getParcelable(ARGUMENT_CHAT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);
        Log.d(TAG, "mChat received:" + mChat);
        unbinder = ButterKnife.bind(this, view);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAdapter = new ConversationAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setAdapter(mAdapter);


        String conversationPath = "conversation/" + ChatFragment.MY_NUMBER + "_" + mChat
                .getPhoneNumber();
        Log.d(TAG, "conversationPath: " + conversationPath);
        mMessages = new ArrayList<>();
        final DatabaseReference meToTargetConversation = mDatabase.child(conversationPath);
        meToTargetConversation.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "child dataSnapshot: " + dataSnapshot);
                Message message = new Message();
                message.setMessage(dataSnapshot.child("message").getValue(String.class));
                message.setSend(new Date(Long.valueOf(dataSnapshot.getKey())));
                message.setMessageReceived(false);
                Log.d(TAG, "message to be added: " + message);
                mMessages.add(message);
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
        meToTargetConversation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "meToTargetConversation finish");
                complete(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        DatabaseReference targetToMeConversation = meToTargetConversation
                .getParent().child(mChat.getPhoneNumber() + "_" + ChatFragment.MY_NUMBER);
        targetToMeConversation.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "targetToMeConversation.child dataSnapshot: " + dataSnapshot);
                Message message = new Message();
                message.setMessage(dataSnapshot.child("message").getValue(String.class));
                message.setSend(new Date(Long.valueOf(dataSnapshot.getKey())));
                message.setMessageReceived(true);
                Log.d(TAG, "message to be added: " + message);
                mMessages.add(message);
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
        targetToMeConversation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "targetToMeConversation finish");
                complete(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
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
