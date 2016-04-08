
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
//	public static HashMap<String , Integer> keyboardmap = new HashMap<String, Integer>();
//	static{
//	}

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

    public final static int max(int... nums) {
        int maxint;
        maxint = nums[0];
        for (int i = 1; i < nums.length; i++) {
            if (maxint < nums[i]) {
                maxint = nums[i];
            }
        }
        return maxint;
    }

    public final static int min(int... nums) {
        int minint;
        minint = nums[0];
        for (int i = 1; i < nums.length; i++) {
            if (minint > nums[i]) {
                minint = nums[i];
            }
        }
        return minint;
    }

    public static boolean isInView(View v, MotionEvent event) {
        return event.getX() > 0 && event.getY() > 0 && event.getX() < v.getWidth() && event.getY() < v.getHeight();
    }

    public static int minButtonAlpha = 40;
    public static int upLimitButtonAlpha = 230;

    public static float textsizeFactor = 1;

    public static int computeButtonAlpha(int buttonAlpha) {
        if (buttonAlpha + minButtonAlpha > upLimitButtonAlpha) return upLimitButtonAlpha;
        else return buttonAlpha + minButtonAlpha;
    }

    public static List<String> convertStringtoList(String[] strings) {
        List<String> arraylist = new ArrayList<String>();

        for (String string : strings) {
            arraylist.add(string);
        }
        if (arraylist.size() == 1 && arraylist.get(0) == "")
            arraylist = Collections.EMPTY_LIST;
        return arraylist;
    }


    public static String[] convertListToString(List<String> list) {
        String strings[] = list.toArray(new String[list.size()]);
        return strings;
    }

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

    public static void clearKernel(){
        if(WIInputMethod.GetWordsNumber()>0)WIInputMethod.CLeanKernel();
        if(WIInputMethodNK.GetWordsNumber()>0)WIInputMethodNK.CLeanKernel();
    }

    public static void refreshState(SoftKeyboard softKeyboard8){
        softKeyboard8.refreshDisplay();
    }

    public static String halfToFull(String halfCharacterString) {
        if (null == halfCharacterString) {
            return "";
        }
        char[] c = halfCharacterString.toCharArray();
        for (int i = 0; i < c.length; i++) {
            //判断是否是半角空格
            if (c[i] == 32) {
                c[i] = (char) 12288;
            } else if (c[i] > 32 && c[i] < 127) {
                // 对于其他半角字符，加65248转换为全角字符
                c[i] = (char) (c[i] + 65248);
            }
        }
        return new String(c);
    }

    public static String fullToHalf(String fullCharacterString) {
        if (null == fullCharacterString) {
            return "";
        }
        char[] c = fullCharacterString.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
            } else if (c[i] > 65280 && c[i] < 65375) {
                c[i] = (char) (c[i] - 65248);
            }
        }
        return new String(c);
    }

    public static int computePosition(float x, float y, int height, int width){
        if(x<0 && x<y && x+y<height){
            return LEFT;
        } else if(x>width && x+y>width && x>y-height+width) {
            return RIGHT;
        } else if(y<0){
            return UP;
        } else if(y>height){
            return DOWM;
        } else {
            return ERR;
        }
    }

    public static boolean isQK(int keyboard){
        return keyboard == Global.KEYBOARD_EN || keyboard==Global.KEYBOARD_QP;
    }

    public static void addToRedo(CharSequence redoText){
        redoText_single.push(new InputAction(redoText, InputAction.TEXT_TO_KERNEL));
        if(redoText_single.size()>redo_MAX_NUM)redoText_single.remove(redoText_single.size()-1);
    }
}