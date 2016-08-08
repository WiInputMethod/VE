package com.hit.wi.t9.viewGroups;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.preference.PreferenceManager;
import android.view.*;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hit.wi.t9.Interfaces.ViewGroupInterface;
import com.hit.wi.t9.R;
import com.hit.wi.t9.settings.WIT9Activity;
import com.hit.wi.t9.values.Global;
import com.hit.wi.t9.values.SkinInfoManager;
import com.hit.wi.t9.view.QuickButton;
import com.hit.wi.t9.view.SetKeyboardSizeView;
import com.hit.wi.t9.widget.VerticalSeekBar;


/**
 * 上侧功能键
 * Created by Administrator on 2015/7/22.
 */
public class FunctionViewGroup extends NonScrollViewGroup implements ViewGroupInterface {
    int[] hideAnima = {R.anim.func_key_1_out, R.anim.func_key_2_out, R.anim.func_key_3_out, R.anim.func_key_4_out, R.anim.func_key_5_out};
    int[] showAnima = {R.anim.func_key_1_in, R.anim.func_key_2_in, R.anim.func_key_3_in, R.anim.func_key_4_in, R.anim.func_key_5_in};
    Point lastTouchPoint = new Point();


    SkinInfoManager skinInfoManager = SkinInfoManager.getSkinInfoManagerInstance();
    private VerticalSeekBar mVerticalSeekBar;
    private ImageView mSeekbarBackImage;
    private RelativeLayout alphaSettingLayout;
    private WindowManager.LayoutParams params_seekBarLayout;
    private android.widget.RelativeLayout.LayoutParams params_seekBar;
    private float mLastAlpha = 1f;

    public void create(Context context) {
        super.create(context);
        viewGroupWrapper.setOrientation(LinearLayout.HORIZONTAL);
        String[] keyText = res.getStringArray(R.array.FUNC_KEY_TEXT);

        for (String text : keyText) {
            QuickButton button = addButtonF(text);
            buttonList.add(button);
        }
        int point = 0;
        buttonList.get(point++).setOnTouchListener(moveOnTouchListener);
        buttonList.get(point++).setOnTouchListener(resizeOnTouchListener);
        buttonList.get(point++).setOnTouchListener(settingOnTouchListener);
        buttonList.get(point++).setOnTouchListener(alphaOnTouchListener);
        buttonList.get(point++).setOnTouchListener(dismissOnTouchListener);
    }

    public void updatesize(int keyboardWidth, int height) {
        setSize(keyboardWidth, height);
        updateViewLayout();
    }

    public void updatePosition(int x, int y) {
        setPosition(x, y);
        updateViewLayout();
    }

    public void setButtonPadding(int padding) {
        for (QuickButton button : buttonList) {
            ((LinearLayout.LayoutParams) button.itsLayoutParams).leftMargin = padding;
        }
    }

    public void refreshState(Boolean show) {
        if (!show) hide();
        else show();
    }

    public void startHideAnimation() {
        super.startAnimation(hideAnima);
    }

//    @Override
//    public void clearAnimation() {
//        super.clearAnimation();
//    }

    public void startShowAnimation() {
        super.startAnimation(showAnima);
    }

    private QuickButton addButtonF(String text) {
        QuickButton button = super.addButton(text, skinInfoManager.skinData.textcolor_functionKeys, skinInfoManager.skinData.backcolor_functionKeys);

        button.setBackgroundResource(R.drawable.middle_button);
        button.setId(Global.generateViewId());
        button.itsLayoutParams = new LinearLayout.LayoutParams(0, 0);
        viewGroupWrapper.addView(button, button.itsLayoutParams);
        return button;
    }

