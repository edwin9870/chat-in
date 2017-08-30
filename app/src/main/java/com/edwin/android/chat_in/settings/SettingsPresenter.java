package com.edwin.android.chat_in.settings;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.edwin.android.chat_in.chat.ChatFragment;
import com.edwin.android.chat_in.data.repositories.ContactRepository;
import com.edwin.android.chat_in.util.FileUtil;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Edwin Ramirez Ventura on 8/29/2017.
 */

public class SettingsPresenter implements SettingsMVP.Presenter {

    public static final String TAG = SettingsPresenter.class.getSimpleName();
    private final SettingsMVP.View mView;
    private final FirebaseStorage mFirebaseStorage;
    private final ContactRepository mContactRepository;
    private final Context mContext;

    @Inject
    public SettingsPresenter(Context context, SettingsMVP.View view,
                             FirebaseStorage firebaseStorage,
                             ContactRepository contactRepository) {
        mContext = context;
        mView = view;
        mFirebaseStorage = firebaseStorage;
        mContactRepository = contactRepository;
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
        final String imageFileNameToSave = ChatFragment.MY_NUMBER + "." + extension;
        Completable.create(emitter -> {
            final String pathToSaveImage = "images/profile/" + imageFileNameToSave;
            Log.d(TAG, "Path to save image: " + pathToSaveImage);
            final StorageReference imageReference = mFirebaseStorage.getReference().child(pathToSaveImage);
            final UploadTask uploadTask = imageReference.putFile(Uri.parse(fileImagePath));
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                try {
                    Log.d(TAG, "Saving image to app directory");
                    FileUtil.saveImage(mContext, Uri.parse(fileImagePath), imageFileNameToSave);
                    emitter.onComplete();
                } catch (IOException e) {
                    emitter.onError(e);
                }
            });
            uploadTask.addOnFailureListener(emitter::onError);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(() -> {Log.d(TAG, "Upload completed");
                    mContactRepository.getContactById(ContactRepository.OWNER_CONTACT_ID).subscribe(contact -> {
                        contact.setProfileImagePath(imageFileNameToSave);
                        mContactRepository.updateContact(contact).subscribe();
                    });
        });
    }
}
