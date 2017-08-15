package com.edwin.android.chat_in.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Edwin Ramirez Ventura on 8/14/2017.
 */

public class Contact implements Parcelable {

    private String name;
    private int profileImage;
    private List<Message> receivedMessage;
    private List<Message> sentMessage;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(int profileImage) {
        this.profileImage = profileImage;
    }

    public List<Message> getReceivedMessage() {
        return receivedMessage;
    }

    public void setReceivedMessage(List<Message> receivedMessage) {
        this.receivedMessage = receivedMessage;
    }

    public List<Message> getSentMessage() {
        return sentMessage;
    }

    public void setSentMessage(List<Message> sentMessage) {
        this.sentMessage = sentMessage;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "name='" + name + '\'' +
                ", profileImage='" + profileImage + '\'' +
                ", receivedMessage=" + receivedMessage +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.profileImage);
        dest.writeTypedList(this.receivedMessage);
    }

    public Contact() {
    }

    protected Contact(Parcel in) {
        this.name = in.readString();
        this.profileImage = in.readInt();
        this.receivedMessage = in.createTypedArrayList(Message.CREATOR);
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
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
