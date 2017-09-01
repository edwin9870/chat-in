package com.edwin.android.chat_in.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v13.app.ActivityCompat;

/**
 * Created by Edwin Ramirez Ventura on 9/1/2017.
 */

public class SecurityUtil {

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
