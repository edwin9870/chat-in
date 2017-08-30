package com.edwin.android.chat_in.settings;


import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.edwin.android.chat_in.R;
import com.edwin.android.chat_in.util.FileUtil;
import com.edwin.android.chat_in.views.RoundedImageView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

public class SettingsFragment extends Fragment implements SettingsMVP.View{

    public static final String TAG = SettingsFragment.class.getSimpleName();
    public static final int REQUEST_CODE_PICK_IMAGE = 51515;
    public static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 5545;
    @BindView(R.id.image_profile)
    RoundedImageView mProfileImageView;
    Unbinder unbinder;
    private SettingsMVP.Presenter mPresenter;
    private Picasso mPicasso;

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
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
        mPicasso = Picasso.with(getActivity());

        mPresenter.loadProfileImage(true);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void setPresenter(SettingsMVP.Presenter presenter) {
        Log.d(TAG, "Setting Presenter");
        mPresenter = presenter;
    }

    @Override
    public void showImageProfile(int resourceId, boolean enableCache) {

        final RequestCreator requestCreator = mPicasso.load(R.drawable.ic_man_image);

        if(!enableCache) {
            requestCreator.memoryPolicy(MemoryPolicy.NO_CACHE);
        }
        requestCreator.fit().into(mProfileImageView);
    }

    @Override
    public void showImageProfile(File imageFile, boolean enableCache) {
        final RequestCreator requestCreator = mPicasso.load(imageFile);

        if(!enableCache) {
            requestCreator.memoryPolicy(MemoryPolicy.NO_CACHE);
        }

        requestCreator.fit().into(mProfileImageView);
    }

    @OnClick({R.id.image_profile})
    public void profileImageClicked() {

        final boolean externalStorageAvailable = FileUtil.isExternalStorageAvailable();
        Log.d(TAG, "externalStorageAvailable: "+ externalStorageAvailable);
        if (externalStorageAvailable && ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Requesting permission");
            requestPermissions();
        } else {
            pickImage();
        }
    }

    private void pickImage() {
        Log.d(TAG, "Starting intent to pick an image");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image_profile)), REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_PICK_IMAGE) {
            Uri imageUri = data.getData();
            Log.d(TAG, "Image obtained, uri: " + imageUri);
            mPresenter.uploadImage(getImageFullPathFromUri(imageUri));
        }
    }

    public String getImageFullPathFromUri(Uri uri) {
        Cursor cursor = null;
        try {
            cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            final String path = cursor.getString(idx);
            return path;
        } finally {
            if(cursor!= null) {
                cursor.close();
            }
        }
    }

    private void requestPermissions() {
        if (FragmentCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(getActivity(), "Read files in SDCards is required to pick images", Toast.LENGTH_LONG).show();
        } else {
            FragmentCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult called");
        switch (requestCode) {
            case PERMISSION_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permissions granted");
                    pickImage();
                } else {
                    Log.d(TAG, "Permissions not granted");
                }
                break;
        }
    }
}
