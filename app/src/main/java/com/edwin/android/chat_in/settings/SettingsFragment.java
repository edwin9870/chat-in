package com.edwin.android.chat_in.settings;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edwin.android.chat_in.R;
import com.edwin.android.chat_in.views.RoundedImageView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SettingsFragment extends Fragment {

    @BindView(R.id.image_profile)
    RoundedImageView mProfileImageView;
    Unbinder unbinder;
    @BindView(R.id.text_person_name)
    TextView mPersonNameTextView;

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_settings, container, false);
        unbinder = ButterKnife.bind(this, view);

        Picasso picasso = Picasso.with(getActivity());
        picasso.load(R.drawable.ic_man_image).fit().into(mProfileImageView);

        //TODO: Get person name
        mPersonNameTextView.setText("Juan Pablo Duarte");
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
