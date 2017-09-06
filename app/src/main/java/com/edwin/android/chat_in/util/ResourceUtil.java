package com.edwin.android.chat_in.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import com.google.firebase.auth.FirebaseAuth;

import javax.annotation.Nullable;

/**
 * Created by Edwin Ramirez Ventura on 8/7/2017.
 */

public final class ResourceUtil {


    public static final int getResourceColor(Context context, int colorCode) {
        int color;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            color = context.getResources().getColor(colorCode, context.getTheme());
        } else {
            color = context.getResources().getColor(colorCode);
        }
        return color;
    }

    public static int dpToPx(Context context,int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @SuppressLint("MissingPermission")
    @Nullable
    public static String getPhoneNumber() {
        //TODO: Change to firebase auth
        //return FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().substring(2);
        return "8292779870";// Edwin number
        //return "8295848089"; //Cindy number
    }


}
