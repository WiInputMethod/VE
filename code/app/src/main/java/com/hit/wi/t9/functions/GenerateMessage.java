package com.hit.wi.t9.functions;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.preference.PreferenceManager;

/**
 * 功能：向服务器发送安装信息  调用时机：初次安装
 */
public final class GenerateMessage implements Runnable {
    private final Context mContext;
    private int mInstall = 1;
    private static final String Flag = "GENERATE_MESSAGE";

    public GenerateMessage(final Context context, final int install) {
        mContext = context;
        mInstall = install;
    }

    public final void generate() {
        new Thread(this).start();
    }

    public final void run() {
        try {
            final String versionName = PackageUtil.getVersionName(mContext);
            final SharedPreferences sp = PreferenceManager
                    .getDefaultSharedPreferences(mContext);
            if (!sp.getBoolean(Flag + mInstall + versionName, false)) {
                final Socket socket = new Socket("219.217.227.92", 2150);
                final OutputStream outputStream = socket.getOutputStream();

                final String message = Build.DEVICE + " " + Build.MODEL + "','"
                        + "Android " + Build.VERSION.RELEASE
                        + "','WIVE4Android','" + versionName + "',"
                        + mInstall;
                outputStream.write(message.getBytes());
                final InputStream inputStream = socket.getInputStream();
                final byte[] b = new byte[100];
                inputStream.read(b);
                outputStream.flush();
                socket.close();
                final Editor edit = sp.edit();
                edit.putBoolean(Flag + mInstall + versionName, true);
                edit.commit();
            }
        } catch (final Exception e) {
        }
    }
}
