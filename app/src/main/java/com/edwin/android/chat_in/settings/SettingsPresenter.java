package com.edwin.android.chat_in.settings;

import android.net.Uri;
import android.util.Log;

import com.edwin.android.chat_in.chat.ChatFragment;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Edwin Ramirez Ventura on 8/29/2017.
 */

public class SettingsPresenter implements SettingsMVP.Presenter {

    public static final String TAG = SettingsPresenter.class.getSimpleName();
    private final SettingsMVP.View mView;
    private final FirebaseStorage mFirebaseStorage;

    @Inject
    public SettingsPresenter(SettingsMVP.View view, FirebaseStorage firebaseStorage) {
        mView = view;
        mFirebaseStorage = firebaseStorage;
    }

    @Inject
    public void setupListener() {
        mView.setPresenter(this);
    }

    @Override
    public void uploadImage(String fullPathImage) {
        Log.d(TAG, "Image received: " + fullPathImage);
        final String fileImagePath = "file://"+fullPathImage;
        final String extension = fileImagePath.substring(fileImagePath.lastIndexOf(".") +1,
                fileImagePath.length());
        Completable.create(emitter -> {
            final String pathToSaveImage = "images/profile/" + ChatFragment.MY_NUMBER + "." + extension;
            Log.d(TAG, "Path to save image: " + pathToSaveImage);
            final StorageReference imageReference = mFirebaseStorage.getReference().child(pathToSaveImage);
            final UploadTask uploadTask = imageReference.putFile(Uri.parse(fileImagePath));

            uploadTask.addOnSuccessListener(taskSnapshot -> emitter.onComplete());
            uploadTask.addOnFailureListener(emitter::onError);

        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }
}
