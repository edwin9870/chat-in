package com.edwin.android.chat_in.mainview;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.edwin.android.chat_in.R;
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
import com.edwin.android.chat_in.settings.SettingsActivity;
import com.edwin.android.chat_in.views.WrapContentViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainViewActivity extends AppCompatActivity {


    public static final String TAG = MainViewActivity.class.getSimpleName();
    public static final int CHAT_FRAGMENT_TAB = 0;
    public static final int CONTACT_FRAGMENT_TAB = 1;
    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 54515;
    @BindView(R.id.toolbar_settings_activity)
    Toolbar mToolbar;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.pager_tab_content)
    WrapContentViewPager mViewPager;
    private ViewPagerAdapter mAdapter;
    private ChatFragment mChatFragment;
    private ContactFragment mContactFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name).toUpperCase());

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
        } else {
            setupActivity();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permissions granted, showing activity");
                    setupActivity();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
        }
    }


    private void setupActivity() {

        setupFragment();

        final SyncComponent syncComponent = DaggerSyncComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .databaseModule(new DatabaseModule())
                .fcmModule(new FcmModule())
                .build();
        final SyncDatabase syncDatabase = syncComponent.getSyncDatabase();
        syncDatabase.syncContact();
        syncDatabase.syncConversation(ChatFragment.MY_NUMBER);

        DaggerChatComponent.builder().chatPresenterModule(new
                ChatPresenterModule(mChatFragment))
                .applicationModule(new ApplicationModule(this))
                .databaseModule(new DatabaseModule())
                .build().getChatPresenter();

        DaggerContactComponent.builder()
                .contactPresenterModule(new ContactPresenterModule(mContactFragment))
                .applicationModule(new ApplicationModule(this))
                .databaseModule(new DatabaseModule())
                .build().getPresenter();

        setupViewPager();
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

    private void requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_CONTACTS)) {
            Toast.makeText(this, R.string.permisson_read_contacts_explanation, Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSIONS_REQUEST_READ_CONTACTS);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_view, menu);
        return true;
    }

    private static String getFragmentName(int viewPagerId, int index) {
        return "android:switcher:" + viewPagerId + ":" + index;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_setting_action:
                final Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }
}
