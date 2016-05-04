package com.hit.wi.ve.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.hit.wi.util.ViewsUtil;

/**
 * Created by Administrator on 2015/4/26.
 */
public class mybutton extends Button {
    protected final int DIRECTION_BLANK = 0;
    protected final int DIRECTION_LEFT = 1;
    protected final int DIRECTION_DOWN = 2;
    protected final int DIRECTION_RIGHT = 3;
    protected final int DIRECTION_UP = 4;
    protected final int DIRECTION_BTN = 5;
    public OnTouchListener onTouchlistener;
    public int lastTouchDirection = -1;
    public int currentTouchDirection = -1;
    private boolean messagetrans = false;

    public mybutton(Context context) {
        super(context);
        onTouchlistener = new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return onTouch(v, event);
            }
        };
        this.setOnTouchListener(onTouchlistener);

    }

    public boolean onTouchM(View v, MotionEvent e) {
        int temp;
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onTouchDownEvent(v, e);
                break;
            case MotionEvent.ACTION_MOVE:
                temp = JudgeDirect(v, e);
                if (currentTouchDirection == -1) currentTouchDirection = temp;
                else {
                    if (currentTouchDirection != temp) {
                        lastTouchDirection = currentTouchDirection;
                        currentTouchDirection = temp;
                    }
                }
                TouchPreHandle(v, e);
                switch (currentTouchDirection) {
                    case DIRECTION_LEFT:
                        onLeftSlideEvent(v, e);
                        break;
                    case DIRECTION_DOWN:
                        onDownSlideEvent(v, e);
                        break;
                    case DIRECTION_RIGHT:
                        onRightSlideEvent(v, e);
                        break;
                    case DIRECTION_UP:
                        onUpSlideEvent(v, e);
                        break;
                    case DIRECTION_BTN:
                        onTouchButtonEvent(v, e);
                        break;
                    case DIRECTION_BLANK:
                        onTouchBlackEvent(v, e);
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
                TouchCompletePreHandle();
                switch (JudgeDirect(v, e)) {
                    case DIRECTION_BLANK:
                        onTouchFall(v, e);
                        break;
                    case DIRECTION_LEFT:
                        onLeftSlideCompleteEvent(v, e);
                        break;
                    case DIRECTION_DOWN:
                        onDownSlideCompleteEvent(v, e);
                        break;
                    case DIRECTION_RIGHT:
                        onRightSlideCompleteEvent(v, e);
                        break;
                    case DIRECTION_UP:
                        onUpSlideCompleteEvent(v, e);
                        break;
                    case DIRECTION_BTN:
                        onClickEvent();
                        return true;
                }
                break;
        }
        return messagetrans;
    }


    /**
     * @param e 滑动事件
     * @param v 视图
     * @return 滑动方向，0表示滑动选择无效，1表示向左滑动，2表示向下，3表示向右，4表示向上，5表示在原位
     */
    private int JudgeDirect(View v, MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        float width = v.getWidth();
        float height = v.getHeight();
        int direct = DIRECTION_BLANK;
        if (x < 0) {
            if (y > 0 && y < height) direct = DIRECTION_LEFT;
            else direct = DIRECTION_BLANK;
        } else {
            if (x > width) {
                if (y > 0 && y < height) direct = DIRECTION_RIGHT;
                else direct = DIRECTION_BLANK;
            } else {
                if (y > 0) {
                    if (y < height) {
                        direct = DIRECTION_BTN;
                    } else {
                        direct = DIRECTION_DOWN;
                    }
                } else {
                    direct = DIRECTION_UP;
                }
            }
        }

        return direct;
    }

    public void onClickEvent() {
    }

    public void onLeftSlideEvent(View vw, MotionEvent me) {
    }

    public void onDownSlideEvent(View vw, MotionEvent me) {
    }

    public void onRightSlideEvent(View vw, MotionEvent me) {
    }

    public void onUpSlideEvent(View vw, MotionEvent me) {
    }

    @SuppressLint("NewApi")
    public void onTouchDownEvent(View vw, MotionEvent me) {
        try {
            getParentForAccessibility().requestDisallowInterceptTouchEvent(true);
            Log.d("quick", "!");
            //   getParentForAccessibility().getParentForAccessibility().requestDisallowInterceptTouchEvent(true);
            Log.d("quick", "!");
        } catch (Exception ex) {
            Log.d("quick", ex.toString());
            ex.toString();
        }
    }

    public void onLeftSlideCompleteEvent(View vw, MotionEvent me) {
    }

    public void onDownSlideCompleteEvent(View vw, MotionEvent me) {
    }

    public void onRightSlideCompleteEvent(View vw, MotionEvent me) {
    }

    public void onUpSlideCompleteEvent(View vw, MotionEvent me) {
    }

    public void onTouchFall(View vw, MotionEvent me) {
    }

    public void onTouchButtonEvent(View v, MotionEvent e) {
    }

    public void onTouchBlackEvent(View v, MotionEvent e) {
    }

    public void TouchPreHandle(View v, MotionEvent e) {
    }

    public void TouchCompletePreHandle() {
        setBackgroundColor(Color.BLACK);
        ViewsUtil.setBackgroundWithGradientDrawable(this,Color.BLACK);
    }

    private void setMessagetrans(boolean value) {
        messagetrans = value;
    }
}
