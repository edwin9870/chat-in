package com.edwin.android.chat_in.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Edwin Ramirez Ventura on 8/14/2017.
 */

public class Message implements Parcelable {


    private Date send;
    private String message;
    private boolean isMessageReceived;

    public Message() {
    }

    public Message(Date send, String message, boolean isMessageReceived) {
        this.send = send;
        this.message = message;
        this.isMessageReceived = isMessageReceived;
    }

    public Date getSend() {
        return send;
    }

    public void setSend(Date send) {
        this.send = send;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isMessageReceived() {
        return isMessageReceived;
    }

    public void setMessageReceived(boolean messageReceived) {
        isMessageReceived = messageReceived;
    }

    @Override
    public String toString() {
        return "Message{" +
                "send=" + send +
                ", message='" + message + '\'' +
                ", isMessageReceived=" + isMessageReceived +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.send != null ? this.send.getTime() : -1);
        dest.writeString(this.message);
        dest.writeByte(this.isMessageReceived ? (byte) 1 : (byte) 0);
    }

    protected Message(Parcel in) {
        long tmpSend = in.readLong();
        this.send = tmpSend == -1 ? null : new Date(tmpSend);
        this.message = in.readString();
        this.isMessageReceived = in.readByte() != 0;
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel source) {
            return new Message(source);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
}
