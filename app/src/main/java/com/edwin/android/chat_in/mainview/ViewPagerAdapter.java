package com.edwin.android.chat_in.mainview;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edwin Ramirez Ventura on 7/15/2017.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {
    public static final String TAG = ViewPagerAdapter.class.getSimpleName();
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "fragment list position: " + position);
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {

        Log.d(TAG, "size: " + mFragmentList.size());
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

}
