package com.edwin.android.chat_in.data.fcm;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Edwin Ramirez Ventura on 8/25/2017.
 */

@Module
public class FcmModule {

    @Provides
    DatabaseReference provideFcmDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference();
    }

    @Provides
    FirebaseStorage provideFirebaseStorage() {
        return FirebaseStorage.getInstance();
    }
}
