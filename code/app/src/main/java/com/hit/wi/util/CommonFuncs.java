package com.hit.wi.util;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by daofa on 2016/4/10.
 */
public class CommonFuncs {
    public final static void showToast(final Context context,
                                       final CharSequence text) {
        final TextView tv = new TextView(context);
        tv.setText(text);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(Color.WHITE);
        tv.setBackgroundResource(android.R.drawable.toast_frame);
        final Toast t = new Toast(context);
        t.setView(tv);
        t.setDuration(Toast.LENGTH_SHORT);
        t.show();
    }


}
