package com.edwin.android.chat_in.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.edwin.android.chat_in.auth.AuthVerificationFragment;

import javax.annotation.Nullable;

/**
 * Created by Edwin Ramirez Ventura on 10/18/2017.
 */

public class PhoneNumberUtil {
    @NonNull
    public static String formatPhoneNumber(String phoneNumber) {

        if(phoneNumber.substring(0,1).equals("+")) {
            phoneNumber = phoneNumber.substring(1);
        }

        String firstNumber = phoneNumber.substring(0, 1);
        if (firstNumber.equals("1")) {
            return phoneNumber;
        } else {
            return "1" + phoneNumber;
        }
    }

    @SuppressLint("MissingPermission")
    @Nullable
    public static String getPhoneNumber(Context context) {
        //TODO: Change to firebase auth
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (context);
        final String phoneNumber = sharedPreferences.getString(AuthVerificationFragment
                .PREF_PHONE_NUMBER, null);
        Log.d(ResourceUtil.TAG, "phone number: " + phoneNumber);
        return phoneNumber;
    }
}
