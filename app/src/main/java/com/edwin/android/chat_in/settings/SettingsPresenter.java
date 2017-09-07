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
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
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
        if(fullPathImage == null || fullPathImage.isEmpty()) {
            Log.d(TAG, "Image path is null");
            return;
        }
        final String fileImagePath = "file://"+fullPathImage;
        final String extension = fileImagePath.substring(fileImagePath.lastIndexOf(".") +1,
                fileImagePath.length());
        final String imageFileNameToSave = UUID.randomUUID().toString() + "." + extension;

        final Completable saveFileObservable = Completable.create(e -> {
            try {
                Log.d(TAG, "Saving image");
                FileUtil.saveImage(mContext, Uri.parse(fileImagePath), imageFileNameToSave);
                Log.d(TAG, "Image saved");
                e.onComplete();
            } catch (Exception exception) {
                Log.e(TAG, "Error saving file", exception);
                e.onError(exception);
            }
        }).cache();

        saveFileObservable.subscribeOn(Schedulers.io())
                .subscribe(() -> {
                    Log.d(TAG, "Starting to upload file to firebase");
            final String pathToSaveImage = "images/profile/" + imageFileNameToSave;
            Log.d(TAG, "Path to save image: " + pathToSaveImage);
            final StorageReference imageReference = mFirebaseStorage.getReference().child(pathToSaveImage);

            final UploadTask fileToUpload = imageReference.putFile(Uri.parse(fileImagePath));
                    fileToUpload.addOnSuccessListener(taskSnapshot -> {
                Log.d(TAG, "Upload completed");
                mContactRepository.getContactById(ContactRepository.OWNER_CONTACT_ID)
                        .subscribe(contact -> {
                            contact.setProfileImagePath(imageFileNameToSave);
                            Log.d(TAG, "Start to update Firebase DB profile");
                            mContactRepositoryFcm.update(contact).subscribeOn(Schedulers
                                    .io()).subscribe();
                        });
            });
        });

        saveFileObservable.subscribeOn(Schedulers.io())
                .subscribe(() ->
                        mContactRepository.getContactById(ContactRepository.OWNER_CONTACT_ID).subscribe(contact -> {
                            Log.d(TAG, "Uploading activity with new picture");
                            contact.setProfileImagePath(imageFileNameToSave);
                            mContactRepository.updateContact(contact)
                                    .subscribeOn(Schedulers.computation())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(() -> SettingsPresenter.this.loadProfileImage(false));
                        })
                );


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
