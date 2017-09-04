package com.edwin.android.chat_in.conversation;


import android.app.Fragment;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.edwin.android.chat_in.R;
import com.edwin.android.chat_in.chat.ConversationWrapper;
import com.edwin.android.chat_in.data.ChatInContract;

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
    @BindView(R.id.linear_layout_send_message)
    LinearLayout mSendMessageLinearLayout;
    @BindView(R.id.fragment_conversation)
    LinearLayout mConversationFragment;
    private int mContactId;
    private ConversationAdapter mAdapter;
    private ConversationMVP.Presenter mPresenter;
    private View mView;

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
            mContactId = getArguments().getInt(BUNDLE_CONTACT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_conversation, container, false);
        Log.d(TAG, "mContactId received:" + mContactId);
        unbinder = ButterKnife.bind(this, mView);
        mAdapter = new ConversationAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        //linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setAdapter(mAdapter);
        mPresenter.setTitle(mContactId);
        mPresenter.getConversation(mContactId);
        mPresenter.keepSyncConversation(mContactId);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        return mView;
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop called");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView called");
        mPresenter.destroy();
        unbinder.unbind();
    }

    @Override
    public void setPresenter(ConversationMVP.Presenter presenter) {
        Log.d(TAG, "Setting presenter");
        mPresenter = presenter;
    }

    @Override
    public void showConversation(List<ConversationWrapper> conversationWrappers) {
        Log.d(TAG, "Conversation to show: " + conversationWrappers);
        mAdapter.setConversations(conversationWrappers);
        scrollToBottom();
    }


    @Override
    public void addConversation(ConversationWrapper conversationWrapper) {
        mAdapter.addConversation(conversationWrapper);
        closeKeyboard();
        Log.d(TAG, "Scrolling to bottom");
        if(mRecyclerView.getAdapter() != null) {
            Log.d(TAG, "itemCount: " + mRecyclerView.getAdapter().getItemCount());
            scrollToBottom();
        }
    }

    private void scrollToBottom() {
        mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount()-1);
    }

    @Override
    public void clearEditText() {
        mMessageToSentEditText.getText().clear();
    }

    @Override
    public void setTitle(String title) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(title);
    }

    @OnClick(R.id.image_view_send_message)
    public void onViewClicked() {
        mPresenter.sendMessage(mMessageToSentEditText.getText().toString(), mContactId);
    }

    private void closeKeyboard() {
        if (getActivity() == null) {
            return;
        }
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService
                (Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(mMessageToSentEditText.getWindowToken(),
                InputMethodManager
                        .HIDE_NOT_ALWAYS);
    }
}
