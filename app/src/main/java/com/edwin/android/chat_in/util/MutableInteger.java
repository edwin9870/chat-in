package com.edwin.android.chat_in.util;

/**
 * Created by Edwin Ramirez Ventura on 8/28/2017.
 */

public class MutableInteger {
    private int value;

    public MutableInteger() {
        this.value = 0;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
