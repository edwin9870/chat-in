package com.edwin.android.chat_in.contact;

import android.app.Activity;
import android.content.Context;

import com.edwin.android.chat_in.data.dto.ContactDTO;

import java.util.List;

/**
 * Created by Edwin Ramirez Ventura on 8/29/2017.
 */

public interface ContactMVP {

    interface View {
        void setPresenter(Presenter presenter);
        void showContacts(List<ContactDTO> contacts);
        void showMessage(String message);
    }

    interface Presenter {
        void getContacts();
        void cleanResource(Context context);
        void refreshContacts();
        void syncContacts();
    }
}