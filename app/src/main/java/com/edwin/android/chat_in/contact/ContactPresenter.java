package com.edwin.android.chat_in.contact;

import android.util.Log;

import com.edwin.android.chat_in.data.dto.ContactDTO;
import com.edwin.android.chat_in.data.repositories.ContactRepository;
import com.edwin.android.chat_in.data.sync.SyncDatabase;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Edwin Ramirez Ventura on 8/29/2017.
 */

public class ContactPresenter implements ContactMVP.Presenter {

    public static final String TAG = ContactPresenter.class.getSimpleName();
    private final ContactMVP.View mView;
    private final ContactRepository mContactRepository;
    private final SyncDatabase mSyncDatabase;

    @Inject
    public ContactPresenter(ContactMVP.View mView, ContactRepository contactRepository,
                            SyncDatabase syncDatabase) {
        this.mView = mView;
        mContactRepository = contactRepository;
        mSyncDatabase = syncDatabase;
    }

    @Inject
    public void setupListener() {
        mView.setPresenter(this);
    }

    @Override
    public void getContacts() {
        mContactRepository.getAllContacts()
                .filter(contactDTO -> !(contactDTO.getUserName() == null || contactDTO.getUserName().isEmpty()))
                .filter(contact -> contact.getId() != ContactRepository.OWNER_CONTACT_ID)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .toList().subscribe(mView::showContacts);
    }

    @Override
    public void syncContact() {
        Log.d(TAG, "Calling syncContacts");
        mSyncDatabase.syncContacts();
    }
}
