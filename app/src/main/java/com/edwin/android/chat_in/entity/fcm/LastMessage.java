
package com.edwin.android.chat_in.entity.fcm;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class LastMessage {

    @SerializedName("lastMessage")
    private String mLastMessage;
    @SerializedName("timestamp")
    private long mTimestamp;

    public String getLastMessage() {
        return mLastMessage;
    }

    public void setLastMessage(String lastMessage) {
        mLastMessage = lastMessage;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(long timestamp) {
        mTimestamp = timestamp;
    }

    @Override
    public String toString() {
        return "LastMessage{" +
                "mLastMessage='" + mLastMessage + '\'' +
                ", mTimestamp='" + mTimestamp + '\'' +
                '}';
    }
}
