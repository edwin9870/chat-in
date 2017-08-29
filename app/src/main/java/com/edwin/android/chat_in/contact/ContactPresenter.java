package com.edwin.android.chat_in.contact;

import com.edwin.android.chat_in.data.repositories.ContactRepository;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Edwin Ramirez Ventura on 8/29/2017.
 */

public class ContactPresenter implements ContactMVP.Presenter {

    private final ContactMVP.View mView;
    private final ContactRepository mContactRepository;

    @Inject
    public ContactPresenter(ContactMVP.View mView, ContactRepository contactRepository) {
        this.mView = mView;
        mContactRepository = contactRepository;
    }

    @Inject
    public void setupListener() {
        mView.setPresenter(this);
    }

    @Override
    public void getContacts() {
        mContactRepository.getAllContacts()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .toList().subscribe(mView::showContacts);
    }
}
