package com.edwin.android.chat_in.data.dto;

/**
 * Created by Edwin Ramirez Ventura on 8/24/2017.
 */

public class ContactDTO {

    private int id;
    private String userName;
    private long number;
    private String profileImagePath;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContactDTO)) return false;

        ContactDTO that = (ContactDTO) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "ContactDTO{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", number=" + number +
                ", profileImagePath='" + profileImagePath + '\'' +
                '}';
    }
}
