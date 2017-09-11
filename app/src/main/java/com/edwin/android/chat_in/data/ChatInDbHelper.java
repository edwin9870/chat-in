package com.edwin.android.chat_in.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.edwin.android.chat_in.data.ChatInContract.ContactEntry;
import com.edwin.android.chat_in.data.ChatInContract.ConversationEntry;

/**
 * Created by Edwin Ramirez Ventura on 8/23/2017.
 */

public class ChatInDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "chat-in40.db";
    public static final String TAG = ChatInDbHelper.class.getSimpleName();

    public ChatInDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "ChatInDbHelper constructor called");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_CONTACT_TABLE = "CREATE TABLE " + ContactEntry
                .TABLE_NAME +
                " ( " + ContactEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT " +
                "NOT NULL," +
                "    " + ContactEntry.COLUMN_NAME_NAME + " VARCHAR(30)," +
                "    " + ContactEntry.COLUMN_NAME_NUMBER + " VARCHAR(20) NOT NULL," +
                "    " + ContactEntry.COLUMN_NAME_PROFILE_IMAGE_PATH + " VARCHAR" +
                ");";

        db.execSQL(SQL_CREATE_CONTACT_TABLE);

        final String SQL_CREATE_CONVERSATION_TABLE = "CREATE TABLE " +
                ConversationEntry.TABLE_NAME + " ( "
                + ConversationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + ConversationEntry.COLUMN_NAME_MESSAGE + " TEXT NOT NULL, " +
                "\"" + ConversationEntry.COLUMN_NAME_SENDER + "\" INTEGER NOT NULL, " +
                "\"" + ConversationEntry.COLUMN_NAME_NUMERIC_DATE + "\" LONG NOT NULL, " +
                "\"" + ConversationEntry.COLUMN_NAME_RECIPIENT + "\" INTEGER NOT NULL, " +
                ConversationEntry.COLUMN_NAME_CONVERSATION_GROUP_ID + " TEXT NOT NULL, " +
                "CONSTRAINT CONVERSATION_CONTACT__ID_fk FOREIGN KEY (\"" +
                ConversationEntry.COLUMN_NAME_SENDER + "\") REFERENCES " +
                ContactEntry.TABLE_NAME + " (" +
                ContactEntry._ID + "), " +
                "CONSTRAINT CONVERSATION_CONTACT__ID_fk FOREIGN KEY (\"" +
                ConversationEntry.COLUMN_NAME_RECIPIENT + "\") REFERENCES " +
                ContactEntry.TABLE_NAME + " " +
                "(" + ContactEntry._ID + ") );";

        db.execSQL(SQL_CREATE_CONVERSATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
