package com.edwin.android.chat_in.data.repositories;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.edwin.android.chat_in.data.ChatInContract;
import com.edwin.android.chat_in.data.dto.ConversationDTO;

import javax.inject.Inject;
import javax.inject.Singleton;

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

    private long persit(ConversationDTO conversationDTO) {
        final ContentValues cv = new ContentValues();

            cv.put(ChatInContract.ConversationEntry.COLUMN_NAME_MESSAGE, conversationDTO.getMessage());
            cv.put(ChatInContract.ConversationEntry.COLUMN_NAME_FROM, conversationDTO.getFromContactId());
            cv.put(ChatInContract.ConversationEntry.COLUMN_NAME_TO, conversationDTO.getToContactId());
            cv.put(ChatInContract.ConversationEntry.COLUMN_NAME_NUMERIC_DATE, conversationDTO.getMessageDate().getTime());
        final Uri insertedUri = mContentResolver.insert(ChatInContract.ConversationEntry
                .CONTENT_URI, cv);
        final long idConversation = ContentUris.parseId(insertedUri);
        Log.d(TAG, "idConversation: " + idConversation);
        return idConversation;
    }
}
