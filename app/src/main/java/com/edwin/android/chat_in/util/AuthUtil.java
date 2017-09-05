package com.edwin.android.chat_in.util;

import android.app.Activity;
import android.util.Log;

import com.edwin.android.chat_in.auth.AuthVerificationFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;

import io.reactivex.Single;

/**
 * Created by Edwin Ramirez Ventura on 9/5/2017.
 */

public final class AuthUtil {

    public static final String TAG = AuthUtil.class.getSimpleName();

    public static Single<Boolean> signInWithPhoneAuthCredential(Activity activity,
                                                                PhoneAuthCredential credential) {
        return Single.create(e -> {

            Log.d(AuthVerificationFragment.TAG, "Calling signInWithPhoneAuthCredential");
            FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener(activity, task -> {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            e.onSuccess(true);
                        } else {

                            Log.d(TAG, "signInWithCredential:unsuccessful");
                            if(task.getException() != null) {
                                Log.d(TAG, "Emitting the exception");
                                e.onError(task.getException());
                            } else {
                                Log.d(TAG, "Emitting a false value");
                                e.onSuccess(false);
                            }

                            // Sign in failed, display a message and update the UI
                            Log.w(AuthVerificationFragment.TAG, "signInWithCredential:failure", task.getException());


                        }
                    });
        });
    }
}
