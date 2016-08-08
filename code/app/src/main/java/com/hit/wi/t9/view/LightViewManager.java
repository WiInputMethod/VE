package com.hit.wi.t9.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.TextView;

import com.hit.wi.util.ViewsUtil;
import com.hit.wi.util.StringUtil;

import com.hit.wi.t9.R;
import com.hit.wi.t9.SoftKeyboard;
import com.hit.wi.t9.values.Global;


/**
 * Created by admin on 2016/3/2.
 */
public class LightViewManager {
    /**
     * 点滑光线效果的个数
     */
    private final int mLightNum = 4;

    private Context context;
    private SoftKeyboard softKeyboard;
    /**
     * 点滑光线的界面
     */
    public TextView[] mLightView;

    /**
     * 点滑滑出去的光线界面
     */
    public TextView[] mOutLightView;

    public ViewGroup parentViewGroup;

    public AbsoluteLayout viewsWraper;
    public WindowManager.LayoutParams viewsParams;

    Resources res;

    /**
     * 点滑光线的背景
     */
    int[] mLightBackground = {
            R.drawable.light_top,
            R.drawable.light_bot,
            R.drawable.light_left,
            R.drawable.light_right,
    };

    /**
     * 点滑出去的光线背景
     */
    int[] mOutLightBackground = {
            R.drawable.light_top,
            R.drawable.light_bot,
            R.drawable.light_left,
            R.drawable.light_right,
    };

    int[] offAnim = {
            R.anim.light_bot_off,
            R.anim.light_top_off,
            R.anim.light_right_off,
            R.anim.light_left_off,
    };

    int[] offAnim_noraml_hide = {
            R.anim.light_top_off,
            R.anim.light_bot_off,
            R.anim.light_left_off,
            R.anim.light_right_off,
    };

    int[] outAnim = {
            R.anim.light_top_out,
            R.anim.light_bot_out,
            R.anim.light_left_out,
            R.anim.light_right_out,
    };

    private AbsoluteLayout.LayoutParams[] mLightParams;

    public void create(Context context){
        mLightView = new TextView[mLightNum];
        mOutLightView = new TextView[mLightNum];
        mLightParams = new AbsoluteLayout.LayoutParams[mLightNum];
        this.context = context;
        viewsWraper = new AbsoluteLayout(context);
        res = context.getResources();
        for (int i = 0; i < mLightNum; ++i) {
            mLightView[i] = addButton(mLightBackground[i]);
            mOutLightView[i] = addButton(mOutLightBackground[i]);
            mLightParams[i] = new AbsoluteLayout.LayoutParams(0,0,0,0);
            viewsWraper.addView(mLightView[i],mLightParams[i]);
            viewsWraper.addView(mOutLightView[i],mLightParams[i]);
        }
        addToWindow();
    }

