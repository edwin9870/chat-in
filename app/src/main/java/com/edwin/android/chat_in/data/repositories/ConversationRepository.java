package com.edwin.android.chat_in.data.repositories;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.edwin.android.chat_in.data.ChatInContract.ConversationEntry;
import com.edwin.android.chat_in.data.dto.ContactDTO;
import com.edwin.android.chat_in.data.dto.ConversationDTO;
import com.edwin.android.chat_in.data.entity.Contact;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Edwin Ramirez Ventur
 *
 * This class contains CRUD operation for Conversation
 */
@Singleton
public class ConversationRepository {

    public static final String TAG = ConversationRepository.class.getSimpleName();
    private final ContactRepository mContactRepository;
    private ContentResolver mContentResolver;

    @Inject
    public ConversationRepository(ContentResolver contentResolver, ContactRepository contactRepository) {
        this.mContentResolver = contentResolver;
        mContactRepository = contactRepository;
    }

    public Maybe<ConversationDTO> getConversationByDateTimeNumber(long dateTime) {
        return Maybe.create(emitter -> {
            Cursor conversationCursor = null;

            try {
                Log.d(TAG, "Finding contact with dateTime: " + dateTime);
                conversationCursor = mContentResolver.query(ConversationEntry.CONTENT_URI,
                        null,
                        ConversationEntry.COLUMN_NAME_NUMERIC_DATE +" = ?",
                        new String[]{String.valueOf(dateTime)}, null);

                if(conversationCursor != null && conversationCursor.moveToNext()) {
                    ConversationDTO conversation = getConversationFromCursor(conversationCursor);
                    Log.d(TAG, "Conversation to return: " + conversation);
                    emitter.onSuccess(conversation);
                } else {
                    emitter.onComplete();
                }
            } catch (Exception e) {
                e.printStackTrace();
                emitter.onError(e);
            }
            finally {
                if(conversationCursor != null) {
                    conversationCursor.close();
                }
            }
        });
    }
    public Maybe<Long> persist(ConversationDTO conversation) {
        Log.d(TAG, "Calling persist with conversation: "+ conversation);
        return Maybe.create(e -> {
            final ContentValues cv = new ContentValues();
            cv.put(ConversationEntry.COLUMN_NAME_MESSAGE, conversation.getMessage());
            cv.put(ConversationEntry.COLUMN_NAME_SENDER, conversation.getSenderContactId());
            cv.put(ConversationEntry.COLUMN_NAME_RECIPIENT, conversation.getRecipientContactId());
            cv.put(ConversationEntry.COLUMN_NAME_NUMERIC_DATE, conversation.getMessageDate());
            cv.put(ConversationEntry.COLUMN_NAME_CONVERSATION_GROUP_ID, conversation.getConversationGroupId());

            final Uri insertedUri = mContentResolver.insert(ConversationEntry.CONTENT_URI, cv);
            final long idConversation = ContentUris.parseId(insertedUri);
            Log.d(TAG, "idConversation: " + idConversation);
            e.onSuccess(idConversation);
            e.onComplete();
        });
    }
    public Observable<ConversationDTO> getLastMessages() {
        return Observable.create(emitter -> {
            Cursor cursor = null;
            try {
                cursor = mContentResolver.query(ConversationEntry.CONTENT_URI,
                        new String[]{ConversationEntry._ID, ConversationEntry.COLUMN_NAME_MESSAGE,
                                ConversationEntry.COLUMN_NAME_SENDER, "MAX(" + ConversationEntry
                                .COLUMN_NAME_NUMERIC_DATE + ") AS " + ConversationEntry.COLUMN_NAME_NUMERIC_DATE,
                        ConversationEntry.COLUMN_NAME_RECIPIENT, ConversationEntry.COLUMN_NAME_CONVERSATION_GROUP_ID},
                        ConversationEntry.COLUMN_NAME_MESSAGE + " IS NOT NULL GROUP BY " +
                                ConversationEntry.COLUMN_NAME_CONVERSATION_GROUP_ID + " ORDER BY " +
                                ConversationEntry.COLUMN_NAME_NUMERIC_DATE + " DESC",
                        null,
                        null);
                List<Integer> contactsIdProcessed = new ArrayList<>();
                while(cursor != null && cursor.moveToNext()) {
                    ConversationDTO conversation = getConversationFromCursor(cursor);
                    Log.d(TAG, "getLastMessage. conversation: " + conversation);

                    int contactId = conversation.getRecipientContactId() == ContactRepository.OWNER_CONTACT_ID
                            ? conversation.getSenderContactId()
                            : conversation.getRecipientContactId();
                    Log.d(TAG, "contactId: " + contactId);
                    if(!contactsIdProcessed.contains(contactId)) {
                        contactsIdProcessed.add(contactId);
                        Log.d(TAG, "getLastMessage. emitting conversation: "+ conversation);
                        emitter.onNext(conversation);
                    }

                }

            } finally {
                Log.d(TAG, "Calling onComplete");
                emitter.onComplete();
                if(cursor != null) {
                    cursor.close();
                }
            }
        });
    }
    public Observable<ConversationDTO> getLastMessageByConversationGroupId(String conversationGroupId) {
        Log.d(TAG, "conversationGroupId: "+ conversationGroupId);
        return Observable.create(emitter -> {
            Cursor cursor = null;
            try {
                cursor = mContentResolver.query(ConversationEntry.CONTENT_URI,
                        new String[]{ConversationEntry._ID, ConversationEntry.COLUMN_NAME_MESSAGE,
                                ConversationEntry.COLUMN_NAME_SENDER, "MAX(" + ConversationEntry
                                .COLUMN_NAME_NUMERIC_DATE + ") AS " + ConversationEntry.COLUMN_NAME_NUMERIC_DATE,
                                ConversationEntry.COLUMN_NAME_RECIPIENT, ConversationEntry.COLUMN_NAME_CONVERSATION_GROUP_ID},
                        ConversationEntry.COLUMN_NAME_CONVERSATION_GROUP_ID + " = ? ",
                        new String[]{conversationGroupId},
                        null);
                List<Integer> contactsIdProcessed = new ArrayList<>();
                while(cursor != null && cursor.moveToNext()) {
                    ConversationDTO conversation = getConversationFromCursor(cursor);
                    Log.d(TAG, "getLastMessage. conversation: " + conversation);

                    int contactId = conversation.getRecipientContactId() == ContactRepository.OWNER_CONTACT_ID
                            ? conversation.getSenderContactId()
                            : conversation.getRecipientContactId();
                    Log.d(TAG, "contactId: " + contactId);
                    if(!contactsIdProcessed.contains(contactId)) {
                        contactsIdProcessed.add(contactId);
                        Log.d(TAG, "getLastMessage. emitting conversation: "+ conversation);
                        emitter.onNext(conversation);
                    }

                }

            } finally {
                Log.d(TAG, "Calling onComplete");
                emitter.onComplete();
                if(cursor != null) {
                    cursor.close();
                }
            }
        });
    }

