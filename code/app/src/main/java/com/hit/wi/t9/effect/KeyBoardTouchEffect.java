package com.hit.wi.t9.effect;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.hit.wi.util.ViewsUtil;
import com.hit.wi.t9.R;
import com.hit.wi.t9.values.Global;

/**
 * Created by Administrator on 2015/5/22.
 */
public class KeyBoardTouchEffect implements KeyboardTouchEffectInterface {
    /**
     * 键盘震动
     */
    VibrateManager vibrateManager;
    /**
     * 键盘声音
     */
    SoundManager soundManager;

    Animation downAnim;
    Animation upAnim;

    public KeyBoardTouchEffect(Context context) {
        vibrateManager = new VibrateManager(context);
        soundManager = new SoundManager(context);
        downAnim = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.touch_down);
        upAnim = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.touch_up);

    }

    public void loadSetting(SharedPreferences sharedPreferences) {
        vibrateManager.setStrength(sharedPreferences.getInt("VIBRATOR", 0));
        soundManager.setVolume(sharedPreferences.getInt("SOUND", 0));
        soundManager.setSoundType(Integer.parseInt(sharedPreferences.getString("KEY_SOUND_EFFECT_SELECTOR", "1")));
        soundManager.setSlideVolume(sharedPreferences.getInt("SLIDE_PIN_VOLUME", 0));
    }

    public final void onPressEffect() {
        vibrateManager.Vibrate();
        soundManager.playSound();
    }

    public final void onSlideEffect() {
        soundManager.playSwipeSound();
    }

    public void onTouchEffect(View v, int action, int touchdown_color, int normal_color) {
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                onPressEffect();
                ViewsUtil.setBackgroundWithGradientDrawable(v,touchdown_color);
                break;
            case MotionEvent.ACTION_MOVE:
                onSlideEffect();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                ViewsUtil.setBackgroundWithGradientDrawable(v,normal_color);
                break;
        }
        v.getBackground().setAlpha(Global.getCurrentAlpha());
    }

    public void animEffect(View v,int action){
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                v.startAnimation(downAnim);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                v.startAnimation(upAnim);
                break;
        }
    }

    public void onTouchEffectWithAnim(View v, int action, int touchdown_color, int normal_color) {
        animEffect(v,action);
        onTouchEffect(v, action, touchdown_color, normal_color);
    }

    public void onTouchEffectWithAnimForQK(View v, int action, int touchdown_color, int normal_color){
        animEffect(v,action);
        onTouchEffect(v.findViewById(R.id.main_text),action,touchdown_color,normal_color);
        onTouchEffect(v.findViewById(R.id.predict_text),action,touchdown_color,normal_color);
    }
}
