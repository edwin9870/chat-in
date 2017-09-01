package com.edwin.android.chat_in.data.fcm;

import android.util.Log;

import com.edwin.android.chat_in.data.dto.ContactDTO;
import com.edwin.android.chat_in.data.dto.ConversationDTO;
import com.edwin.android.chat_in.data.repositories.ContactRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Edwin Ramirez Ventura on 8/31/2017.
 */

@Singleton
public class ConversationRepositoryFcm {

    public static final String TAG = ConversationRepositoryFcm.class.getSimpleName();
    private final ContactRepository mContactRepository;
    private DatabaseReference mDatabaseReference;

    @Inject
    public ConversationRepositoryFcm(DatabaseReference databaseReference, ContactRepository contactRepository) {
        this.mDatabaseReference = databaseReference;
        mContactRepository = contactRepository;
    }

    public Completable persist(ConversationDTO conversation) {
        Log.d(TAG, "Calling persist with conversation: "+ conversation);
        return Completable.create(emitter -> {
            final DatabaseReference conversationDatabaseReference = mDatabaseReference.child("conversation/");
            conversationDatabaseReference
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d(TAG, "conversationDataSnapShort: " + dataSnapshot);


                            final Maybe<ContactDTO> recipientContactMaybe = mContactRepository
                                    .getContactById
                                    (conversation.getRecipientContactId());
                            final Maybe<ContactDTO> senderContactMaybe = mContactRepository
                                    .getContactById
                                    (conversation.getSenderContactId());

                            Maybe.zip(recipientContactMaybe, senderContactMaybe, (recipientContact,
                                                                                  senderContact) ->
                                    senderContact.getNumber() + "_" + recipientContact.getNumber())
                                    .subscribeOn(Schedulers.computation())
                                    .subscribe(conversationPath -> {
                                        Log.d(TAG, "conversation path: " + conversationPath);

                                        if (!dataSnapshot.hasChild(conversationPath)) {
                                            conversationDatabaseReference.setValue(conversationPath);
                                        }
                                        conversationDatabaseReference.child(conversationPath)
                                                .push()
                                                .setValue(conversation.getMessageDate());

                                        final HashMap<String, String> messageMap = new HashMap<>();
                                        messageMap.put("message", conversation.getMessage());
                                        conversationDatabaseReference
                                                .child(conversationPath)
                                                .child(String.valueOf(conversation.getMessageDate()))
                                                .setValue(messageMap, (databaseError,databaseReference) -> emitter.onComplete());

                                    });


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        });
    }
}
