package com.edwin.android.chat_in.util;

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
        contact.setMessages(new ArrayList<Message>());
        contact.getMessages().add(new Message(new Date(), "One morning, when Gregor Samsa woke from troubled dreams, he"));
        contact.getMessages().add(new Message(new Date(), "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo"));
        contacts.add(contact);

        contact = new Contact();
        contact.setName("Ana Maria");
        contact.setMessages(new ArrayList<Message>());
        contact.getMessages().add(new Message(new Date(), "Sed ut perspiciatis unde omnis iste natus error sit voluptatem"));
        contact.getMessages().add(new Message(new Date(), "But I must explain to you how all this mistaken"));
        contact.getMessages().add(new Message(new Date(), "Far far away, behind the word mountains, far from the"));
        contacts.add(contact);

        return contacts;
    }
}
