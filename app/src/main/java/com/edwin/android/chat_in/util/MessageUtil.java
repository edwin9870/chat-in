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
        contact.getMessages().add(new Message(new Date(), "ipsum dolor sit amet, consect", true));
        contacts.add(contact);

        contact = new Contact();
        contact.setName("Ana Maria");
        contact.setMessages(new ArrayList<Message>());
        contact.setProfileImage(R.drawable.ic_women_image);
        contact.getMessages().add(new Message(new Date(), "¿Qué cuatrimestre?", true));
        contact.getMessages().add(new Message(new Date(), "3", false));
        contact.getMessages().add(new Message(new Date(), "Prioridades, tendrás que organizarte bien y saber a que dedicarle tiempo y en que momento, listo.", true));
        contact.getMessages().add(new Message(new Date(), " Busca buenos grupos en cada materia...", true));
        contact.getMessages().add(new Message(new Date(), "Prioridades, tendrás que organizarte bien y saber a que dedicarle tiempo y en que momento, listo.", true));
        contact.getMessages().add(new Message(new Date(), "Estimados compañeros, alguien sabe de quien es esta seccion.", true));
        contact.getMessages().add(new Message(new Date(), "Esta bien entonces", false));
        contacts.add(contact);

        return contacts;
    }
}