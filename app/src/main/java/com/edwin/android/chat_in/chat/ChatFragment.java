package com.edwin.android.chat_in.chat;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edwin.android.chat_in.R;
import com.edwin.android.chat_in.entity.dto.Chat;
import com.edwin.android.chat_in.entity.fcm.LastMessage;
import com.edwin.android.chat_in.util.FirebaseDatabaseUtil;
import com.edwin.android.chat_in.util.ResourceUtil;
import com.edwin.android.chat_in.views.SpacesItemDecoration;
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


public class ChatFragment extends Fragment implements ChatListener {

    public static final String TAG = ChatFragment.class.getSimpleName();
    @BindView(R.id.recycler_view_chat)
    RecyclerView mRecyclerView;
    Unbinder mUnbinder;
    private ChatAdapter mChatAdapter;
    private DatabaseReference mDatabase;

    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mChatAdapter = new ChatAdapter(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setAdapter(mChatAdapter);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(ResourceUtil.dpToPx(this
                .getActivity(), getResources().getInteger(R.integer.space_between_chat_list))));

        String actualPhoneNumber = "8292779870";
        mDatabase.child(FirebaseDatabaseUtil.Constants.CHATS_ROOT_PATH).startAt(null,
                actualPhoneNumber).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<Chat> chats = new ArrayList<>();
                Log.d(TAG, "key: " + dataSnapshot);
                for (DataSnapshot singleDataSnapshot : dataSnapshot.getChildren()) {
                    String key = singleDataSnapshot.getKey();
                    String targetNumber = key.substring(key.indexOf("_") + 1);
                    Log.d(TAG, "singleDataSnapshot: " + targetNumber);
                    final LastMessage lastMessage = singleDataSnapshot.getValue(LastMessage.class);
                    Log.d(TAG, "dataSnapshot: " + lastMessage);

                    mDatabase.child(FirebaseDatabaseUtil.Constants.USERS_ROOT_PATH +
                            targetNumber).child(FirebaseDatabaseUtil.Constants.USER_NAME_PATH)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Log.d(TAG, "userName: " + dataSnapshot.getValue(String.class));
                                    Chat chat = new Chat();
                                    chat.setUserName(dataSnapshot.getValue(String.class));
                                    chat.setProfileImage(R.drawable.ic_women_image);
                                    chat.setLastMessage(lastMessage.getLastMessage());
                                    chat.setMessageDate(new Date(lastMessage.getTimestamp()));
                                    Log.d(TAG, "chat: " + chat);
                                    chats.add(chat);
                                    mChatAdapter.setChats(chats);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
            }


        });

        Log.d(TAG, "finish onCreateView");

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onClickContact(Chat chat) {
        Log.d(TAG, "Chat clicked: " + chat);
    }
}