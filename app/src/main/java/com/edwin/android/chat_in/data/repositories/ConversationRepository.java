package com.edwin.android.chat_in.data.repositories;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.edwin.android.chat_in.data.ChatInContract.ConversationEntry;
import com.edwin.android.chat_in.data.dto.ConversationDTO;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Maybe;

/**
 * Created by Edwin Ramirez Ventura on 8/24/2017.
 */

@Singleton
public class ConversationRepository {

    public static final String TAG = ConversationRepository.class.getSimpleName();
    private ContentResolver mContentResolver;

    @Inject
    public ConversationRepository(ContentResolver contentResolver) {
        this.mContentResolver = contentResolver;
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
                    ConversationDTO conversation = new ConversationDTO();
                    conversation.setRecipientContactId(
                            conversationCursor.getInt(
                                    conversationCursor.getColumnIndex(ConversationEntry.COLUMN_NAME_RECIPIENT)));

                    conversation.setSenderContactId(conversationCursor.getInt(
                            conversationCursor.getColumnIndex(ConversationEntry.COLUMN_NAME_SENDER)));

                    conversation.setMessageDate(dateTime);
                    conversation.setMessage(conversationCursor.getString(conversationCursor
                            .getColumnIndex(ConversationEntry.COLUMN_NAME_MESSAGE)));
                    conversation.setId(conversationCursor.getInt(conversationCursor.getColumnIndex(ConversationEntry._ID)));
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

    public long persist(ConversationDTO conversation) {
        final ContentValues cv = new ContentValues();
        Log.d(TAG, "Persisting conversation: "+ conversation);
        cv.put(ConversationEntry.COLUMN_NAME_MESSAGE, conversation.getMessage());
        cv.put(ConversationEntry.COLUMN_NAME_SENDER, conversation.getSenderContactId());
        cv.put(ConversationEntry.COLUMN_NAME_RECIPIENT, conversation.getRecipientContactId());
        cv.put(ConversationEntry.COLUMN_NAME_NUMERIC_DATE, conversation.getMessageDate());
        final Uri insertedUri = mContentResolver.insert(ConversationEntry
                .CONTENT_URI, cv);
        final long idConversation = ContentUris.parseId(insertedUri);
        Log.d(TAG, "idConversation: " + idConversation);
        return idConversation;
    }
}
