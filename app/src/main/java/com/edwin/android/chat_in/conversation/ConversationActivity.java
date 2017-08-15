package com.edwin.android.chat_in.conversation;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.edwin.android.chat_in.R;
import com.edwin.android.chat_in.entity.Contact;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConversationActivity extends AppCompatActivity {


    public static final String TAG = ConversationActivity.class.getSimpleName();
    @BindView(R.id.toolbar_main_view)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name).toUpperCase());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ConversationFragment fragment = (ConversationFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_conversation);

        if (fragment == null) {
            Contact contact = getIntent().getExtras().getParcelable(ConversationFragment
                    .ARGUMENT_CONTACT);
            Log.d(TAG, "Contact received: " + contact);

            fragment = ConversationFragment.newInstance(contact);
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_conversation, fragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
