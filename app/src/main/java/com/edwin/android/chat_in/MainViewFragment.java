package com.edwin.android.chat_in;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.edwin.android.chat_in.util.ResourceUtil.getResourceColor;


public class MainViewFragment extends Fragment {


    public static final String TAG = MainViewFragment.class.getSimpleName();
    @BindView(R.id.toolbar_main_view)
    Toolbar mToolbar;
    Unbinder unbinder;
    @BindView(R.id.collapsing_toolbar_main_view)
    CollapsingToolbarLayout mMainViewCollapsingToolbar;

    public MainViewFragment() {
    }


    public static MainViewFragment newInstance() {
        MainViewFragment fragment = new MainViewFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_view, container, false);
        unbinder = ButterKnife.bind(this, view);

        Log.d(TAG, "mToolbar: " + mToolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.app_name).toUpperCase());

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
