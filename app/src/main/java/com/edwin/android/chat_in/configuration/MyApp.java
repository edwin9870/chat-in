package com.edwin.android.chat_in.configuration;

import android.app.Application;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Edwin Ramirez Ventura on 9/3/2017.
 */

public class MyApp extends Application {

    public static final String TAG = MyApp.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Executing onCreate");
        Log.d(TAG, "Enabling Firebase persistence");
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
