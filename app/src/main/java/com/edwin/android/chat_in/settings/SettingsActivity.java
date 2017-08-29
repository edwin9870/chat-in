package com.edwin.android.chat_in.settings;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.edwin.android.chat_in.R;
import com.edwin.android.chat_in.configuration.di.ApplicationModule;
import com.edwin.android.chat_in.data.fcm.FcmModule;
import com.edwin.android.chat_in.data.repositories.DatabaseModule;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_settings_activity)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.toolbar_settings_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SettingsFragment settingsFragment = (SettingsFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_settings);

        if (settingsFragment == null) {
            settingsFragment = SettingsFragment.newInstance();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_settings, settingsFragment);
            fragmentTransaction.commit();
        }

        DaggerSettingsComponent.builder()
                .settingsPresenterModule(new SettingsPresenterModule(settingsFragment))
                .applicationModule(new ApplicationModule(this))
                .databaseModule(new DatabaseModule())
                .fcmModule(new FcmModule())
                .build().getPresenter();

    }
}
