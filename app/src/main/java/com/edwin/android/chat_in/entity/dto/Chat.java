package com.edwin.android.chat_in.entity.dto;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Edwin Ramirez Ventura on 8/17/2017.
 */

public class Chat implements Parcelable {

    private String userName;
    private String phoneNumber;
    private String LastMessage;
    private int profileImage;
    private Date messageDate;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLastMessage() {
        return LastMessage;
    }

    public void setLastMessage(String lastMessage) {
        LastMessage = lastMessage;
    }

    public Date getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(Date messageDate) {
        this.messageDate = messageDate;
    }

    public int getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(int profileImage) {
        this.profileImage = profileImage;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "userName='" + userName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", LastMessage='" + LastMessage + '\'' +
                ", profileImage=" + profileImage +
                ", messageDate=" + messageDate +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userName);
        dest.writeString(this.phoneNumber);
        dest.writeString(this.LastMessage);
        dest.writeInt(this.profileImage);
        dest.writeLong(this.messageDate != null ? this.messageDate.getTime() : -1);
    }

    public Chat() {
    }

    protected Chat(Parcel in) {
        this.userName = in.readString();
        this.phoneNumber = in.readString();
        this.LastMessage = in.readString();
        this.profileImage = in.readInt();
        long tmpMessageDate = in.readLong();
        this.messageDate = tmpMessageDate == -1 ? null : new Date(tmpMessageDate);
    }

    public static final Parcelable.Creator<Chat> CREATOR = new Parcelable.Creator<Chat>() {
        @Override
        public Chat createFromParcel(Parcel source) {
            return new Chat(source);
        }

        @Override
        public Chat[] newArray(int size) {
            return new Chat[size];
        }
    };
}
