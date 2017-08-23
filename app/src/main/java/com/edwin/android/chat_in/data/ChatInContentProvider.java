package com.edwin.android.chat_in.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.edwin.android.chat_in.data.ChatInContract.ContactEntry;

/**
 * Created by Edwin Ramirez Ventura on 8/23/2017.
 */

public class ChatInContentProvider extends ContentProvider {

    public static final int CONTACT = 100;
    public static final int CONTACT_WITH_ID = 101;

    public static final int CONVERSATION = 200;
    public static final int CONVERSATION_WITH_ID = 201;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ChatInDbHelper mChatInDbHelper;


    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(ChatInContract.AUTHORITY, ChatInContract
                .PATH_CONTACT, CONTACT);
        uriMatcher.addURI(ChatInContract.AUTHORITY, ChatInContract
                .PATH_CONTACT + "/#", CONTACT_WITH_ID);

        uriMatcher.addURI(ChatInContract.AUTHORITY, ChatInContract
                .PATH_CONVERSATION, CONVERSATION);
        uriMatcher.addURI(ChatInContract.AUTHORITY, ChatInContract
                .PATH_CONVERSATION + "/#", CONTACT_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mChatInDbHelper = new ChatInDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String
            selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mChatInDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);

        Cursor refCursor;

        switch (match) {
            case CONTACT:
                refCursor = db.query(ContactEntry.TABLE_NAME, projection, selection, selectionArgs, null,
                        null, sortOrder);
                break;
            case CONVERSATION:
                refCursor = db.query(ChatInContract.ConversationEntry.TABLE_NAME, projection, selection, selectionArgs, null,
                        null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        refCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return refCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
