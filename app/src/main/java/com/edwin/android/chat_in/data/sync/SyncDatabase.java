package com.edwin.android.chat_in.data.sync;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.edwin.android.chat_in.data.dto.ContactDTO;
import com.edwin.android.chat_in.data.dto.ConversationDTO;
import com.edwin.android.chat_in.data.entity.Contact;
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
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
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
    private Disposable conversationDisposable;

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
            mContactRepository.getContactByNumber(phoneNumber).isEmpty()
                    .subscribeOn(Schedulers.computation())
                    .subscribe(isEmpty -> {
                Log.d(TAG, "isEmpty: " + isEmpty);
                if(isEmpty) {
                    Log.d(TAG, "Persisting owner contact");
                    final ContactDTO contact = new ContactDTO();
                    contact.setId(ContactRepository.OWNER_CONTACT_ID);
                    contact.setNumber(phoneNumber);
                    Log.d(TAG, "Persisted contact on Firebase: "+ contact);
                    mContactRepositoryFcm.persist(contact)
                            .subscribeOn(Schedulers.io())
                            .subscribe();
                    String profileImage = mContactRepositoryFcm.getProfileImage
                            (phoneNumber)
                            .subscribeOn(Schedulers.computation())
                            .blockingGet();
                    contact.setProfileImagePath(profileImage);
                    Log.d(TAG, "contact to be persisted: "+ contact);
                    mContactRepository.persist(contact);
                    SyncDatabase.this.downloadProfileImage(contact.getProfileImagePath())
                            .subscribeOn(Schedulers.io())
                            .subscribe();

                }
                final List<String> numberProcessed = new ArrayList<>();
                mContactRepository.getAllPhoneContacts()
                        .filter(sparseArray -> {
                            final String telephoneNumber = sparseArray.get(ContactRepository.TELEPHONE_NUMBER);

                            if(!numberProcessed.contains(telephoneNumber)) {
                                numberProcessed.add(telephoneNumber);
                                return true;
                            }
                            Log.d(TAG, "telephoneNumber has been processed previously, skip it");
                            return false;
                        })
                        .filter(sparseArray -> {
                            final String telephoneNumber = sparseArray.get(ContactRepository.TELEPHONE_NUMBER);
                            final ContactDTO contact = mContactRepository.getContactByNumber
                                    (telephoneNumber).blockingGet();
                            if(contact == null) {
                                return true;
                            }

                            if(contact.getUserName() == null || contact.getUserName().isEmpty()) {
                                return true;
                            }

                            Log.d(TAG, "Number: " + telephoneNumber + ", contact exist, skip it. Contact: " + contact);
                            return false;
                        })
                        .toList().subscribe(sparseArrays -> {
                            Log.d(TAG, "Clear number processed contacts list");
                            numberProcessed.clear();
                            Log.d(TAG, "spaceArrays size: "+sparseArrays);
                            MutableInteger mutableInteger = new MutableInteger();

                            if(sparseArrays.isEmpty()) {
                                emitter.onComplete();
                            }
                            for(SparseArray<String> sparseArray : sparseArrays) {
                                final String contactName = sparseArray.get(ContactRepository.CONTACT_NAME);
                                final String telephoneNumber = sparseArray.get(ContactRepository.TELEPHONE_NUMBER);
                                Log.d(TAG, "Contact name received: "+ contactName+", number: "+ telephoneNumber);
                                mDatabase.child("/users/"+telephoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Log.d(TAG, "user dataSnapshot: " + dataSnapshot);
                                        if (dataSnapshot.getValue() == null) {
                                            Log.d(TAG, "Skip user because value is null");
                                            return;
                                        }
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

    public void syncConversation() {
        String ownerTelephoneNumber = ResourceUtil.getPhoneNumber(mContext);
        Log.d(TAG, "Executing syncConversation method");
        Log.d(TAG, "ownerTelephoneNumber: " + ownerTelephoneNumber);
        Log.d(TAG, "Starting to persist conversation");
        conversationDisposable = getNewConversations()
                .subscribeOn(Schedulers.computation())
                .subscribe(messageWrapper -> {
                    Log.d(TAG, "Conversation received: "+ messageWrapper);
                    final ContactDTO senderContact = mContactRepository.getContactByNumber
                            (messageWrapper.getSenderNumber()).blockingGet();
                    final ContactDTO recipientContact = mContactRepository.getContactByNumber
                            (messageWrapper.getRecipientNumber()).blockingGet();

                    ConversationDTO conversation = new ConversationDTO();
                    conversation.setMessageDate(messageWrapper.getMessageDate());
                    conversation.setMessage(messageWrapper.getMessage());
                    conversation.setConversationGroupId(String.valueOf(
                            Long.valueOf(messageWrapper.getRecipientNumber()) +
                                    Long.valueOf(messageWrapper.getSenderNumber()))
                    );

                    if(senderContact == null) {
                        ContactDTO contact = new ContactDTO();
                        contact.setNumber(messageWrapper.getSenderNumber());
                        final int contactId = mContactRepository.persist(contact);
                        contact.setId(contactId);
                        conversation.setSenderContactId(contactId);
                    } else {
                        conversation.setSenderContactId(senderContact.getId());
                    }

                    if(recipientContact == null) {
                        ContactDTO contact = new ContactDTO();
                        contact.setNumber(messageWrapper.getRecipientNumber());
                        final int contactId = mContactRepository.persist(contact);
                        contact.setId(contactId);
                        conversation.setRecipientContactId(contactId);
                    } else {
                        conversation.setRecipientContactId(recipientContact.getId());
                    }

                    Log.d(TAG, "Conversation to persist: "+ conversation);
                    mConversationRepository.persist(conversation).subscribe();
                });
    }

    public void syncContacts() {
        Log.d(TAG, "Calling syncContacts");
        getNewContacts().subscribeOn(Schedulers.computation())
                .subscribe(contact -> {
                    final ContactDTO persistedContact = mContactRepository.getContactByNumber
                            (contact.getNumber()).blockingGet();
                    if(persistedContact == null) {
                        Log.d(TAG, "Persisting new contact: "+ contact);
                        mContactRepository.persist(contact);
                    } else {
                        Log.d(TAG, "Updating new contact: "+ contact);
                        mContactRepository.updateContact(contact);
                    }
                    if(contact.getProfileImagePath() != null && !contact.getProfileImagePath().isEmpty()) {
                        Log.d(TAG, "Downloading profile image");
                        SyncDatabase.this.downloadProfileImage(contact.getProfileImagePath())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe();
                    }
                });
    }

    public Disposable getConversationDisposable() {
        return conversationDisposable;
    }

    public Observable<MessageWrapper> getNewConversations() {
        String ownerTelephoneNumber = ResourceUtil.getPhoneNumber(mContext);
        final Query conversationPath = mDatabase.child(CONVERSATION_ROOT_PATH);
        Observable<MessageWrapper> targetToMeObservable = Observable.create(e -> {
            Log.d(TAG, "getNewConversations");
            conversationPath.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    if(!dataSnapshot.getKey().contains(ownerTelephoneNumber)) {
                        Log.d(TAG, "Skip processing because is not valid dataSnapshot");
                        return;
                    }

                    mDatabase.child(CONVERSATION_ROOT_PATH).child(dataSnapshot.getKey()).addChildEventListener(new ChildEventListener() {

                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshotConversationChild, String s) {
                            MessageWrapper conversation = new MessageWrapper();
                            Log.d(TAG, "dataSnapshotConversationChild: " + dataSnapshotConversationChild);
                            if(!TextUtils.isDigitsOnly(dataSnapshotConversationChild.getKey())) {
                                Log.d(TAG, "Skip message because is not valid");
                                return;
                            }
                            final DatabaseReference referenceConversation = dataSnapshotConversationChild.getRef
                                    ().getParent();
                            final String conversationNumbers = referenceConversation.toString().substring(
                                    referenceConversation.toString().lastIndexOf("/")+1
                            );
                            Log.d(TAG, "conversationNumbers: " +conversationNumbers);

                            final String senderNumber = conversationNumbers.substring(0, conversationNumbers.indexOf("_"));
                            final String recipientNumber = conversationNumbers.substring(conversationNumbers.indexOf("_") + 1);
                            Log.d(TAG, "senderNumber: " + senderNumber+", recipientNumber: "+ recipientNumber);

                            conversation.setRecipientNumber(recipientNumber);
                            conversation.setSenderNumber(senderNumber);

                            conversation.setMessage(dataSnapshotConversationChild.child("message").getValue(String.class));
                            conversation.setMessageDate(Long.valueOf(dataSnapshotConversationChild.getKey()));
                            Log.d(TAG, "Conversation: " + conversation);
                            e.onNext(conversation);

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