    /**
     * Get the a list of conversations with a contact
     *
     * @param contactId
     * @return list of conversation sorted by Date in ascending order
     */
    public Observable<ConversationDTO> getConversations(int contactId) {
        return Observable.create(emitter -> {
            Cursor cursor = null;
            try {
                cursor = mContentResolver.query(ConversationEntry.CONTENT_URI, null,
                        ConversationEntry.COLUMN_NAME_RECIPIENT + " = ? OR " +
                                ConversationEntry.COLUMN_NAME_SENDER + " = ?", new
                                String[]{String.valueOf(contactId), String.valueOf(contactId)
                        }, ConversationEntry.COLUMN_NAME_NUMERIC_DATE + " ASC");

                while(cursor != null && cursor.moveToNext()) {
                    ConversationDTO conversation = getConversationFromCursor(cursor);
                    Log.d(TAG, "Emitting conversation: " + conversation);
                    emitter.onNext(conversation);
                }
            } catch (Exception e) {
                e.printStackTrace();
                emitter.onError(e);
            } finally {
                emitter.onComplete();
                if(cursor != null) {
                    cursor.close();
                }
            }

        });
    }
    private ConversationDTO getConversationFromCursor(Cursor conversationCursor) {
        ConversationDTO conversation = new ConversationDTO();
        conversation.setRecipientContactId(
                conversationCursor.getInt(
                        conversationCursor.getColumnIndex(ConversationEntry.COLUMN_NAME_RECIPIENT)));

        conversation.setSenderContactId(conversationCursor.getInt(
                conversationCursor.getColumnIndex(ConversationEntry.COLUMN_NAME_SENDER)));

        conversation.setMessageDate(conversationCursor.getLong(conversationCursor
                .getColumnIndex(ConversationEntry.COLUMN_NAME_NUMERIC_DATE)));
        conversation.setMessage(conversationCursor.getString(conversationCursor
                .getColumnIndex(ConversationEntry.COLUMN_NAME_MESSAGE)));
        conversation.setId(conversationCursor.getInt(conversationCursor.getColumnIndex(ConversationEntry._ID)));
        conversation.setConversationGroupId(
                conversationCursor.getString(conversationCursor
                .getColumnIndex(ConversationEntry.COLUMN_NAME_CONVERSATION_GROUP_ID)));

        return conversation;
    }
}
