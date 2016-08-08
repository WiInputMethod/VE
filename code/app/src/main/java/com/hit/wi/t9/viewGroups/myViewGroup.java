package com.hit.wi.t9.viewGroups;

import android.content.Context;
import android.graphics.Typeface;
import android.view.ViewGroup;
import android.view.animation.Animation;
import com.hit.wi.t9.values.SkinInfoManager;

/**
 * Created by Administrator on 2015/5/20.
 */
public abstract class myViewGroup {
    protected ViewGroup parentViewGroup;
    protected Context context;


    protected float textSize;
    protected int textColor;
    protected int backgroundColor;
    protected int backgroundAlpha;
    protected int backgroundResource;
    protected int buttonWidth;
    protected int buttonHeight;
    protected float buttonAlpha;
    protected SkinInfoManager skinInfoManager = SkinInfoManager.getSkinInfoManagerInstance();

    public myViewGroup() {
    }

    public abstract void addThisToView(ViewGroup viewGroup);

    public abstract void setTextSize(float size);

    public abstract void setTextColor(int color);

    public abstract void setSize(int width, int height);

    public abstract void setTypeface(Typeface typeface);

    public abstract void setBackgroundColor(int color);

    public abstract void setBackgroundAlpha(int alpha);

    public abstract void setBackgroundResource(int resource);

    public abstract void setButtonAlpha(float alpha);

    public abstract void setVisibility(int visibility);

    public abstract void clearAnimation();

    public abstract void startAnimation(Animation animation);

    public abstract boolean isShown();

    public abstract void hide();

    public abstract void show();

    public abstract void setButtonSize(int width, int height);

    public void create(Context context) {
        this.context = context;
    }

}
