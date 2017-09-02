package com.edwin.android.chat_in.data.sync;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.edwin.android.chat_in.data.dto.ContactDTO;
import com.edwin.android.chat_in.data.dto.ConversationDTO;
import com.edwin.android.chat_in.data.fcm.ContactRepositoryFcm;
import com.edwin.android.chat_in.data.repositories.ContactRepository;
import com.edwin.android.chat_in.data.repositories.ConversationRepository;
import com.edwin.android.chat_in.util.FileUtil;
import com.edwin.android.chat_in.util.MutableInteger;
import com.edwin.android.chat_in.util.ResourceUtil;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.net.URL;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static com.edwin.android.chat_in.util.FirebaseDatabaseUtil.Constants.CONVERSATION_ROOT_PATH;

/**
 * Created by Edwin Ramirez Ventura on 8/23/2017.
 */

@Singleton
public class SyncDatabase {

    public static final String TAG = SyncDatabase.class.getSimpleName();
    private final ContactRepository mContactRepository;
    private final ConversationRepository mConversationRepository;
    private final FirebaseStorage mFirebaseStorage;
    private final Context mContext;
    private final ContactRepositoryFcm mContactRepositoryFcm;
    private DatabaseReference mDatabase;

    @Inject
    public SyncDatabase(Context context,
                        DatabaseReference databaseReference,
                        FirebaseStorage firebaseStorage,
                        ContactRepository contactRepository,
                        ConversationRepository conversationRepository,
                        ContactRepositoryFcm contactRepositoryFcm) {
        mContext = context;
        this.mDatabase = databaseReference;
        mFirebaseStorage = firebaseStorage;
        mContactRepository = contactRepository;
        mConversationRepository = conversationRepository;
        mContactRepositoryFcm = contactRepositoryFcm;
    }

