package com.edwin.android.chat_in.conversation;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.edwin.android.chat_in.R;
import com.edwin.android.chat_in.data.dto.ConversationDTO;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ConversationFragment extends Fragment implements ConversationMVP.View {

    public static final String BUNDLE_CONTACT_ID = "BUNDLE_CONTACT_ID";
    public static final String TAG = ConversationFragment.class.getSimpleName();
    @BindView(R.id.recycler_conversation)
    RecyclerView mRecyclerView;
    Unbinder unbinder;
    @BindView(R.id.image_view_send_message)
    ImageView mSendMessageImageView;
    @BindView(R.id.edit_text_message_to_sent)
    EditText mMessageToSentEditText;
    private int recipientContactId;
    private ConversationAdapter mAdapter;
    private ConversationMVP.Presenter mPresenter;

    public ConversationFragment() {

    }

    public static ConversationFragment newInstance(int contactId) {
        ConversationFragment fragment = new ConversationFragment();
        Bundle args = new Bundle();
        args.putInt(BUNDLE_CONTACT_ID, contactId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipientContactId = getArguments().getInt(BUNDLE_CONTACT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);
        Log.d(TAG, "recipientContactId received:" + recipientContactId);
        unbinder = ButterKnife.bind(this, view);
        mAdapter = new ConversationAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setAdapter(mAdapter);
        mPresenter.getConversation(recipientContactId);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void setPresenter(ConversationMVP.Presenter presenter) {
        Log.d(TAG, "Setting presenter");
        mPresenter = presenter;
    }

    @Override
    public void showConversation(List<ConversationDTO> conversation) {
        Log.d(TAG, "Conversation to show: " + conversation);
        mAdapter.setConversations(conversation);
    }

    @Override
    public void addConversation(ConversationDTO conversation) {
        mAdapter.addConversation(conversation);
    }

    @Override
    public void clearEditText() {
        mMessageToSentEditText.getText().clear();
    }

    @OnClick(R.id.image_view_send_message)
    public void onViewClicked() {
        mPresenter.sendMessage(mMessageToSentEditText.getText().toString(), recipientContactId);
    }
}