    public void addToWindow(){
        if (!viewsWraper.isShown()){
            WindowManager wm  = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
            viewsParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    1
                    );
            viewsParams.gravity = Gravity.TOP | Gravity.LEFT;
            try {
                wm.addView(viewsWraper,viewsParams);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void removeView() {
        if (viewsWraper != null && viewsWraper.isShown()){
            WindowManager wm = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
            wm.removeView(viewsWraper);
        }
    }

    public TextView addButton(int resID){
        TextView light = new TextView(context);
        light.setTextColor(Color.BLACK);
        light.setGravity(Gravity.CENTER);
        light.getPaint().setFakeBoldText(true);
        light.setBackgroundResource(resID);
        light.setVisibility(View.GONE);
        return light;
    }

    public void setTypeface(Typeface typeface){
        for (TextView view:mLightView){
            view.setTypeface(typeface);
        }
        for (TextView view:mOutLightView){
            view.setTypeface(typeface);
        }
    }

    public void setSoftkeyboard(SoftKeyboard softkeyboard){
        this.softKeyboard = softkeyboard;
    }

    private void animate(final View v, int anim, int visible) {
        if(visible == View.VISIBLE){
            v.clearAnimation();
            v.setVisibility(View.VISIBLE);
            v.startAnimation(AnimationUtils.loadAnimation(context, anim));
        } else {
            v.clearAnimation();
            Animation animtion = AnimationUtils.loadAnimation(context, anim);
            animtion.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    v.clearAnimation();
                    v.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            v.startAnimation(animtion);
        }
    }

    public int lightViewAnimate(View v, MotionEvent event){
        int index = ViewsUtil.computeDirection(event.getX(), event.getY(), v.getHeight(), v.getWidth());
        if (index == Global.ERR){
            for (int i = 0;i <mLightNum ;i++){
                if(mLightView[i].isShown())animate(mLightView[i],offAnim_noraml_hide[i],View.GONE);
            }
        } else if (mLightView[index].isShown()) {
            animate(mLightView[index], offAnim[index], View.GONE);
//            mOutLightView[index].setVisibility(View.VISIBLE);
//            animate(mOutLightView[index], outAnim[index], View.GONE);
        }
        return index;
    }

    public void updateViewPosAndSize(int keyboardWidth,int inputHeight,int posX,int posY){
        if(viewsWraper==null || !viewsWraper.isShown())return;
        WindowManager wm = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        viewsParams.x = posX;
        viewsParams.y = posY;
        viewsParams.width = keyboardWidth;
        viewsParams.height = inputHeight;

        wm.updateViewLayout(viewsWraper,viewsParams);
    }

    public void setSize(int keyboardWidth,int inputHeight,int posX,int posY){
        if (viewsWraper==null) return;
        updateViewPosAndSize(keyboardWidth,inputHeight,posX,posY);

        int[] lightX = res.getIntArray(R.array.LIGHT_POS_X);
        int[] lightY = res.getIntArray(R.array.LIGHT_POS_Y);
        int[] lightWidth = res.getIntArray(R.array.LIGHT_WIDTH);
        int[] lightHeight = res.getIntArray(R.array.LIGHT_HEIGHT);
        for (int i = 0; i < mLightNum; ++i) {
            mLightParams[i].x = lightX[i] * keyboardWidth / 100;
            mLightParams[i].y = lightY[i] * inputHeight / 100;
            mLightParams[i].width = lightWidth[i] * keyboardWidth / 100;
            mLightParams[i].height = lightHeight[i] * inputHeight / 100;
            mLightView[i].getPaint().setTextSize(3 * Math.min(mLightParams[i].width ,mLightParams[i].height)/5);
            viewsWraper.updateViewLayout(mLightView[i], mLightParams[i]);
            viewsWraper.updateViewLayout(mOutLightView[i], mLightParams[i]);
        }
    }

    public boolean HideLightView(float x, float y, float width, float height) {
        int index = ViewsUtil.computeDirection(x, y, (int) height, (int) width);
        for (int i =0;i<mLightNum;i++){
            if (i == index )continue;
            if(mLightView[i].isShown())
                animate(mLightView[i],offAnim_noraml_hide[i],View.GONE);
        }
        viewsWraper.setVisibility(View.GONE);
        return true;
    }

    /**
     * 隐藏光线弹片
     */
    public void invisibleLightView() {
        for (int i = 0; i < mLightNum; ++i) {
            mLightView[i].clearAnimation();
            mOutLightView[i].clearAnimation();
            mLightView[i].setVisibility(View.GONE);
            mOutLightView[i].setVisibility(View.GONE);
        }
    }

    int[] showAnim = {
        R.anim.light_top,
        R.anim.light_bot,
        R.anim.light_left,
        R.anim.light_right,
    };

    public boolean ShowLightView(float x, float y, float width, float height, String text) {
        boolean mLightShow = false;
        viewsWraper.setVisibility(View.VISIBLE);
        for (int i = 0; i < 4; ++i) {
            mLightShow |= mLightView[i].isShown();
        }
        int index = ViewsUtil.computeDirection(x, y, (int) height, (int) width);
        int[] follow = {1,3,0,2};
        if (index!= -1 && !mLightShow) {
            animate(mLightView[index],showAnim[index],View.VISIBLE);
            String showText = " ";
            if(StringUtil.isAllLetter(text)){
                if(text.length() > follow[index])
                    showText = (String) text.subSequence(follow[index], follow[index]+1);
            } else {
                showText = text;
            }
            mLightView[index].setText(showText);
        }

        return true;
    }

}
