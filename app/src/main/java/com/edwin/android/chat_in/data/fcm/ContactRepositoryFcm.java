package com.edwin.android.chat_in.data.fcm;

import android.content.Context;
import android.util.Log;

import com.edwin.android.chat_in.data.dto.ContactDTO;
import com.edwin.android.chat_in.util.ResourceUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Maybe;

/**
 * Created by Edwin Ramirez Ventura on 9/1/2017.
 */

@Singleton
public class ContactRepositoryFcm {

    public static final String TAG = ContactRepositoryFcm.class.getSimpleName();
    public static final String USER_PROFILE_IMAGE_PATH = "profileImage";
    public static final String USERS_PATH = "users/";
    private final DatabaseReference mDatabaseReference;
    private final Context mContext;

    @Inject
    public ContactRepositoryFcm(Context context, DatabaseReference databaseReference) {
        mContext = context;
        mDatabaseReference = databaseReference;
    }

    public Completable persist(ContactDTO contact) {
        return Completable.create(emitter ->
        {
            Log.d(TAG, "Contact to persist: " + contact);
            mDatabaseReference.child(USERS_PATH).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void
                                onDataChange(DataSnapshot dataSnapshot) {
                                    Log.d(TAG, "users dataSnapshot: " + dataSnapshot);
                                    Log.d(TAG, "dataSnapshot.hasChild(contact.getNumber()): "+ dataSnapshot.hasChild(contact.getNumber()));
                                    if (!dataSnapshot.hasChild(contact.getNumber())) {
                                        mDatabaseReference
                                                .child(USERS_PATH)
                                                .push()
                                                .setValue(contact.getNumber(), (databaseError,
                        databaseReference) -> mDatabaseReference
                                                        .child(USERS_PATH)
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
    public Completable update(ContactDTO contact) {
        Log.d(TAG, "Calling update method");
        return Completable.create(e -> {
            if(contact.getProfileImagePath() == null || contact.getProfileImagePath().isEmpty()) {
                Log.d(TAG, "No update profile when missing all updates parameters");
                e.onComplete();
                return;
            }
            final HashMap<String, Object> profile = new HashMap<>();
            profile.put(USER_PROFILE_IMAGE_PATH, contact.getProfileImagePath());
            mDatabaseReference.child("users").child(ResourceUtil.getPhoneNumber(mContext)).updateChildren(profile);
        });
    }
    public Maybe<String> getProfileImage(String phoneNumber) {
        Log.d(TAG, "Executing getProfileImage");
        return Maybe.create(e -> mDatabaseReference.child(USERS_PATH).child(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "user dataSnapshot: "+ dataSnapshot);
                if(dataSnapshot.hasChild(USER_PROFILE_IMAGE_PATH)) {
                    final String profileImage = dataSnapshot.child(USER_PROFILE_IMAGE_PATH).getValue
                            (String.class);
                    Log.d(TAG, "profile image: "+ profileImage);
                    e.onSuccess(profileImage);
                }
                Log.d(TAG, "Calling onComplete");
                e.onComplete();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                e.onError(databaseError.toException());
            }
        }));
    }
}
