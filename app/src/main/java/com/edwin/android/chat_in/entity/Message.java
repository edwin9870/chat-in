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

    public Message() {
    }

    public Message(Date send, String message) {
        this.send = send;
        this.message = message;
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

    @Override
    public String toString() {
        return "Message{" +
                "send=" + send +
                ", message='" + message + '\'' +
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
    }

    protected Message(Parcel in) {
        long tmpSend = in.readLong();
        this.send = tmpSend == -1 ? null : new Date(tmpSend);
        this.message = in.readString();
    }

    public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
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
