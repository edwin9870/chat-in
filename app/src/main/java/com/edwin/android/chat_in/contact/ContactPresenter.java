package com.edwin.android.chat_in.contact;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.edwin.android.chat_in.data.ChatInContract;
import com.edwin.android.chat_in.data.repositories.ContactRepository;
import com.edwin.android.chat_in.data.sync.SyncDatabase;
import com.edwin.android.chat_in.util.FileUtil;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Edwin Ramirez Ventura on 8/29/2017.
 */

public class ContactPresenter implements ContactMVP.Presenter {

    public static final String TAG = ContactPresenter.class.getSimpleName();
    private final ContactMVP.View mView;
    private final ContactRepository mContactRepository;
    private final SyncDatabase mSyncDatabase;
    private ContentObserver mContactoContentObserver;
    private Disposable mUpdatingContactsImage;

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
    public void syncContact(Context context) {
        Log.d(TAG, "Calling syncContacts");
        mContactoContentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                Log.d(TAG, "Syncing contacts");
                getContacts();
            }
        };
        context.getContentResolver().registerContentObserver(ChatInContract.ContactEntry.CONTENT_URI, true, mContactoContentObserver);

    }

    @Override
    public void cleanResource(Context context) {
        Log.d(TAG, "Calling cleanResource");
        if(mContactoContentObserver != null) {
            Log.d(TAG, "unregister mContactoContentObserver");
            context.getContentResolver().unregisterContentObserver(mContactoContentObserver);
        }

        if(mUpdatingContactsImage != null && !mUpdatingContactsImage.isDisposed()) {
            Log.d(TAG, "mUpdatingContactsImage.dispose();");
            mUpdatingContactsImage.dispose();
        }
    }

    @Override
    public void refreshContacts() {
        Log.d(TAG, "Starting to refresh contacts");
        Log.d(TAG, "updateContactsImage");
        mUpdatingContactsImage = mSyncDatabase.updateContactsImage().subscribe(() -> {
            Log.d(TAG, "Updating contacts");
            getContacts();
        });
        mView.showMessage("Refreshing");
    }
}
