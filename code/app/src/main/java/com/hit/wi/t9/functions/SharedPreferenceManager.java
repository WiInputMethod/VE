package com.hit.wi.t9.functions;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import com.hit.wi.t9.R;

/**
 * 公用的静态SharedPreferenceManager，功能包括初始化和获取defaultSharedPreferences, editor等
 *
 * @author winlandiano
 */
public class SharedPreferenceManager {
    private static SharedPreferences sharedPreferences;
    private static Editor editor;

    private static SlideContentManager slideContentManager = new SlideContentManager();

    private SharedPreferenceManager() {
    }

    /**
     * 第一次安装输入法后的一些初始化数据放入defaultSharedPreferences里面。仅执行一次!
     *
     * @param context Activity的context
     */
    static public void initSharedPreferencesData(Context context) {
        getDefaultSharedPreferences(context);
        if (!sharedPreferences.getBoolean("IS_SHAREDPREFERENCE_INIT", false)) {
            getEditor(context);
            slideContentManager.loadRecommendSlideMode(R.array.RECOMMEND_SLIDE_TEXT0, context, editor);
            editor.putBoolean("IS_SHAREDPREFERENCE_INIT", true);
            editor.commit();
        }
    }

    /**
     * 返回唯一defaultSharedPreferences
     *
     * @param context
     * @return SharedPreferences
     */
    static public SharedPreferences getDefaultSharedPreferences(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return sharedPreferences;
    }

    static public Editor getEditor(Context context) {
        if (editor == null) {
            if (sharedPreferences == null) {
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            }
            editor = sharedPreferences.edit();
        }

        return editor;
    }
}
