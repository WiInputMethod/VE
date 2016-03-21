package com.hit.wi.t9.view;

import android.content.Context;
import android.graphics.Point;
import android.widget.*;

/**
 * Created by Administrator on 2015/5/1.
 */
public class PopupView extends TextView {
    public PopupView(Context context) {
        super(context);
    }

    public PopupView(Context context, Point p) {
        super(context);
        this.setX(p.x);
        this.setY(p.y);
    }

    public PopupView(Context context, float x, float y, int width, int height) {
        super(context);
        this.setX(x);
        this.setY(y);
        this.setWidth(width);
        this.setHeight(height);
    }

    public PopupView(Context context, CharSequence sequence) {
        super(context);
        this.setText(sequence);
    }

    public void setConfigure(Point p, int width, int height) {
        setX(p.x);
        setY(p.y);
        setHeight(height);
        setWidth(width);
    }

    public void setConfigure(float x, float y, int width, int height) {
        setX(x);
        setY(y);
        setHeight(height);
        setWidth(width);
    }
}
