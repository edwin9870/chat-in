package com.edwin.android.chat_in.util;

import android.support.annotation.NonNull;

/**
 * Created by Edwin Ramirez Ventura on 10/18/2017.
 */

public class PhoneNumber {
    @NonNull
    public static String formatPhoneNumber(String phoneNumber) {
        String firstNumber = phoneNumber.substring(0, 1);
        if (firstNumber.equals("1")) {
            return phoneNumber;
        } else {
            return "1" + phoneNumber;
        }
    }
}
