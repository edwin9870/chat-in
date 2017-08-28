package com.edwin.android.chat_in.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Edwin Ramirez Ventura on 8/23/2017.
 */

public class ChatInContract {

    public static final String AUTHORITY = "com.edwin.android.chat_in";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_CONTACT = "CONTACT";
    public static final String PATH_CONVERSATION = "CONVERSATION";

    public static class ContactEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CONTACT).build();

        public static final String TABLE_NAME = "CONTACT";
        public static final String COLUMN_NAME_NAME = "NAME";
        public static final String COLUMN_NAME_NUMBER = "NUMBER";
        public static final String COLUMN_NAME_PROFILE_IMAGE_PATH = "PROFILE_IMAGE_PATH";
    }

    public static class ConversationEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CONVERSATION).build();

        public static final String TABLE_NAME = "CONVERSATION";
        public static final String COLUMN_NAME_MESSAGE = "MESSAGE";
        public static final String COLUMN_NAME_SENDER = "SENDER";
        public static final String COLUMN_NAME_RECIPIENT = "RECIPIENT";
        public static final String COLUMN_NAME_NUMERIC_DATE = "NUMERIC_DATE";
    }
}