    public Observable<ContactDTO> getNewContacts() {
        Log.d(TAG, "Executing getNewContacts");
        return Observable.create(emitter -> {
            final String phoneNumber = ResourceUtil.getPhoneNumber(mContext);
            mContactRepository.getContactByNumber(phoneNumber).isEmpty().subscribe(isEmpty -> {
                Log.d(TAG, "isEmpty: " + isEmpty);
                if(isEmpty) {
                    Log.d(TAG, "Persisting owner contact");
                    final ContactDTO contact = new ContactDTO();
                    contact.setId(ContactRepository.OWNER_CONTACT_ID);
                    contact.setNumber(phoneNumber);
                    mContactRepository.persist(contact);
                    Log.d(TAG, "Persisted contact: "+ contact);
                    mContactRepositoryFcm.persist(contact)
                            .subscribeOn(Schedulers.io())
                            .subscribe();
                }

                mContactRepository.getAllPhoneContacts()
                        .filter(sparseArray -> {
                            final String telephoneNumber = sparseArray.get(ContactRepository.TELEPHONE_NUMBER);
                            final Boolean isEmptyContact = mContactRepository.getContactByNumber(telephoneNumber)
                                    .isEmpty().blockingGet();
                            Log.d(TAG, "Number: " + telephoneNumber + ", number exists: " + isEmptyContact);
                            return isEmptyContact;
                        })
                        .toList().subscribe(sparseArrays -> {
                            Log.d(TAG, "spaceArrays size: "+sparseArrays);
                            MutableInteger mutableInteger = new MutableInteger();
                            for(SparseArray<String> sparseArray : sparseArrays) {
                                final String contactName = sparseArray.get(ContactRepository.CONTACT_NAME);
                                final String telephoneNumber = sparseArray.get(ContactRepository.TELEPHONE_NUMBER);
                                Log.d(TAG, "Contact name received: "+ contactName+", number: "+ telephoneNumber);
                                mDatabase.child("/users/"+telephoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Log.d(TAG, "user dataSnapshot: " + dataSnapshot);
                                        mutableInteger.setValue(mutableInteger.getValue() + 1);
                                        final ContactDTO contact = new ContactDTO();
                                        contact.setNumber(telephoneNumber);
                                        contact.setUserName(contactName);
                                        final String profileFileImage = dataSnapshot.child("profileImage").getValue(String.class);
                                        contact.setProfileImagePath(profileFileImage);
                                        Log.d(TAG, "Contact onNext: "+ contact);
                                        emitter.onNext(contact);
                                        Log.d(TAG, "mutableInteger.getValue(): "+ mutableInteger.getValue());
                                        if(mutableInteger.getValue() == sparseArrays.size()) {
                                            Log.d(TAG, "Calling onComplete in spaceArray");
                                            emitter.onComplete();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        emitter.onError(databaseError.toException());
                                    }
                                });
                            }

                        });
            });
        });
    }

    public void sync(String ownerTelephoneNumber) {
        Log.d(TAG, "Executing sync method");
        Log.d(TAG, "ownerTelephoneNumber: " + ownerTelephoneNumber);

        getNewContacts()
                .subscribeOn(Schedulers.computation())
                .subscribe(new DisposableObserver<ContactDTO>() {
            @Override
            public void onNext(ContactDTO contact) {
                Log.d(TAG, "Contact to persist: "+contact);
                mContactRepository.persist(contact);
                SyncDatabase.this.downloadProfileImage(contact.getProfileImagePath())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "Contact persisted, Starting to persist conversation");
                getConversation(ownerTelephoneNumber)
                        .subscribeOn(Schedulers.computation())
                        .subscribe(conversationDTO -> mConversationRepository.persist(conversationDTO).subscribe());
            }
        });

    }

    public Observable<ConversationDTO> getConversation(String ownerTelephoneNumber) {
        final Query targetToMeConversation = mDatabase.child(CONVERSATION_ROOT_PATH);
        Observable<ConversationDTO> targetToMeObservable = Observable.create(e -> {
            Log.d(TAG, "getConversation");
            targetToMeConversation.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "child dataSnapshot: " + dataSnapshot);
                    if(dataSnapshot.getKey().endsWith(ownerTelephoneNumber)) {
                        Log.d(TAG, "Skip conversation because isn't end with owner number");
                        return;
                    }

                    if(!dataSnapshot.getKey().contains(ownerTelephoneNumber)) {
                        Log.d(TAG, "Skip processing because is not valid dataSnapshot");
                        return;
                    }

                    ConversationDTO conversation = new ConversationDTO();

                    final String senderNumber = dataSnapshot.getKey().substring(0, dataSnapshot.getKey().indexOf("_"));
                    final String recipientNumber = dataSnapshot.getKey().substring(dataSnapshot.getKey().indexOf("_") + 1);
                    Log.d(TAG, "senderNumber: " + senderNumber+", recipientNumber: "+ recipientNumber);
                    String numberToFindContact;
                    if(recipientNumber.equals(ownerTelephoneNumber)) {
                        conversation.setRecipientContactId(ContactRepository.OWNER_CONTACT_ID);
                        numberToFindContact = senderNumber;
                    } else {
                        conversation.setSenderContactId(ContactRepository.OWNER_CONTACT_ID);
                        numberToFindContact = recipientNumber;
                    }

                    Log.d(TAG, "numberToFindContact: " + numberToFindContact);
                    mContactRepository.getContactByNumber
                            (numberToFindContact).subscribe(contact -> {
                                for (DataSnapshot conversationDataSnapShot : dataSnapshot
                                        .getChildren()) {
                                    Log.d(TAG, "conversationDataSnapShot: " + conversationDataSnapShot);
                                    if(!TextUtils.isDigitsOnly(conversationDataSnapShot.getKey())) {
                                        continue;
                                    }
                                    if(conversation.getSenderContactId() <= 0) {
                                        conversation.setSenderContactId(contact.getId());
                                    } else {
                                        conversation.setRecipientContactId(contact.getId());
                                    }
                                    conversation.setMessage(conversationDataSnapShot.child("message").getValue(String.class));
                                    conversation.setMessageDate(Long.valueOf(conversationDataSnapShot.getKey()));
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
        return targetToMeObservable.filter(conversationDTO -> {
            final ConversationDTO conversationReturned = mConversationRepository
                    .getConversationByDateTimeNumber(conversationDTO
                            .getMessageDate()).blockingGet();
            Log.d(TAG, "conversationReturned from filter: " + conversationReturned);
            return conversationReturned == null;
        });
    }

    private Completable downloadProfileImage(String imageName) {
        return Completable.create(emitter -> mFirebaseStorage.getReference().child("images/profile/" + imageName).getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    try {
                        Log.d(TAG, "Uri to download de image: " + uri);
                        Completable.create(e ->
                                FileUtil.saveImage(mContext, new URL(uri.toString()), imageName))
                                .subscribeOn(Schedulers.io())
                                .subscribe(emitter::onComplete);
                    } catch (Exception e1) {
                        emitter.onError(e1);
                    }
                })

        );
    }
}
