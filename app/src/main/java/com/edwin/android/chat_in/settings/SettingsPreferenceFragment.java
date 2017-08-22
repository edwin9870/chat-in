package com.edwin.android.chat_in.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.edwin.android.chat_in.R;

/**
 * Created by Edwin Ramirez Ventura on 8/22/2017.
 */

public class SettingsPreferenceFragment extends PreferenceFragment {

    public static SettingsPreferenceFragment newInstance() {
        SettingsPreferenceFragment fragment = new SettingsPreferenceFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}
