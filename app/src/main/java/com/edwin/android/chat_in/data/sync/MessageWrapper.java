package com.edwin.android.chat_in.data.sync;

/**
 * Created by Edwin Ramirez Ventura on 9/4/2017.
 */

public class MessageWrapper {

    private String message;
    private String senderNumber;
    private String recipientNumber;
    private Long messageDate;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderNumber() {
        return senderNumber;
    }

    public void setSenderNumber(String senderNumber) {
        this.senderNumber = senderNumber;
    }

    public String getRecipientNumber() {
        return recipientNumber;
    }

    public void setRecipientNumber(String recipientNumber) {
        this.recipientNumber = recipientNumber;
    }

    public Long getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(Long messageDate) {
        this.messageDate = messageDate;
    }

    @Override
    public String toString() {
        return "MessageWrapper{" +
                "message='" + message + '\'' +
                ", senderNumber='" + senderNumber + '\'' +
                ", recipientNumber='" + recipientNumber + '\'' +
                ", messageDate=" + messageDate +
                '}';
    }
}