    /**
     * author winlandiano
     */
    public void addSetAlphaView(View view, MotionEvent event) {
        int x, y;
        alphaSettingLayout = new RelativeLayout(context);
        params_seekBarLayout = new WindowManager.LayoutParams(10, 100, WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
        params_seekBarLayout.width = 70;
        params_seekBarLayout.height = 300;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display dis = wm.getDefaultDisplay();
        int mScreenWidth = dis.getWidth();
        int mScreenHeight = dis.getHeight();

        x = (int) (event.getRawX() - mScreenWidth / 2);
        if (x < (int) (-mScreenWidth * 0.5)) {
            x = (int) (-0.5 * mScreenWidth);
        }
        if (x > (int) (mScreenWidth * 0.5 - 35)) {
            x = (int) (mScreenWidth * 0.5 - 35);
        }
        params_seekBarLayout.x = x;

        y = (int) (event.getRawY() - mScreenHeight / 2);
        if (y < (int) (-mScreenHeight * 0.5)) {
            y = (int) (-mScreenHeight * 0.5);
        }
        if (y > mScreenHeight / 2 - 150) {
            y = mScreenHeight / 2 - 150;
        }
        params_seekBarLayout.y = y;
        params_seekBarLayout.gravity = Gravity.CENTER;

        mSeekbarBackImage = new ImageView(context);
        mSeekbarBackImage.setImageResource(R.drawable.seekbar_back);
        mSeekbarBackImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        alphaSettingLayout.addView(mSeekbarBackImage);

        wm.addView(alphaSettingLayout, params_seekBarLayout);
        mVerticalSeekBar = new VerticalSeekBar(context);
        mVerticalSeekBar.setThumb(res.getDrawable(R.drawable.blank));
        mVerticalSeekBar.setProgressDrawable(res.getDrawable(R.drawable.progress_bar));

        params_seekBar = new RelativeLayout.LayoutParams(10, 250);
        params_seekBar.topMargin = 30;
        params_seekBar.leftMargin = 30;
        alphaSettingLayout.addView(mVerticalSeekBar, params_seekBar);
        mVerticalSeekBar.startAnimation(AnimationUtils.loadAnimation(context, R.anim.popup));
        mSeekbarBackImage.startAnimation(AnimationUtils.loadAnimation(context, R.anim.popup));


    }

    private int getCurrentProgress(float currentY, float bottomY, int height, int maxProgress) {
        return Math.max(Math.min((int) ((bottomY - currentY) / height * maxProgress), maxProgress), 0);
    }

    public void removeSetAlphaView() {
        mSeekbarBackImage.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fadeoff));
        mVerticalSeekBar.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fadeoff));
        alphaSettingLayout.destroyDrawingCache();
        ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE)).
                removeView(alphaSettingLayout);
//        mHandler.sendEmptyMessageDelayed(SET_ALPHA_VIEW_DESTROY, 210);
    }

    private void onTouchEffect(View view, int action) {
        softKeyboard.transparencyHandle.handleAlpha(action);
        softKeyboard.keyboardTouchEffect.onTouchEffectWithAnim(view, action,
                skinInfoManager.skinData.backcolor_touchdown,
                skinInfoManager.skinData.backcolor_functionKeys
        );
    }

    private View.OnTouchListener resizeOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            onTouchEffect(view, motionEvent.getAction());
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastTouchPoint.x = (int) motionEvent.getRawX();
                    lastTouchPoint.y = (int) motionEvent.getRawY();
                    softKeyboard.viewManagerC.addSetKeyboardSizeView(SetKeyboardSizeView.SettingType.QuickSetMode);
                    break;
                case MotionEvent.ACTION_MOVE:
                    softKeyboard.mSetKeyboardSizeView.UpdateSize(
                            motionEvent.getRawX() - lastTouchPoint.x,
                            motionEvent.getRawY() - lastTouchPoint.y
                    );
