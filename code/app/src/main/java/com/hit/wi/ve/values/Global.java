
package com.hit.wi.ve.values;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.hit.wi.jni.WIInputMethodNK;
import com.hit.wi.jni.WIInputMethod;
import com.hit.wi.ve.SoftKeyboard;
import com.hit.wi.ve.datastruct.InputAction;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class Global {

    public static final int UP = 0;
    private static final int DOWM = 1;
    public static final int LEFT = 2;
    private static final int RIGHT = 3;
    public static final int ERR = -1;


    public static final int KEYBOARD_T9 = 0;
    public static final int KEYBOARD_EN = 1;
    public static final int KEYBOARD_NUM = 2;
    public static final int KEYBOARD_QP = 3;
    public static final int KEYBOARD_SYM = 4;
//	public static final int KEYBOARD_NULL = 5;


    public static int keyboardRestTimeCount = 0;

    public static int currentKeyboard;
    public static int currentSkinType;


    public static float mCurrentAlpha = 1f;
    public static float keyboardViewBackgroundAlpha = 0f;
    public static boolean slideDeleteSwitch = true;
    public static boolean shadowSwitch = true;
    public static int shadowRadius = 5;

    public static CharSequence redoTextForDeleteAll = "";
    public static String redoTextForDeleteAll_preedit = "";
    public static Stack<InputAction> redoText_single = new Stack<>();
    public static int redo_MAX_NUM = 60;
    public static String FilePath = "/data/data/com.hit.wi.ve/dict_qk/";

    public static String QUANPIN = "qp";
    public static String EMOJI = "emoji";
    public static String SYMBOL = "s";

    public static String XIAOMI = "Xiaomi";
    public static String PERMISSION_TAG = "PERMISSION_TAG";
    public static String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    public static String V6 = "V6";
    public static String V5 = "V5";
    public static String V7 = "V7";
    public static String PACKAGE = "package:com.hit.wi.ve";

    public static int metaRefreshTime = 1000;
    public static boolean inLarge = false;

    public static boolean isInView(View v, MotionEvent event) {
        return event.getX() > 0 && event.getY() > 0 && event.getX() < v.getWidth() && event.getY() < v.getHeight();
    }

    public static float textsizeFactor = 1;

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    public static void refreshState(SoftKeyboard softKeyboard8){
        softKeyboard8.refreshDisplay();
    }

    public static void addToRedo(CharSequence redoText){
        redoText_single.push(new InputAction(redoText, InputAction.TEXT_TO_KERNEL));
        if(redoText_single.size()>redo_MAX_NUM)redoText_single.remove(redoText_single.size()-1);
    }

    public static int getCurrentAlpha(){
        return (int) (mCurrentAlpha*255);
    }

}