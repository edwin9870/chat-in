package com.edwin.android.chat_in.settings;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.edwin.android.chat_in.R;
import com.edwin.android.chat_in.data.fcm.ContactRepositoryFcm;
import com.edwin.android.chat_in.data.repositories.ContactRepository;
import com.edwin.android.chat_in.util.FileUtil;
import com.edwin.android.chat_in.util.ResourceUtil;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

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
    private final ContactRepository mContactRepository;
    private final Context mContext;
    private final ContactRepositoryFcm mContactRepositoryFcm;

    @Inject
    public SettingsPresenter(Context context, SettingsMVP.View view,
                             FirebaseStorage firebaseStorage,
                             ContactRepository contactRepository,
                             ContactRepositoryFcm contactRepositoryFcm) {
        mContext = context;
        mView = view;
        mFirebaseStorage = firebaseStorage;
        mContactRepository = contactRepository;
        mContactRepositoryFcm = contactRepositoryFcm;
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
        final String imageFileNameToSave = UUID.randomUUID().toString() + "." + extension;
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
                .subscribe(() -> {
                    Log.d(TAG, "Upload completed");
                    mContactRepository.getContactById(ContactRepository.OWNER_CONTACT_ID)
                            .subscribe(contact -> {
                        contact.setProfileImagePath(imageFileNameToSave);
                        mContactRepository.updateContact(contact)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> SettingsPresenter.this.loadProfileImage(false));
                        Log.d(TAG, "Start to update Firebase DB profile");
                        mContactRepositoryFcm.update(contact).subscribeOn(Schedulers.io()).subscribe();
                    });
        });
    }

    @Override
    public void loadProfileImage(boolean enableCache) {
        Log.d(TAG, "loadProfileImage called");
        mContactRepository.getContactById(ContactRepository.OWNER_CONTACT_ID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(contactDTO -> !(contactDTO.getProfileImagePath() == null ||
                                        contactDTO.getProfileImagePath().isEmpty()))
                .switchIfEmpty(observer -> mView.showImageProfile(R.drawable.ic_faceless_man, true))
                .subscribe(contactDTO -> mView.showImageProfile(
                        FileUtil.getImageFile(mContext, contactDTO.getProfileImagePath()),
                        enableCache));

    }
}
