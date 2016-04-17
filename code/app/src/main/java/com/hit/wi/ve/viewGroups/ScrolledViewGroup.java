package com.hit.wi.ve.viewGroups;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.hit.wi.ve.R;
import com.hit.wi.ve.SoftKeyboard;
import com.hit.wi.ve.values.Global;
import com.hit.wi.ve.view.QuickButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/5/20.
 */
public class ScrolledViewGroup extends myViewGroup {
    protected List<QuickButton> buttonList;
    protected HorizontalScrollView horizontalScrollView;
    protected ScrollView scrollView;
    protected FrameLayout.LayoutParams innerLayoutParams;
    protected LinearLayout.LayoutParams outerLayoutParams;
    protected LinearLayout layoutforWrapButtons;
    protected Resources res;
    protected int buttonpadding = 0;
    protected SoftKeyboard softKeyboard8;
    protected Typeface typeface;


    public final int horizontal = 0;
    public final int vertical = 1;

    private int direction;

    protected int width;
    protected int height;

    public ScrolledViewGroup() {
        super();
        buttonList = new ArrayList<QuickButton>();
    }

    public void setSoftKeyboard(SoftKeyboard softKeyboard8) {
        this.softKeyboard8 = softKeyboard8;
    }

    @Override
    public void setTextSize(float size) {
        this.textSize = size;
        for (QuickButton quickButton : buttonList) {
            quickButton.getPaint().setTextSize(textSize);
        }
    }

    public void setTextSize() {
        setTextSize(textSize);
    }

    @Override
    public void setTextColor(int color) {
        textColor = color;
        for (QuickButton quickButton : buttonList) {
            quickButton.setTextColor(color);
        }
    }

