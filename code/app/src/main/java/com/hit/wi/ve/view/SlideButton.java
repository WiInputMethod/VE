package com.hit.wi.ve.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.view.*;
import android.widget.Toast;

/**
 * Created by Administrator on 2015/4/26.
 */
public class SlideButton extends mybutton {
    Context context;
    Toast toast;
    PopupView popupView;
    Point point;
    WindowManager mWindowManager;
    private WindowManager.LayoutParams windowManagerParams = new WindowManager.LayoutParams();
    boolean popupadded;

    public SlideButton(Context context) {
        super(context);
        this.context = context;
        popupView = new PopupView(context, "music is here!");

        mWindowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        windowManagerParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        windowManagerParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        windowManagerParams.format = PixelFormat.RGBA_8888;
        windowManagerParams.gravity = Gravity.LEFT | Gravity.TOP;
        popupadded = false;
    }

    @Override
    public void onTouchDownEvent(View v, MotionEvent e) {
        super.onTouchDownEvent(v, e);
        setBackgroundColor(Color.GRAY);

    }

    @Override
    public void TouchPreHandle(View v, MotionEvent e) {
//        if(lastTouchDirection!=currentTouchDirection && lastTouchDirection!= DIRECTION_BTN)
        if (directionPopupUnAble(currentTouchDirection) || !directionPopupUnAble(lastTouchDirection))
            try {
                if (popupadded == true) {
                    mWindowManager.removeView(popupView);
                    popupadded = false;
                }
            } catch (Exception ex) {
                ex.toString();
            }
    }

    @Override
    public void TouchCompletePreHandle() {
        super.TouchCompletePreHandle();
    }

    @Override
    public void onUpSlideEvent(View vw, MotionEvent me) {
        try {

            windowManagerParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            windowManagerParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            windowManagerParams.x = (int) me.getRawX() - 100;
            windowManagerParams.y = (int) me.getRawY() - 200;
            if (windowManagerParams.x < 0) windowManagerParams.x = 0;
            if (windowManagerParams.y < 0) windowManagerParams.y = 0;
            if (!popupadded) {
                popupView.setBackgroundColor(Color.GREEN);
                mWindowManager.addView(popupView, windowManagerParams);
                popupadded = true;
            } else {
                mWindowManager.updateViewLayout(popupView, windowManagerParams);
            }

        } catch (Exception e) {
            e.toString();
            if (popupadded) {
                mWindowManager.removeView(popupView);
                popupadded = false;
            }
        }
    }

    @Override
    public void onRightSlideEvent(View vw, MotionEvent me) {
        try {

            windowManagerParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            windowManagerParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            windowManagerParams.x = (int) me.getRawX() - 100;
            windowManagerParams.y = (int) me.getRawY() - 200;
            if (windowManagerParams.x < 0) windowManagerParams.x = 0;
            if (windowManagerParams.y < 0) windowManagerParams.y = 0;
            if (!popupadded) {
                popupView.setBackgroundColor(Color.GREEN);
                mWindowManager.addView(popupView, windowManagerParams);
                popupadded = true;
            } else {
                mWindowManager.updateViewLayout(popupView, windowManagerParams);
            }

        } catch (Exception e) {
            e.toString();
            if (popupadded) {
                mWindowManager.removeView(popupView);
                popupadded = false;
            }
        }
    }

    @Override
    public void onUpSlideCompleteEvent(View vw, MotionEvent me) {
        try {
            if (popupadded = true) mWindowManager.removeView(popupView);
            popupadded = false;
        } catch (Exception e) {
            e.toString();
        }
    }

    @Override
    public void onRightSlideCompleteEvent(View v, MotionEvent me) {
        try {
            if (popupadded = true) mWindowManager.removeView(popupView);
            popupadded = false;
        } catch (Exception e) {
            e.toString();
        }
    }

    @Override
    public void onClickEvent() {
        try {
            toast = Toast.makeText(context, getText(), Toast.LENGTH_SHORT);
            toast.show();
        } catch (Exception e) {
            e.toString();
        }
    }

    @Override
    public void onDownSlideCompleteEvent(View v, MotionEvent e) {
        try {

            windowManagerParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            windowManagerParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            windowManagerParams.x = (int) e.getRawX() - 100;
            windowManagerParams.y = (int) e.getRawY() - 200;
            if (windowManagerParams.x < 0) windowManagerParams.x = 0;
            if (windowManagerParams.y < 0) windowManagerParams.y = 0;
            if (!popupadded) {
                popupView.setBackgroundColor(Color.GREEN);
                popupView.setText(String.valueOf(e.getRawX()) + "!" + String.valueOf(e.getRawY()));
                mWindowManager.addView(popupView, windowManagerParams);
                popupadded = true;
            } else {
                mWindowManager.updateViewLayout(popupView, windowManagerParams);
            }

        } catch (Exception ex) {
            ex.toString();
            if (popupadded) {
                mWindowManager.removeView(popupView);
                popupadded = false;
            }
        }
    }

    public void setPopUpText(CharSequence text) {
        popupView.setText(text);
    }

    private boolean directionPopupUnAble(int temp) {
        return temp == DIRECTION_BLANK || temp == DIRECTION_BTN;
    }
}
