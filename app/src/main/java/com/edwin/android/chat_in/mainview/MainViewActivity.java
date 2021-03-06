package com.edwin.android.chat_in.mainview;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v13.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.edwin.android.chat_in.R;
import com.edwin.android.chat_in.auth.AuthActivity;
import com.edwin.android.chat_in.chat.ChatFragment;
import com.edwin.android.chat_in.chat.ChatPresenterModule;
import com.edwin.android.chat_in.chat.DaggerChatComponent;
import com.edwin.android.chat_in.configuration.di.ApplicationModule;
import com.edwin.android.chat_in.contact.ContactFragment;
import com.edwin.android.chat_in.contact.ContactPresenterModule;
import com.edwin.android.chat_in.contact.DaggerContactComponent;
import com.edwin.android.chat_in.data.fcm.FcmModule;
import com.edwin.android.chat_in.data.repositories.DatabaseModule;
import com.edwin.android.chat_in.data.sync.DaggerSyncComponent;
import com.edwin.android.chat_in.data.sync.SyncComponent;
import com.edwin.android.chat_in.data.sync.SyncDatabase;
import com.edwin.android.chat_in.util.PhoneNumberUtil;
import com.edwin.android.chat_in.util.SecurityUtil;
import com.edwin.android.chat_in.views.WrapContentViewPager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainViewActivity extends AppCompatActivity {


    public static final String TAG = MainViewActivity.class.getSimpleName();
    public static final int CHAT_FRAGMENT_TAB = 0;
    public static final int CONTACT_FRAGMENT_TAB = 1;
    public static final int REQUEST_PERMISSION_READ_PHONE_STATUS_AND_CONTACT = 51212;
    @BindView(R.id.toolbar_settings_activity)
    Toolbar mToolbar;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.pager_tab_content)
    WrapContentViewPager mViewPager;
    private ViewPagerAdapter mAdapter;
    private ChatFragment mChatFragment;
    private ContactFragment mContactFragment;
    private SyncDatabase mSyncDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name).toUpperCase());

        final String[] permissions = {
                Manifest.permission.READ_CONTACTS};
        final boolean hasPermissions = SecurityUtil.hasPermissions(this, permissions);

        if(!hasPermissions) {
            ActivityCompat.requestPermissions(MainViewActivity.this,
                    permissions,
                    REQUEST_PERMISSION_READ_PHONE_STATUS_AND_CONTACT);
        } else {
            setupActivity();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (mSyncDatabase != null &&
                mSyncDatabase.getConversationDisposable() != null &&
                !mSyncDatabase.getConversationDisposable().isDisposed()) {
            Log.d(TAG, "Disposing syncConversation database conversation");
            mSyncDatabase.getConversationDisposable().dispose();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_READ_PHONE_STATUS_AND_CONTACT:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permissions granted, showing activity");
                        setupActivity();
                } else {

                    Toast.makeText(this, getString(R.string.permission_phone_number), Toast.LENGTH_LONG).show();
                }
                break;

        }
    }
    private void setupActivity() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String phoneNumber = PhoneNumberUtil.getPhoneNumber(this);
        if(phoneNumber == null || phoneNumber.isEmpty()) {
            final Intent intent = new Intent(this, AuthActivity.class);
            Log.d(TAG, "User is not logged, starting AuthActivity");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Log.d(TAG, "Closing MainViewActivity");
            finish();
            return;
        }

        setupFragment();

        final SyncComponent syncComponent = DaggerSyncComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .databaseModule(new DatabaseModule())
                .fcmModule(new FcmModule())
                .build();
        mSyncDatabase = syncComponent.getSyncDatabase();

        DaggerChatComponent.builder().chatPresenterModule(new
                ChatPresenterModule(mChatFragment))
                .applicationModule(new ApplicationModule(this))
                .databaseModule(new DatabaseModule())
                .fcmModule(new FcmModule())
                .build().getChatPresenter();

        DaggerContactComponent.builder()
                .contactPresenterModule(new ContactPresenterModule(mContactFragment))
                .applicationModule(new ApplicationModule(this))
                .databaseModule(new DatabaseModule())
                .fcmModule(new FcmModule())
                .build().getPresenter();

        setupViewPager();

        Log.d(TAG, "Phone number: "+ PhoneNumberUtil.getPhoneNumber(this));
        mSyncDatabase.syncConversation();

    }

    private void setupFragment() {
        mAdapter = new ViewPagerAdapter(getFragmentManager());

        mChatFragment = (ChatFragment) getFragmentManager().findFragmentByTag
                (getFragmentName(R.id.pager_tab_content, CHAT_FRAGMENT_TAB));
        if(mChatFragment == null) {
            mChatFragment = ChatFragment.newInstance();
        }

        mContactFragment = (ContactFragment) getFragmentManager().findFragmentByTag
                (getFragmentName(R.id.pager_tab_content, CONTACT_FRAGMENT_TAB));
        if(mContactFragment == null) {
            mContactFragment = ContactFragment.newInstance();
        }
    }


    private void setupViewPager() {
        mAdapter.addFragment(mChatFragment, getString(R.string.main_view_tab_layout_chat_tab));
        mAdapter.addFragment(mContactFragment, getString(R.string.main_view_tab_layout_contacts_tab));
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

    private static String getFragmentName(int viewPagerId, int index) {
        return "android:switcher:" + viewPagerId + ":" + index;
    }

}
