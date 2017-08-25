package com.edwin.android.chat_in.data.fcm;

import com.google.firebase.database.DatabaseReference;

import dagger.Component;

/**
 * Created by Edwin Ramirez Ventura on 8/25/2017.
 */
@Component(modules = {FcmModule.class})
public interface FcmComponent {
    DatabaseReference getDatabaseReference();
}
