package com.hit.wi.t9.viewGroups;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hit.wi.jni.Kernel;
import com.hit.wi.util.DisplayUtil;
import com.hit.wi.util.ViewsUtil;
import com.hit.wi.util.WIMath;
import com.hit.wi.t9.values.Global;
import com.hit.wi.t9.view.QuickButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/8/10.
 */
public class PreFixViewGroup extends ScrolledViewGroup {
    private final float TEXTSIZE_RATE = (float) 0.6;

    public void create(Context context) {
        super.create(super.horizontal, context);
    }

    public QuickButton addButton(String text) {
        QuickButton button = super.addButton(
                skinInfoManager.skinData.textcolor_quickSymbol,
                skinInfoManager.skinData.backcolor_prefix,
                text);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.leftMargin = buttonpadding;
        button.itsLayoutParams = params;

        layoutforWrapButtons.addView(button, params);
        return button;
    }

    private int lastState = -1;

    public void refreshState() {
        int prefixNumber = Kernel.getPrefixNumber();
        if (lastState == prefixNumber) return;
        if (prefixNumber > 1 && Global.currentKeyboard == Global.KEYBOARD_T9 && !Global.inLarge) {
            List<String> texts = new ArrayList<>();
            for (int i = prefixNumber - 1; i > 0; i--) {
                texts.add(Kernel.getPrefix(i));
            }
            setText(texts);
            setVisibility(View.VISIBLE);
        } else {
            setVisibility(View.GONE);
        }
        lastState = prefixNumber;
    }


    public void setText(List<String> texts) {
        int i = 0;
        for (String text : texts) {
            text = text.replace("'", "");
            QuickButton button;
            if (i < buttonList.size()) {
                button = buttonList.get(i++);
            } else {
                button = addButton(text);
                button.setOnTouchListener(prefixOnTouchListener);
                buttonList.add(button);
                i++;
            }
            int textsizeJudge;
            if (WIMath.min(buttonWidth, height) <= 0)
                textsizeJudge = buttonWidth == 0 ? height : buttonWidth;
            else textsizeJudge = WIMath.min(buttonWidth, height);
            button.setTextSize(DisplayUtil.px2sp(context, textsizeJudge * TEXTSIZE_RATE));
            button.setText(text);
            button.setVisibility(View.VISIBLE);
        }
        for (; i < buttonList.size(); i++) {
            layoutforWrapButtons.removeView(buttonList.get(buttonList.size() - 1));
            buttonList.remove(buttonList.size() - 1);
        }
        setButtonWidth(width / Math.min(buttonList.size() + 1, 4) - buttonpadding);
        if (buttonList.size() > 0)
            ((LinearLayout.LayoutParams) buttonList.get(0).itsLayoutParams).leftMargin = 0;
    }

    public void setBackgroundColorByIndex(int color, int index) {
        ViewsUtil.setBackgroundWithGradientDrawable(buttonList.get(index), color);
//        buttonList.get(index).setBackgroundColor(color);
    }

    public void updateSkin() {
        super.updateSkin(
                skinInfoManager.skinData.textcolor_quickSymbol,
                skinInfoManager.skinData.backcolor_prefix
        );
    }

    private void onTouchEffect(View v, MotionEvent event) {
        softKeyboard.keyboardTouchEffect.onTouchEffectWithAnim(v, event.getAction(),
                skinInfoManager.skinData.backcolor_touchdown,
                skinInfoManager.skinData.backcolor_prefix
        );
        softKeyboard.transparencyHandle.handleAlpha(event.getAction());
    }

    private View.OnTouchListener prefixOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            onTouchEffect(v, event);
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Kernel.selectPrefix(buttonList.size() - buttonList.indexOf(v));
                softKeyboard.refreshDisplay();
            }
            return false;
        }
    };

}
