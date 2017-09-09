package com.edwin.android.chat_in.contact;

import android.content.Context;
import android.util.Log;

import com.edwin.android.chat_in.R;
import com.edwin.android.chat_in.data.repositories.ContactRepository;
import com.edwin.android.chat_in.data.sync.SyncDatabase;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Edwin Ramirez Ventura on 8/29/2017.
 */

public class ContactPresenter implements ContactMVP.Presenter {

    public static final String TAG = ContactPresenter.class.getSimpleName();
    private final ContactMVP.View mView;
    private final ContactRepository mContactRepository;
    private final SyncDatabase mSyncDatabase;
    private final Context mContext;
    private Disposable mUpdatingContactsImage;

    @Inject
    public ContactPresenter(ContactMVP.View mView, ContactRepository contactRepository,
                            SyncDatabase syncDatabase, Context context) {
        this.mView = mView;
        mContactRepository = contactRepository;
        mSyncDatabase = syncDatabase;
        mContext = context;
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
    public void cleanResource(Context context) {
        Log.d(TAG, "Calling cleanResource");

        if(mUpdatingContactsImage != null && !mUpdatingContactsImage.isDisposed()) {
            Log.d(TAG, "mUpdatingContactsImage.dispose();");
            mUpdatingContactsImage.dispose();
        }
    }

    @Override
    public void refreshContacts() {
        Log.d(TAG, "Starting to refresh contacts");
        Log.d(TAG, "updateContactsImage");
        mView.showMessage(mContext.getString(R.string.refreshing_contact_message));
        mSyncDatabase.syncContacts()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
            Log.d(TAG, "SyncContacts finished, updating contacts");
            getContacts();
            mUpdatingContactsImage = mSyncDatabase.updateContactsImage()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                Log.d(TAG, "Contacts profile images updated");
                getContacts();
            });
        });
    }
}
