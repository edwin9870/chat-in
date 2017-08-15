package com.edwin.android.chat_in.chat;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edwin.android.chat_in.R;
import com.edwin.android.chat_in.contact.ContactListener;
import com.edwin.android.chat_in.conversation.ConversationActivity;
import com.edwin.android.chat_in.conversation.ConversationFragment;
import com.edwin.android.chat_in.entity.Contact;
import com.edwin.android.chat_in.util.MessageUtil;
import com.edwin.android.chat_in.util.ResourceUtil;
import com.edwin.android.chat_in.views.SpacesItemDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class ChatFragment extends Fragment implements ContactListener {

    @BindView(R.id.recycler_view_chat)
    RecyclerView mRecyclerView;
    Unbinder mUnbinder;
    private ChatAdapter mChatAdapter;

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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setAdapter(mChatAdapter);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(ResourceUtil.dpToPx(this
                .getActivity(), getResources().getInteger(R.integer.space_between_chat_list))));

        mChatAdapter.setContacts(MessageUtil.getContacts());

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
    public void onClickContact(Contact contact) {
        Log.d(ChatFragment.class.getSimpleName(), "Contact clicked: " + contact.getName());
        Intent intent = new Intent(getActivity(), ConversationActivity.class);
        intent.putExtra(ConversationFragment.ARGUMENT_CONTACT, contact);
        getActivity().startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
