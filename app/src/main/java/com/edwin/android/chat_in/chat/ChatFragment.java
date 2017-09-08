package com.edwin.android.chat_in.chat;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.edwin.android.chat_in.R;
import com.edwin.android.chat_in.conversation.ConversationActivity;
import com.edwin.android.chat_in.conversation.ConversationFragment;
import com.edwin.android.chat_in.settings.SettingsActivity;
import com.edwin.android.chat_in.util.ResourceUtil;
import com.edwin.android.chat_in.views.SpacesItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class ChatFragment extends Fragment implements ChatListener , ChatMVP.View{

    public static final String TAG = ChatFragment.class.getSimpleName();
    @BindView(R.id.recycler_view_chat)
    RecyclerView mRecyclerView;
    Unbinder mUnbinder;
    private ChatAdapter mChatAdapter;
    private ChatMVP.Presenter mPresenter;

    public ChatFragment() {
    }

    @Override
    public void setPresenter(ChatMVP.Presenter presenter) {
        Log.d(TAG, "Setting presenter");
        mPresenter = presenter;
    }

    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        setHasOptionsMenu(true);
        mUnbinder = ButterKnife.bind(this, view);
        mChatAdapter = new ChatAdapter(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setAdapter(mChatAdapter);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(ResourceUtil.dpToPx(this
                .getActivity(), getResources().getInteger(R.integer.space_between_chat_list))));
        mPresenter.getChats();

        Log.d(TAG, "finish onCreateView");
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.keepChatSync(getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.cleanResources(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main_view, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_setting_action:
                final Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void showChats(List<ConversationWrapper> conversations) {
        Log.d(TAG, "Showing list of chats with the followings conversations: " + conversations);
        mChatAdapter.setChats(conversations);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        mPresenter.cleanResources(getActivity());
    }

    @Override
    public void onClickContact(int contactId) {
        Log.d(TAG, "contactId clicked: " + contactId);
        Intent intent = new Intent(getActivity(), ConversationActivity.class);
        intent.putExtra(ConversationFragment.BUNDLE_CONTACT_ID, contactId);
        getActivity().startActivity(intent);
    }
}
