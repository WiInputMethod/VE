package com.hit.wi.t9.view;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by purebluesong on 2016/5/25.
 */
public class KeyContainerLinearLayout extends LinearLayout {
    public final float LOCATE_JUDGE_RATE = (float) 0.15;

    public KeyContainerLinearLayout(Context context) {
        super(context);
    }
    private float cursorFirstTouchX ;

    private boolean moved = false;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            cursorFirstTouchX = event.getX();
            moved = false;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE){
            if (event.getY() > 0 && Math.abs(event.getX() - cursorFirstTouchX) > getMeasuredWidth() * LOCATE_JUDGE_RATE){
                moved = true;
                return true;
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP && moved) {//useless
            return true;
        }
        return false;
    }


}
