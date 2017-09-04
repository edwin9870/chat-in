package com.edwin.android.chat_in.data.dto;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Edwin Ramirez Ventura on 8/17/2017.
 */

public class ConversationDTO implements Parcelable {

    private long id;
    private int senderContactId;
    private int recipientContactId;
    private String message;
    private long messageDate;
    private String conversationGroupId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getSenderContactId() {
        return senderContactId;
    }

    public void setSenderContactId(int senderContactId) {
        this.senderContactId = senderContactId;
    }

    public int getRecipientContactId() {
        return recipientContactId;
    }

    public void setRecipientContactId(int recipientContactId) {
        this.recipientContactId = recipientContactId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(long messageDate) {
        this.messageDate = messageDate;
    }

    public String getConversationGroupId() {
        return conversationGroupId;
    }

    public void setConversationGroupId(String conversationGroupId) {
        this.conversationGroupId = conversationGroupId;
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
                ", senderContactId=" + senderContactId +
                ", recipientContactId=" + recipientContactId +
                ", message='" + message + '\'' +
                ", messageDate=" + messageDate +
                ", conversationGroupId='" + conversationGroupId + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeInt(this.senderContactId);
        dest.writeInt(this.recipientContactId);
        dest.writeString(this.message);
        dest.writeLong(this.messageDate);
        dest.writeString(this.conversationGroupId);
    }

    public ConversationDTO() {
    }

    protected ConversationDTO(Parcel in) {
        this.id = in.readLong();
        this.senderContactId = in.readInt();
        this.recipientContactId = in.readInt();
        this.message = in.readString();
        this.messageDate = in.readLong();
        this.conversationGroupId = in.readString();
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
