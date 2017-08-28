package com.edwin.android.chat_in.data.sync;

import android.util.Log;

import com.edwin.android.chat_in.chat.ChatFragment;
import com.edwin.android.chat_in.data.dto.ContactDTO;
import com.edwin.android.chat_in.data.dto.ConversationDTO;
import com.edwin.android.chat_in.data.repositories.ContactRepository;
import com.edwin.android.chat_in.data.repositories.ConversationRepository;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import static com.edwin.android.chat_in.util.FirebaseDatabaseUtil.Constants.CONVERSATION_ROOT_PATH;

/**
 * Created by Edwin Ramirez Ventura on 8/23/2017.
 */

@Singleton
public class SyncDatabase {

    public static final String TAG = SyncDatabase.class.getSimpleName();
    private final ContactRepository mContactRepository;
    private final ConversationRepository mConversationRepository;
    private DatabaseReference mDatabase;

    @Inject
    public SyncDatabase(DatabaseReference databaseReference,
                        ContactRepository contactRepository,
                        ConversationRepository conversationRepository) {
        this.mDatabase = databaseReference;
        mContactRepository = contactRepository;
        mConversationRepository = conversationRepository;
    }

    public void syncContact(String ownerTelephoneNumber) {
        Log.d(TAG, "Executing syncContact");

        mContactRepository.getContactByNumber(Long.valueOf(ChatFragment.MY_NUMBER)).isEmpty().subscribe(isEmpty -> {
            Log.d(TAG, "isEmpty: " + isEmpty);
            if(isEmpty) {
                Log.d(TAG, "Persisting owner contact");
                final ContactDTO contact = new ContactDTO();
                contact.setId(ContactRepository.OWNER_CONTACT_ID);
                contact.setNumber(Long.valueOf(ChatFragment.MY_NUMBER));
                mContactRepository.persist(contact);
                Log.d(TAG, "Persisted contact: "+ contact);
            }
        });

        PublishSubject<Long> publishSubjectTelephoneNumberContact = PublishSubject.create();
        final DatabaseReference contactsReference = mDatabase.child("/users/" + ownerTelephoneNumber +
                "/contacts");
        contactsReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "child dataSnapshot: "+ dataSnapshot);
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
                Log.d(TAG, "OnComplete called");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        publishSubjectTelephoneNumberContact
                .observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.computation())
                .subscribe(telephoneNumber ->
                {
                    Log.d(TAG, "telephoneNumber to find contact: "+ telephoneNumber);
                    mContactRepository.getContactByNumber(telephoneNumber).isEmpty().subscribe(isEmpty -> {
                    Log.d(TAG, "isEmpty: " + isEmpty);
                    if(isEmpty) {
                        final ContactDTO contact = new ContactDTO();
                        contact.setNumber(telephoneNumber);
                        mContactRepository.persist(contact);
                        Log.d(TAG, "Persisted contact: "+ contact);
                    }
                });
                }
        );

    }

    public void syncConversation(String ownerTelephoneNumber) {
        Log.d(TAG, "Executing syncConversation method");
        Log.d(TAG, "ownerTelephoneNumber: " + ownerTelephoneNumber);
        final Query meToTargetConversation = mDatabase.child(CONVERSATION_ROOT_PATH).startAt
                (null, ownerTelephoneNumber);

        Observable<ConversationDTO> meToTargetObservable = Observable.create(e -> {
            Log.d(TAG, "meToTargetObservable");
            meToTargetConversation.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "child dataSnapshot: " + dataSnapshot);
                    ConversationDTO conversation = new ConversationDTO();

                    final String destinationNumber = dataSnapshot.getKey().substring(dataSnapshot
                            .getKey
                            ().indexOf("_") + 1);
                    Log.d(TAG, "destinationNumber: " + destinationNumber);
                    mContactRepository.getContactByNumber(Long.valueOf(destinationNumber))
                            .subscribe(contactDTO -> {
                                final int receiveContactId = contactDTO.getId();
                                for (DataSnapshot conversationDataSnapShot : dataSnapshot
                                        .getChildren()) {
                                    Log.d(TAG, "conversationDataSnapShot: " +
                                            conversationDataSnapShot);
                                    conversation.setSenderContactId(ContactRepository
                                            .OWNER_CONTACT_ID);
                                    conversation.setRecipientContactId(receiveContactId);
                                    conversation.setMessage(conversationDataSnapShot.child
                                            ("message").getValue(String.class));
                                    conversation.setMessageDate(Long.valueOf
                                            (conversationDataSnapShot.getKey()) * 1000);
                                    Log.d(TAG, "Conversation: " + conversation);
                                    e.onNext(conversation);
                                }
                            });

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
        });
        meToTargetObservable.filter(conversationDTO -> {
            final ConversationDTO conversationReturned = mConversationRepository
                    .getConversationByDateTimeNumber(conversationDTO
                            .getMessageDate()).blockingGet();
            Log.d(TAG, "conversationReturned from filter: " + conversationReturned);
            return conversationReturned == null;
        }).subscribe(mConversationRepository::persist);

    }


}