//                    softKeyboard.mSetKeyboardSizeView.requestUpdateSize();
//                    lastTouchPoint.x = (int) motionEvent.getRawX();
//                    lastTouchPoint.y = (int) motionEvent.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    if (Global.isInView(view, motionEvent)) {
                        softKeyboard.mSetKeyboardSizeParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                        wm.updateViewLayout(softKeyboard.mSetKeyboardSizeView, softKeyboard.mSetKeyboardSizeParams);
                        softKeyboard.mSetKeyboardSizeView.SetSettingType(SetKeyboardSizeView.SettingType.FullSetMode);
                        softKeyboard.mSetKeyboardSizeView.invalidate();
                    } else {
                        softKeyboard.mSetKeyboardSizeView.requestUpdateSize();
                        softKeyboard.mOnSizeChangeListener.onFinishSetting();
                        softKeyboard.updateWindowManager();
                    }
                    break;
            }
            return false;
        }
    };

    private View.OnTouchListener moveOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            onTouchEffect(view, motionEvent.getAction());
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastTouchPoint.x = (int) motionEvent.getRawX();
                    lastTouchPoint.y = (int) motionEvent.getRawY();
                    softKeyboard.updateSetKeyboardSizeViewPos();
                    break;
                case MotionEvent.ACTION_MOVE:
                    softKeyboard.mSetKeyboardSizeView.UpdatePos(
                            motionEvent.getRawX() - lastTouchPoint.x,
                            motionEvent.getRawY() - lastTouchPoint.y
                    );
                    softKeyboard.mSetKeyboardSizeView.requestUpdatePos();
                    break;
                case MotionEvent.ACTION_UP:
                    softKeyboard.screenInfoC.WriteKeyboardSizeInfoToSharedPreference();
                    break;
            }

            return false;
        }
    };

    private float inputViewBackgroundAlpha = 0;
    private float lastTouchX;
    private float lastTouchY;
    private View.OnTouchListener settingOnTouchListener = new View.OnTouchListener() {
        @SuppressLint("NewApi")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            onTouchEffect(view, motionEvent.getAction());
            if (motionEvent.getAction() == MotionEvent.ACTION_UP && Global.isInView(view, motionEvent)) {
                final Intent intent = new Intent(context, WIT9Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE && !Global.isInView(view, motionEvent)) {
                if (motionEvent.getY() < 0 || motionEvent.getY() > view.getHeight()) {
                    float textsizeFactorDelta = (lastTouchY - motionEvent.getY()) / (2 * softKeyboard.keyboardParams.height);
                    lastTouchY = motionEvent.getY();
                    Global.textsizeFactor += textsizeFactorDelta;
                    Global.textsizeFactor = Math.max(0f, Math.min(Global.textsizeFactor, 2f));
                } else {
                    float inputViewBackgroundAlphaDelta = (motionEvent.getX() - lastTouchX) / softKeyboard.keyboardParams.width;
                    lastTouchX = motionEvent.getX();
                    inputViewBackgroundAlpha += inputViewBackgroundAlphaDelta;
                    Global.keyboardViewBackgroundAlpha = Math.max(0f, Math.min(inputViewBackgroundAlpha, 1f));
                    if (softKeyboard.keyboardLayout.getBackground() != null)
                        softKeyboard.keyboardLayout.getBackground().setAlpha((int) (Global.keyboardViewBackgroundAlpha * 255));
                }
            } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                lastTouchX = 0;
                lastTouchY = 0;
            }
            return false;
        }
    };

    private boolean isSeekBarShown;
    private View.OnTouchListener alphaOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            onTouchEffect(view, motionEvent.getAction());
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastTouchPoint.y = (int) motionEvent.getRawY();
                    isSeekBarShown = true;
                    mLastAlpha = Global.mCurrentAlpha;
                    addSetAlphaView(view, motionEvent);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (isSeekBarShown) {
                        int tmp = getCurrentProgress(motionEvent.getRawY(),
                                lastTouchPoint.y + mVerticalSeekBar.getHeight() / 2,
                                mVerticalSeekBar.getHeight(),
                                mVerticalSeekBar.getMax());
                        mVerticalSeekBar.setProgress(tmp);
                        Global.mCurrentAlpha = ((float) tmp - (float) mVerticalSeekBar.getMax() / 2) / ((float) mVerticalSeekBar.getMax()) + mLastAlpha;
                        Global.mCurrentAlpha = Math.max(0f, Math.min(1f, Global.mCurrentAlpha));
                        softKeyboard.transparencyHandle.setKeyBoardAlpha(Global.getCurrentAlpha());
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    removeSetAlphaView();
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putFloat("CURRENT_ALPHA", Global.mCurrentAlpha);
                    editor.commit();
                    isSeekBarShown = false;
                    break;
            }
            return false;
        }
    };

    private View.OnTouchListener dismissOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            onTouchEffect(view, motionEvent.getAction());
            if (motionEvent.getAction() == MotionEvent.ACTION_UP && Global.isInView(view, motionEvent)) {
                softKeyboard.requestHideSelf(0);
            }
            return false;
        }
    };

    @Override
    public void updateSkin() {
        setTextColor(skinInfoManager.skinData.textcolor_functionKeys);
        setBackgroundColor(skinInfoManager.skinData.backcolor_functionKeys);
        setBackgroundAlpha(Global.getCurrentAlpha());
    }

    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
    }
}
