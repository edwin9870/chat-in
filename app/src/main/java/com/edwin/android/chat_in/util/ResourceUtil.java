package com.edwin.android.chat_in.util;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;

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


}
