package com.hit.wi.t9.functions;

import android.content.Context;
import android.content.SharedPreferences;
import com.hit.wi.t9.R;

/**
 * Created by Administrator on 2015/4/1.
 */
public class SlideContentManager {

    /**
     * 加载系统推荐的点滑设置
     * @param type
     * @param context
     * @param editor
     */
    public final void loadRecommendSlideMode(int type, Context context, SharedPreferences.Editor editor) {
        if (type != R.array.RECOMMEND_SLIDE_TEXT0 &&
                type != R.array.RECOMMEND_SLIDE_TEXT1 &&
                type != R.array.RECOMMEND_SLIDE_TEXT2 &&
                type != R.array.RECOMMEND_SLIDE_TEXT3)
            return;
        String[] recommendModeStr = context.getResources().getStringArray(type);
        String tag;
        for (int i = 0; i < 26; i++) {
            tag = "SLIDE_PIN_" + (char) ('A' + i);
            editor.putString(tag, recommendModeStr[i]).commit();
        }
    }

}
