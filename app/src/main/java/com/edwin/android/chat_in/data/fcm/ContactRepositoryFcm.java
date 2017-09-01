package com.edwin.android.chat_in.data.fcm;

import android.util.Log;

import com.edwin.android.chat_in.data.dto.ContactDTO;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;

/**
 * Created by Edwin Ramirez Ventura on 9/1/2017.
 */

@Singleton
public class ContactRepositoryFcm {

    public static final String TAG = ContactRepositoryFcm.class.getSimpleName();
    private final DatabaseReference mDatabaseReference;

    @Inject
    public ContactRepositoryFcm(DatabaseReference databaseReference) {
        mDatabaseReference = databaseReference;
    }

    public Completable persist(ContactDTO contact) {
        return Completable.create(emitter ->
        {
            Log.d(TAG, "Contact to persist: " + contact);
            mDatabaseReference.child("users/").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void
                                onDataChange(DataSnapshot dataSnapshot) {
                                    Log.d(TAG, "users dataSnapshot: " + dataSnapshot);
                                    Log.d(TAG, "dataSnapshot.hasChild(contact.getNumber()): "+ dataSnapshot.hasChild(contact.getNumber()));
                                    if (!dataSnapshot.hasChild(contact.getNumber())) {
                                        mDatabaseReference
                                                .child("users/")
                                                .push()
                                                .setValue(contact.getNumber(), (databaseError,
                        databaseReference) -> mDatabaseReference
                                                        .child("users/")
                                                        .child(contact.getNumber())
                                                        .setValue("isValid", "true",
                        (databaseError1, databaseReference1) -> {
                            if(databaseError1 == null) {
                                emitter.onComplete();
                            } else {
                                emitter.onError(databaseError1.toException());
                            }
                        }));

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        emitter.onError(databaseError.toException());
                    }
                }
                    );
        });
    }
}
