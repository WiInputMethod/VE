package com.hit.wi.t9.functions;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public final class PackageUtil {
    public final static String getVersionName(final Context context) {
        final PackageManager packageManager = context.getPackageManager();
        String versionName = "";
        try {
            final PackageInfo packInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            versionName = packInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }
}
