package com.edwin.android.chat_in.util;

import com.edwin.android.chat_in.R;
import com.edwin.android.chat_in.entity.Contact;
import com.edwin.android.chat_in.entity.Message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Edwin Ramirez Ventura on 8/14/2017.
 */

public class MessageUtil {

    public static List<Contact> getContacts() {
        List<Contact> contacts = new ArrayList<>();

        Contact contact = new Contact();
        contact.setName("Juan Pablo Duarte");
        contact.setProfileImage(R.drawable.ic_man_image);
        contact.setMessages(new ArrayList<Message>());
        contact.setMessages(new ArrayList<Message>());
        contact.getMessages().add(new Message(new Date(), "One morning, when Gregor Samsa woke from troubled dreams, he", true));
        contact.getMessages().add(new Message(new Date(), "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo", true));
        contact.getMessages().add(new Message(new Date(), "Si, estoy muy bien la verdad", false));
        contacts.add(contact);

        contact = new Contact();
        contact.setName("Ana Maria");
        contact.setMessages(new ArrayList<Message>());
        contact.setProfileImage(R.drawable.ic_women_image);
        contact.getMessages().add(new Message(new Date(), "Sed ut perspiciatis unde omnis iste natus error sit voluptatem", true));
        contact.getMessages().add(new Message(new Date(), "But I must explain to you how all this mistaken", true));
        contact.getMessages().add(new Message(new Date(), "But I must explain to you how all this mistaken", false));
        contacts.add(contact);

        return contacts;
    }
}
