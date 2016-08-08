package com.hit.wi.t9.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

public class NumKey extends ImageButton {

    public NumKey(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.imageButtonStyle);
        // TODO Auto-generated constructor stub
    }

    public NumKey(Context context) {
        this(context, null);
        // TODO Auto-generated constructor stub
    }

    Animation mShowAnim;
    Animation mHideAnim;

    public NumKey(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        int[] attr = {android.R.attr.windowEnterAnimation, android.R.attr.windowExitAnimation};
        TypedArray typedArray = context.obtainStyledAttributes(attrs, attr);
        int showAnimId = typedArray.getResourceId(0, -1);
        int hideAnimId = typedArray.getResourceId(1, -1);
        if (showAnimId != -1) {
            mShowAnim = AnimationUtils.loadAnimation(context, showAnimId);
        }
        if (hideAnimId != -1) {
            mHideAnim = AnimationUtils.loadAnimation(context, hideAnimId);
        }
        typedArray.recycle();
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        if (visibility == View.VISIBLE && mShowAnim != null) {
            this.startAnimation(mShowAnim);
        }
        if ((visibility == View.INVISIBLE || visibility == View.GONE) && mHideAnim != null) {
            this.startAnimation(mHideAnim);
        }
        super.onVisibilityChanged(changedView, visibility);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        return super.onTouchEvent(event);
    }

}