    @Override
    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
        for (QuickButton button : buttonList) {
            button.setTypeface(typeface);
        }
    }

    public void setTextColor() {
        setTextColor(textColor);
    }

    @Override
    public void setBackgroundColor(int color) {
        backgroundColor = color;
        for (QuickButton quickButton : buttonList) {
            quickButton.setBackgroundColor(color);
        }
    }

    public void setBackgroundColor() {
        setBackgroundColor(backgroundColor);
    }

    @Override
    public void setBackgroundAlpha(int alpha) {
        backgroundAlpha = alpha;
        for (QuickButton quickButton : buttonList) {
            quickButton.getBackground().setAlpha(alpha);
        }
    }

    public void setBackgroundAlpha() {
        setBackgroundAlpha(backgroundAlpha);
    }

    @Override
    public void setBackgroundResource(int resource) {
        backgroundResource = resource;
        for (QuickButton quickButton : buttonList) {
            quickButton.setBackgroundResource(resource);
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void setButtonAlpha(float alpha) {
        buttonAlpha = alpha;
        for (QuickButton quickButton : buttonList) {
            quickButton.setAlpha(alpha);
        }
    }

    public void setButtonAlpha() {
        setButtonAlpha(buttonAlpha);
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        outerLayoutParams.width = width;
        outerLayoutParams.height = height;
        innerLayoutParams.width = width;
        innerLayoutParams.height = height;
        updateViewLayout();
    }

    protected void updateSkin(int textcolor, int backgroundcolor) {
        this.textColor = textcolor;
        this.backgroundColor = backgroundcolor;
        for (QuickButton button : buttonList) {
            button.setTextColor(textcolor);
            button.setBackgroundColor(backgroundcolor);
            button.getBackground().setAlpha(Global.getCurrentAlpha());
        }
    }

    public void setHeight(int height ){
        this.height = height;
        outerLayoutParams.height = height;
        innerLayoutParams.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
        outerLayoutParams.width = width;
        innerLayoutParams.width = width;
        updateViewLayout();
    }

    public void setPosition(int x, int y) {
        outerLayoutParams.leftMargin = x;
        outerLayoutParams.topMargin = y;
    }

    @Override
    public void addThisToView(ViewGroup viewGroup) {
        parentViewGroup = viewGroup;
        if (direction == horizontal) {
            parentViewGroup.addView(horizontalScrollView, outerLayoutParams);
        } else if (direction == vertical) {
            parentViewGroup.addView(scrollView, outerLayoutParams);
        }
    }

    @Override
    public void setVisibility(int visibility) {
        if (direction == horizontal) {
            horizontalScrollView.setVisibility(visibility);
        } else {
            scrollView.setVisibility(visibility);
        }
        layoutforWrapButtons.setVisibility(visibility);
        for (QuickButton quickButton : buttonList) {
            quickButton.clearAnimation();
            quickButton.setVisibility(visibility);
        }
        updateViewLayout();
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    @Override
    public void clearAnimation() {
        for (QuickButton quickButton : buttonList) {
            quickButton.clearAnimation();
        }
    }

    @Override
    public void startAnimation(Animation animation) {
        for (QuickButton quickButton : buttonList) {
            quickButton.startAnimation(animation);
        }
    }

    public void startAnimation(int anim) {
        for (QuickButton quickButton : buttonList) {
            quickButton.startAnimation(AnimationUtils.loadAnimation(context, anim));
        }
    }

    @Override
    public boolean isShown() {
        boolean ans = false;
        for (QuickButton quickButton : buttonList) {
            ans |= quickButton.isShown();
        }
        return ans;
    }

    @Override
    public void hide() {
        clearAnimation();
        setVisibility(View.GONE);
    }

    @Override
    public void show() {
        clearAnimation();
        setVisibility(View.VISIBLE);
    }

    @Override
    public void setButtonSize(int width, int height) {
        buttonWidth = width;
        buttonHeight = height;
        for (QuickButton eachQuickButton : buttonList) {
            eachQuickButton.itsLayoutParams.height = buttonHeight;
            eachQuickButton.itsLayoutParams.width = buttonWidth;
        }
        float textSize = 2 * Global.textsizeFactor * Math.min(buttonWidth, buttonHeight) / 5;
        setTextSize(textSize);
    }

    public void setButtonPadding(int padding) {
        buttonpadding = padding;
        for (QuickButton eachButton : buttonList) {
            ((LinearLayout.LayoutParams) eachButton.itsLayoutParams).leftMargin = padding;
        }
        if (buttonList.size() > 0) ((LinearLayout.LayoutParams) buttonList.get(0).itsLayoutParams).leftMargin = 0;
    }

    public void setShadowLayer(int radius,int color){
        if(Global.shadowSwitch)
            for (QuickButton button:buttonList) {
                button.setShadowLayer(radius,0,0,color);
            }
    }

    protected QuickButton addButton(String text) {
        QuickButton button = new QuickButton(context);

        button.setSingleLine();
        button.setPadding(0, 0, 0, 0);
        button.setGravity(Gravity.CENTER);
        button.setId(Global.generateViewId());
        button.setTypeface(typeface);
        button.setText(text);
        button.setVisibility(View.GONE);
        button.setBackgroundResource(R.drawable.button_back_x);
        if(Global.shadowSwitch)button.setShadowLayer(Global.shadowRadius,0,0,skinInfoManager.skinData.shadow);

        return button;
    }


    protected QuickButton addButton(int textColor, int backgroundColor, String text) {
        QuickButton button = addButton(text);
        button.setTextColor(textColor);
        button.setBackgroundColor(backgroundColor);

        button.getBackground().setAlpha(Global.getCurrentAlpha());
        return button;
    }

    public void create(int direction, Context context) {
        super.create(context);
        outerLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        innerLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutforWrapButtons = new LinearLayout(context);
        res = context.getResources();
        this.direction = direction;
        if (direction == horizontal) {
            horizontalScrollView = new HorizontalScrollView(context);
            horizontalScrollView.setSmoothScrollingEnabled(true);//很必须，勿删
            horizontalScrollView.setHorizontalScrollBarEnabled(false);
            layoutforWrapButtons.setOrientation(LinearLayout.HORIZONTAL);
            horizontalScrollView.addView(layoutforWrapButtons, innerLayoutParams);
        } else if (direction == vertical) {
            scrollView = new ScrollView(context);
            scrollView.setSmoothScrollingEnabled(true);
            scrollView.setScrollbarFadingEnabled(false);
            layoutforWrapButtons.setOrientation(LinearLayout.VERTICAL);
            scrollView.addView(layoutforWrapButtons, innerLayoutParams);
        }
    }

    public void updateViewLayout() {
        if (direction == horizontal) {
            horizontalScrollView.updateViewLayout(layoutforWrapButtons, innerLayoutParams);
            parentViewGroup.updateViewLayout(horizontalScrollView, outerLayoutParams);
        } else {
            scrollView.updateViewLayout(layoutforWrapButtons, innerLayoutParams);
            parentViewGroup.updateViewLayout(scrollView, outerLayoutParams);
        }

    }

    public void setButtonWidth(int buttonWidth) {
        this.buttonWidth = buttonWidth;
        for (QuickButton button : buttonList) {
            button.itsLayoutParams.width = buttonWidth;
            button.itsLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        }
    }
}
