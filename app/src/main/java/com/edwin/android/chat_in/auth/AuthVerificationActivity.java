package com.edwin.android.chat_in.auth;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.edwin.android.chat_in.R;
import com.google.firebase.auth.PhoneAuthProvider;

public class AuthVerificationActivity extends AppCompatActivity {


    public static final String TAG = AuthVerificationActivity.class.getSimpleName();
    public static final String BUNDLE_PHONE_NUMBER = "BUNDLE_PHONE_NUMBER";
    public static final String BUNDLE_TOKEN_VERIFICATION = "BUNDLE_TOKEN_VERIFICATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_verification);
        final String phoneNumber = getIntent().getStringExtra(BUNDLE_PHONE_NUMBER);
        //final String phoneNumber = "18292779870";
        Log.d(TAG, "Phone number received: "+ phoneNumber);

        AuthVerificationFragment fragment = (AuthVerificationFragment) getFragmentManager()
                .findFragmentById(R.id.frame_layout_auth_verification);

        if (fragment == null) {

            fragment = AuthVerificationFragment.newInstance(phoneNumber);
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.frame_layout_auth_verification, fragment);
            fragmentTransaction.commit();
        }
    }
}
