package com.edwin.android.chat_in.data.repositories;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;

import com.edwin.android.chat_in.data.ChatInContract;
import com.edwin.android.chat_in.data.dto.ContactDTO;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Maybe;
import io.reactivex.Observable;

/**
 * Created by Edwin Ramirez Ventura on 8/24/2017.
 */

@Singleton
public class ContactRepository {

    public static final String TAG = ContactRepository.class.getSimpleName();
    public static final int OWNER_CONTACT_ID = 1;
    public static final int TELEPHONE_NUMBER = 5451;
    public static final int CONTACT_NAME = 12121;
    private ContentResolver mContentResolver;

    @Inject
    public ContactRepository(ContentResolver contentResolver) {
        this.mContentResolver = contentResolver;
    }

    public Maybe<ContactDTO> getContactByNumber(long number) {

        return Maybe.create(emitter -> {
            Cursor contactCursor = null;
            try {
                Log.d(TAG, "Finding contact with number: " + number);
                contactCursor = mContentResolver.query(
                        ChatInContract.ContactEntry.CONTENT_URI,
                        null,
                        ChatInContract.ContactEntry.COLUMN_NAME_NUMBER + " = ?",
                        new String[]{String.valueOf(number)},
                        null);
                if (contactCursor != null && contactCursor.moveToNext()) {
                    Log.d(TAG, "Contact name exists");
                    final ContactDTO contact = convertCursorToContact(contactCursor);
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

    public Maybe<ContactDTO> getContactById(int contactId) {
        return Maybe.create(emitter -> {
            Cursor contactCursor = null;
            try {
                Log.d(TAG, "Finding contact with contactId: " + contactId);
                contactCursor = mContentResolver.query(
                        ChatInContract.ContactEntry.CONTENT_URI,
                        null,
                        ChatInContract.ContactEntry._ID + " = ?",
                        new String[]{String.valueOf(contactId)},
                        null);
                if (contactCursor != null && contactCursor.moveToNext()) {
                    Log.d(TAG, "Contact id exists");
                    final ContactDTO contact = convertCursorToContact(contactCursor);
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
        if(contactDTO.getId() > 0) {
            cv.put(ChatInContract.ContactEntry._ID, contactDTO.getId());
        }
        cv.put(ChatInContract.ContactEntry.COLUMN_NAME_NAME, contactDTO.getUserName());
        cv.put(ChatInContract.ContactEntry.COLUMN_NAME_NUMBER, contactDTO.getNumber());
        cv.put(ChatInContract.ContactEntry.COLUMN_NAME_PROFILE_IMAGE_PATH, contactDTO.getProfileImagePath());

        final Uri insertedUri = mContentResolver.insert(ChatInContract.ContactEntry
                .CONTENT_URI, cv);
        final int idContactGenerated = (int)ContentUris.parseId(insertedUri);
        Log.d(TAG, "idContactGenerated: " + idContactGenerated);
        return idContactGenerated;
    }

    @NonNull
    private ContactDTO convertCursorToContact(Cursor contactCursor) {
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
        return contact;
    }

    public Observable<ContactDTO> getAllContacts() {
        return Observable.create(emitter -> {
            Cursor contactCursor = null;
            try {
                Log.d(TAG, "Retrieving all contacts");
                contactCursor = mContentResolver.query(
                        ChatInContract.ContactEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
                while (contactCursor != null && contactCursor.moveToNext()) {
                    final ContactDTO contact = convertCursorToContact(contactCursor);
                    emitter.onNext(contact);
                }
                    Log.d(TAG, "Calling onComplete");
                    emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            } finally {
                emitter.onComplete();
                if (contactCursor != null) {
                    contactCursor.close();
                }
            }
        });
    }

    public Observable<SparseArray<String>> getAllPhoneContacts() {
        return Observable.create(emitter -> {
            Cursor cursor = null;
            try {
                cursor = mContentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                        null, null, null, null);
                Log.d(TAG, "Calling getAllPhoneContacts method");
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        String id = cursor.getString(
                                cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        String name = cursor.getString(cursor.getColumnIndex(
                                ContactsContract.Contacts.DISPLAY_NAME));

                        if (cursor.getInt(cursor.getColumnIndex(
                                ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                            Cursor phoneCursor = mContentResolver.query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                    new String[]{id}, null);
                            while (phoneCursor != null && phoneCursor.moveToNext()) {
                                String phoneNo = phoneCursor.getString(phoneCursor.getColumnIndex(
                                        ContactsContract.CommonDataKinds.Phone.NUMBER));
                                Log.d(TAG, "Name: " + name + ", Phone No: " + phoneNo);
                                SparseArray<String> data = new SparseArray<>();
                                data.put(TELEPHONE_NUMBER, phoneNo.replaceAll("[()\\s-]+", ""));
                                data.put(CONTACT_NAME, name);
                                emitter.onNext(data);
                            }
                            if(phoneCursor != null) {
                                phoneCursor.close();
                            }
                        }
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
}
