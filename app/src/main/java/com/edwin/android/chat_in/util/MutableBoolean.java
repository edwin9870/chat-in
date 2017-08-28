package com.edwin.android.chat_in.util;

/**
 * Created by Edwin Ramirez Ventura on 8/28/2017.
 */

public class MutableBoolean {
    private boolean value;

    public MutableBoolean() {
        this.value = false;
    }

    public boolean is() {
        return this.value;
    }

    public void setTrue() {
        this.value = true;
    }

    public void setFalse() {
        this.value = false;
    }
}
