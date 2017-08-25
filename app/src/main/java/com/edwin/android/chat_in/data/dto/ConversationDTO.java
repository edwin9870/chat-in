package com.edwin.android.chat_in.data.dto;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Edwin Ramirez Ventura on 8/17/2017.
 */

public class ConversationDTO implements Parcelable {

    private long id;
    private int sentContactId;
    private int receiveContactId;
    private String message;
    private Date messageDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getFromContactId() {
        return sentContactId;
    }

    public void setSentContactId(int sentContactId) {
        this.sentContactId = sentContactId;
    }

    public int getToContactId() {
        return receiveContactId;
    }

    public void setReceiveContactId(int receiveContactId) {
        this.receiveContactId = receiveContactId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(Date messageDate) {
        this.messageDate = messageDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConversationDTO)) return false;

        ConversationDTO that = (ConversationDTO) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "ConversationDTO{" +
                "id=" + id +
                ", sentContactId=" + sentContactId +
                ", receiveContactId=" + receiveContactId +
                ", message='" + message + '\'' +
                ", messageDate=" + messageDate +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeInt(this.sentContactId);
        dest.writeInt(this.receiveContactId);
        dest.writeString(this.message);
        dest.writeLong(this.messageDate != null ? this.messageDate.getTime() : -1);
    }

    public ConversationDTO() {
    }

    protected ConversationDTO(Parcel in) {
        this.id = in.readLong();
        this.sentContactId = in.readInt();
        this.receiveContactId = in.readInt();
        this.message = in.readString();
        long tmpMessageDate = in.readLong();
        this.messageDate = tmpMessageDate == -1 ? null : new Date(tmpMessageDate);
    }

    public static final Parcelable.Creator<ConversationDTO> CREATOR = new Parcelable
            .Creator<ConversationDTO>() {
        @Override
        public ConversationDTO createFromParcel(Parcel source) {
            return new ConversationDTO(source);
        }

        @Override
        public ConversationDTO[] newArray(int size) {
            return new ConversationDTO[size];
        }
    };
}
