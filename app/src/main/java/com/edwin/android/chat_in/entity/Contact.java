package com.edwin.android.chat_in.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Edwin Ramirez Ventura on 8/14/2017.
 */

public class Contact implements Parcelable {

    private String name;
    private String profileImage;
    private List<Message> messages;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "name='" + name + '\'' +
                ", profileImage='" + profileImage + '\'' +
                ", messages=" + messages +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.profileImage);
        dest.writeTypedList(this.messages);
    }

    public Contact() {
    }

    protected Contact(Parcel in) {
        this.name = in.readString();
        this.profileImage = in.readString();
        this.messages = in.createTypedArrayList(Message.CREATOR);
    }

    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel source) {
            return new Contact(source);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
}
