package com.hit.wi.ve.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Administrator on 2015/5/12.
 */
public class QuickButton extends Button {
    private Context context;
    public ValueAnimator itsAnimator;

    public ViewGroup.LayoutParams itsLayoutParams;

    public QuickButton(Context context) {
        super(context);
        this.context = context;
    }

}
