package com.edwin.android.chat_in.data.fcm;

import android.util.Log;

import com.edwin.android.chat_in.data.dto.ContactDTO;
import com.edwin.android.chat_in.data.repositories.ContactRepository;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import javax.inject.Inject;

import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Edwin Ramirez Ventura on 8/23/2017.
 */

public class SyncDatabase {

    public static final String TAG = SyncDatabase.class.getSimpleName();
    private final ContactRepository mContactRepository;
    private DatabaseReference mDatabase;

    @Inject
    public SyncDatabase(DatabaseReference databaseReference, ContactRepository contactRepository) {
        this.mDatabase = databaseReference;
        mContactRepository = contactRepository;
    }


    public void syncContact(long ownerTelephoneNumber) {
        PublishSubject<Long> publishSubjectTelephoneNumberContact = PublishSubject.create();
        final DatabaseReference contactsReference = mDatabase.child("/users/" + ownerTelephoneNumber +
                "/contacts");
        contactsReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                publishSubjectTelephoneNumberContact.onNext(Long.valueOf(dataSnapshot.getKey()));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        contactsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                publishSubjectTelephoneNumberContact.onComplete();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        publishSubjectTelephoneNumberContact
                .observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.computation())
                .subscribe(telephoneNumber ->
                mContactRepository.getContactByNumber(telephoneNumber).isEmpty().subscribe(isEmpty -> {
                    Log.d(TAG, "isEmpty: " + isEmpty);
                    if(isEmpty) {
                        final ContactDTO contact = new ContactDTO();
                        contact.setNumber(telephoneNumber);
                        mContactRepository.persit(contact);
                        Log.d(TAG, "Persisted contact: "+ contact);
                    }
                })
        );

    }
}
