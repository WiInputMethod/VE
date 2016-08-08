package com.hit.wi.t9.viewGroups;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.hit.wi.util.ViewsUtil;
import com.hit.wi.t9.R;
import com.hit.wi.t9.SoftKeyboard;
import com.hit.wi.t9.values.Global;
import com.hit.wi.t9.view.QuickButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by purebleusong on 2015/7/22.
 */
public class NonScrollViewGroup extends myViewGroup {
    public List<QuickButton> buttonList;
    protected Resources res;
    public LinearLayout viewGroupWrapper;
    public LinearLayout.LayoutParams paramsForViewGroup;
    protected SoftKeyboard softKeyboard;

    int padding = 0;


    public NonScrollViewGroup() {
        super();
        buttonList = new ArrayList<QuickButton>();
    }

    @Override
    public void addThisToView(ViewGroup viewGroup) {
        parentViewGroup = viewGroup;
        viewGroup.addView(viewGroupWrapper, paramsForViewGroup);
    }

    @Override
    public void setTextSize(float size) {
        this.textSize = size;
        for (QuickButton quickButton : buttonList) {
            quickButton.getPaint().setTextSize(textSize);
        }
    }

    @Override
    public void setTextColor(int color) {
        textColor = color;
        for (QuickButton quickButton : buttonList) {
            quickButton.setTextColor(color);
        }
    }

    @Override
    public void setSize(int width, int height) {
        paramsForViewGroup.width = width;
        paramsForViewGroup.height = height;
    }

    public void setPosition(int x, int y) {
        paramsForViewGroup.leftMargin = x;
        paramsForViewGroup.topMargin = y;
    }

    @Override
    public void setBackgroundColor(int color) {
        backgroundColor = color;
        for (QuickButton quickButton : buttonList) {
            ViewsUtil.setBackgroundWithGradientDrawable(quickButton, color);
        }
    }

    @Override
    public void setBackgroundAlpha(int alpha) {
        backgroundAlpha = alpha;
        for (QuickButton quickButton : buttonList) {
            quickButton.getBackground().setAlpha(alpha);
        }
    }

    @Override
    public void setBackgroundResource(int resource) {
        backgroundResource = resource;
        for (QuickButton quickButton : buttonList) {
            quickButton.setBackgroundResource(resource);
        }
    }

    public int getHeight() {
        if (paramsForViewGroup.height > 0) return paramsForViewGroup.height;
        else return viewGroupWrapper.getMeasuredHeight();
    }

    @SuppressLint("NewApi")
    @Override
    public void setButtonAlpha(float alpha) {
        buttonAlpha = alpha;
        for (QuickButton quickButton : buttonList) {
            quickButton.setAlpha(alpha);
        }
    }

    @Override
    public void setVisibility(int visibility) {
        viewGroupWrapper.clearAnimation();
        viewGroupWrapper.setVisibility(visibility);
//        int childnum = viewGroupWrapper.getChildCount();
//        for(int i = 0;i<childnum;i++) {
//            viewGroupWrapper.getChildAt(childnum).setVisibility(visibility);
//        }
        for (QuickButton quickButton : buttonList) {
            quickButton.clearAnimation();
            quickButton.setVisibility(visibility);
        }
    }


    @Override
    public void clearAnimation() {
        for (QuickButton quickButton : buttonList) {
            quickButton.clearAnimation();
        }
        int childNum = viewGroupWrapper.getChildCount();
        for (int i = 0; i < childNum; i++) {
            viewGroupWrapper.getChildAt(i).clearAnimation();
        }
    }

    private Animation.AnimationListener getClearAnimationListener(final View v) {
        return new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
    }

    @Override
    public void startAnimation(Animation animation) {
        for (QuickButton quickButton : buttonList) {
//            animation.setAnimationListener(getClearAnimationListener(quickButton));
            quickButton.startAnimation(animation);
        }
    }

    public void updateViewLayout() {
        parentViewGroup.updateViewLayout(viewGroupWrapper, paramsForViewGroup);
    }

    public void startAnimation(int[] anims) {
        int i = 0;
        for (QuickButton quickButton : buttonList) {
            quickButton.startAnimation(AnimationUtils.loadAnimation(context, anims[i++]));
        }
    }

    protected Animation.AnimationListener getMyAnimationListener(final View view) {
        Animation.AnimationListener al = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        return al;
    }

