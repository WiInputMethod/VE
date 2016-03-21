package com.hit.wi.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by xujc on 9/2/15.
 */
public class VersionUtil {
    public static String getVersionName(final Context context) {
        final PackageManager packageManager = context.getPackageManager();
        String versionName = "";
        try {
            final PackageInfo packInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            versionName = packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static int getVersionCode(final Context context) {
        final PackageManager packageManager = context.getPackageManager();
        int code = 0;
        try {
            final PackageInfo packInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            code = packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return code;
    }
}
