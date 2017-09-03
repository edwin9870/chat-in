package com.edwin.android.chat_in.auth;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.edwin.android.chat_in.R;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        AuthFragment fragment = (AuthFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_conversation);

        if (fragment == null) {

            fragment = AuthFragment.newInstance();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_auth, fragment);
            fragmentTransaction.commit();
        }
    }
}