    public void startAnimation(int anim) {
        for (QuickButton quickButton : buttonList) {
            quickButton.startAnimation(AnimationUtils.loadAnimation(context, anim));
        }
//        int childnum = viewGroupWrapper.getChildCount();
//        for (int i=0;i<childnum;i++) {
//            viewGroupWrapper.getChildAt(i).startAnimation(AnimationUtils.loadAnimation(context, anim));
//        }
    }

    @Override
    public boolean isShown() {
        boolean ans = false;
        for (QuickButton quickButton : buttonList) {
            ans |= quickButton.isShown();
        }
//        int childnum = viewGroupWrapper.getChildCount();
//        for (int i=0;i<childnum;i++) {
//            ans |= viewGroupWrapper.getChildAt(i).isShown();
//        }
        return ans;
    }

    @Override
    public void hide() {
        for (QuickButton quickButton : buttonList) {
            quickButton.clearAnimation();
            quickButton.setVisibility(View.GONE);
        }
        viewGroupWrapper.setVisibility(View.GONE);
    }

    @Override
    public void show() {
        for (QuickButton quickButton : buttonList) {
            quickButton.clearAnimation();
            quickButton.setVisibility(View.VISIBLE);
        }
        viewGroupWrapper.setVisibility(View.VISIBLE);
    }


    @Override
    public void setButtonSize(int width, int height) {
        buttonWidth = width;
        buttonHeight = height;
        for (QuickButton eachQuickButton : buttonList) {
            eachQuickButton.itsLayoutParams.width = width;
            eachQuickButton.itsLayoutParams.height = height;
        }
    }

    public void setButtonSize(int[] width, int height) {
        int i = 0;
        buttonHeight = height;
        for (QuickButton eachQuickButton : buttonList) {
            eachQuickButton.itsLayoutParams.width = width[i++];
            eachQuickButton.itsLayoutParams.height = height;
        }
    }

    public void updateTextSize() {
        setTextSize(textSize);
    }

    public void setTypeface(Typeface typeface) {
        for (QuickButton button : buttonList) {
            button.setTypeface(typeface);
        }
    }


    public void setSoftKeyboard(SoftKeyboard softKeyboard) {
        this.softKeyboard = softKeyboard;
    }


    public QuickButton addNewButton(String text) {
        QuickButton button = new QuickButton(context);

        button.setBackgroundResource(R.drawable.button_back_x);
        button.setGravity(Gravity.CENTER);
        button.setVisibility(View.GONE);
        button.getBackground().setAlpha(Global.getCurrentAlpha());
        button.setText(text);
        button.setSingleLine();

        if (Global.shadowSwitch)
            button.setShadowLayer(Global.shadowRadius, 0, 0, skinInfoManager.skinData.shadow);

        return button;
    }

    public void setShadowLayer(int radius, int color) {
        if (Global.shadowSwitch)
            for (QuickButton button : buttonList) {
                button.setShadowLayer(radius, 0, 0, color);
            }
    }

    public QuickButton addButton(String text, int textColor, int backgroundColor) {
        QuickButton button = this.addNewButton(text);

        button.setBackgroundResource(R.drawable.middle_button);
        button.setTextColor(textColor);
        ViewsUtil.setBackgroundWithGradientDrawable(button, backgroundColor);
        //button.setBackgroundColor(backgroundColor);
        button.getBackground().setAlpha(Global.getCurrentAlpha());
        return button;
    }


    public void create(Context context) {
        super.create(context);
        res = context.getResources();
        viewGroupWrapper = new LinearLayout(context);
        paramsForViewGroup = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void setButtonPadding(int padding) {
        this.padding = padding;
        for (QuickButton button : buttonList) {
            ((LinearLayout.LayoutParams) button.itsLayoutParams).leftMargin = padding;
        }
    }

    public void setButtonWidth(int width) {
        buttonWidth = width;
        for (QuickButton button : buttonList) {
            button.itsLayoutParams.width = width;
            button.itsLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        }
    }

    public void setButtonWidth(int[] widths) {
        int i = 0;
        for (QuickButton button : buttonList) {
            if (widths.length <= i) {
                break;
            }
            button.itsLayoutParams.width = widths[i++];
        }
    }

    public void setButtonWidthByRate(int[] widths) {
        int i = 0;
        int buttonnum = widths.length;
        for (int width : widths) {
            widths[i++] = (paramsForViewGroup.width - buttonnum * padding) * width / 100;
        }
        setButtonWidth(widths);
    }
}
