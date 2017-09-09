package com.edwin.android.chat_in.contact;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.edwin.android.chat_in.R;
import com.edwin.android.chat_in.data.repositories.ContactRepository;
import com.edwin.android.chat_in.data.sync.SyncDatabase;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Edwin Ramirez Ventura on 8/29/2017.
 */

public class ContactPresenter implements ContactMVP.Presenter {

    public static final String TAG = ContactPresenter.class.getSimpleName();
    public static final String PREF_FIRST_TIME = "PREF_FIRST_TIME";
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

    @Override
    public void syncContacts() {
        Log.d(TAG, "Executing syncContacts");
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (mContext);

        if(!sharedPreferences.contains(PREF_FIRST_TIME)) {
            final SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(PREF_FIRST_TIME, false);
            editor.apply();
            Log.d(TAG, "First time pref change to false. Refreshing contacts");
            refreshContacts();
        } else {
            Log.d(TAG, "Is not first time, calling getcontacts");
            getContacts();
        }
    }
}
