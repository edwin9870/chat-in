package com.edwin.android.chat_in.data.dto;

import java.util.Date;

/**
 * Created by Edwin Ramirez Ventura on 8/17/2017.
 */

public class ConversationDTO {

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
}
