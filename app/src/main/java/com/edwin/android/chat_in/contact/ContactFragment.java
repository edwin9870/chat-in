package com.edwin.android.chat_in.contact;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.edwin.android.chat_in.R;
import com.edwin.android.chat_in.conversation.ConversationActivity;
import com.edwin.android.chat_in.conversation.ConversationFragment;
import com.edwin.android.chat_in.data.dto.ContactDTO;
import com.edwin.android.chat_in.settings.SettingsActivity;
import com.edwin.android.chat_in.util.ResourceUtil;
import com.edwin.android.chat_in.views.SpacesItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class ContactFragment extends Fragment implements ContactListener, ContactMVP.View {


    public static final String TAG = ContactFragment.class.getSimpleName();
    @BindView(R.id.recycler_view_contact)
    RecyclerView mRecyclerView;
    Unbinder mUnbinder;
    private ContactAdapter mAdapter;
    private ContactMVP.Presenter mPresenter;

    public ContactFragment() {
    }

    public static ContactFragment newInstance() {
        return new ContactFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        setHasOptionsMenu(true);
        mUnbinder = ButterKnife.bind(this, view);
        mAdapter = new ContactAdapter(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(ResourceUtil.dpToPx(this
                .getActivity(), getResources().getInteger(R.integer.space_between_chat_list))));
        mPresenter.syncContacts();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.cleanResource(getActivity());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        mPresenter.cleanResource(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_contact, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_setting_action:
                final Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.item_refresh_action:
                showMessage(getActivity().getString(R.string.refreshing_contact_message));
                mPresenter.refreshContacts();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void setPresenter(ContactMVP.Presenter presenter) {
        Log.d(TAG, "Setting presenter");
        mPresenter = presenter;
    }

    @Override
    public void showContacts(List<ContactDTO> contacts) {
        Log.d(TAG, "contacts to show: "+ contacts);
        mAdapter.setContacts(contacts);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClickContact(ContactDTO contact) {
        Intent intent = new Intent(getActivity(), ConversationActivity.class);
        intent.putExtra(ConversationFragment.BUNDLE_CONTACT_ID, contact.getId());
        getActivity().startActivity(intent);
    }
}
