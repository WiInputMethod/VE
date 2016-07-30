package com.hit.wi.ve.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.Button;

import com.hit.wi.t9.R;

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
        this.setBackgroundResource(R.drawable.middle_button);
    }

}
