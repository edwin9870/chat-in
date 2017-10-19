package com.edwin.android.chat_in.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.edwin.android.chat_in.auth.AuthVerificationFragment;
import com.google.firebase.auth.FirebaseAuth;

import javax.annotation.Nullable;

/**
 * Created by Edwin Ramirez Ventura on 8/7/2017.
 */

public final class ResourceUtil {


    public static final String TAG = ResourceUtil.class.getSimpleName();

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


}
