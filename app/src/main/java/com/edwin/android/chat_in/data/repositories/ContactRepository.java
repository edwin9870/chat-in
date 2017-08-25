package com.edwin.android.chat_in.data.repositories;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.edwin.android.chat_in.data.ChatInContract;
import com.edwin.android.chat_in.data.dto.ContactDTO;
import com.google.firebase.database.DatabaseReference;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Maybe;

/**
 * Created by Edwin Ramirez Ventura on 8/24/2017.
 */

@Singleton
public class ContactRepository {

    public static final String TAG = ContactRepository.class.getSimpleName();
    private ContentResolver mContentResolver;

    @Inject
    public ContactRepository(ContentResolver contentResolver) {
        this.mContentResolver = contentResolver;
    }

    public Maybe<ContactDTO> getContactByNumber(long number) {

        return Maybe.create(emitter -> {
            Cursor contactCursor = null;
            try {
                contactCursor = mContentResolver.query(
                        ChatInContract.ContactEntry.CONTENT_URI,
                        null,
                        ChatInContract.ContactEntry.COLUMN_NAME_NUMBER + " = ?",
                        new String[]{String.valueOf(number)},
                        null);
                if (contactCursor != null && contactCursor.moveToNext()) {
                    final int contactId = contactCursor.getInt(contactCursor.getColumnIndex
                            (ChatInContract.ContactEntry._ID));
                    final String contactName = contactCursor.getString(contactCursor.getColumnIndex

                            (ChatInContract.ContactEntry.COLUMN_NAME_NAME));
                    final String contactProfileImagePath = contactCursor.getString
                            (contactCursor.getColumnIndex
                            (ChatInContract.ContactEntry.COLUMN_NAME_PROFILE_IMAGE_PATH));
                    final long contactNumber = contactCursor.getLong(contactCursor.getColumnIndex
                            (ChatInContract.ContactEntry.COLUMN_NAME_NUMBER));

                    final ContactDTO contact = new ContactDTO();
                    contact.setId(contactId);
                    contact.setUserName(contactName);
                    contact.setNumber(contactNumber);
                    contact.setProfileImagePath(contactProfileImagePath);
                    emitter.onSuccess(contact);
                } else {
                    Log.d(TAG, "Returning empty");
                    emitter.onComplete();
                }
            } catch (Exception e) {
                emitter.onError(e);
            } finally {
                if (contactCursor != null) {
                    contactCursor.close();
                }
            }
        });
    }

    public int persist(ContactDTO contactDTO) {
        final ContentValues cv = new ContentValues();
        cv.put(ChatInContract.ContactEntry.COLUMN_NAME_NAME, contactDTO.getUserName());
        cv.put(ChatInContract.ContactEntry.COLUMN_NAME_NUMBER, contactDTO.getNumber());
        cv.put(ChatInContract.ContactEntry.COLUMN_NAME_PROFILE_IMAGE_PATH, contactDTO.getProfileImagePath());

        final Uri insertedUri = mContentResolver.insert(ChatInContract.ContactEntry
                .CONTENT_URI, cv);
        final int idContactGenerated = (int)ContentUris.parseId(insertedUri);
        Log.d(TAG, "idContactGenerated: " + idContactGenerated);
        return idContactGenerated;
    }
}
