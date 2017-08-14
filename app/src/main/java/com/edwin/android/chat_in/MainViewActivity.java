package com.edwin.android.chat_in;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainViewActivity extends AppCompatActivity {

    private MainViewFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment = MainViewFragment.newInstance();

        fragmentTransaction.add(R.id.fragment_movie_view, fragment);
        fragmentTransaction.commit();


    }
}
