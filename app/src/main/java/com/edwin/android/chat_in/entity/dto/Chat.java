package com.edwin.android.chat_in.entity.dto;

import java.util.Date;

/**
 * Created by Edwin Ramirez Ventura on 8/17/2017.
 */

public class Chat {

    private String userName;
    private String LastMessage;
    private int profileImage;
    private Date messageDate;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
                ", LastMessage='" + LastMessage + '\'' +
                ", profileImage=" + profileImage +
                ", messageDate=" + messageDate +
                '}';
    }
}
