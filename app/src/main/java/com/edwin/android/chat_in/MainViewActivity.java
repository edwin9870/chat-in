package com.edwin.android.chat_in;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.edwin.android.chat_in.views.WrapContentViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainViewActivity extends AppCompatActivity {


    public static final String TAG = MainViewActivity.class.getSimpleName();
    @BindView(R.id.toolbar_main_view)
    Toolbar mToolbar;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.pager_tab_content)
    WrapContentViewPager mViewPager;
    private ViewPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name).toUpperCase());

        mAdapter = new ViewPagerAdapter(getFragmentManager());

        ChatFragment chatFragment = ChatFragment.newInstance();
        ContactFragment contactFragment = ContactFragment.newInstance();
        mAdapter.addFragment(chatFragment, "CHAT");
        mAdapter.addFragment(contactFragment, "CONTACTOS");
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mViewPager.reMeasureCurrentPage(mViewPager.getCurrentItem());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }
}