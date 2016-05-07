package com.hit.wi.ve.viewGroups;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.hit.wi.jni.Kernel;
import com.hit.wi.util.ViewsUtil;
import com.hit.wi.util.WIMath;
import com.hit.wi.ve.values.Global;
import com.hit.wi.ve.view.QuickButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/8/10.
 */
public class PreFixViewGroup extends ScrolledViewGroup {
    public void create(Context context) {
        super.create(super.horizontal, context);
    }

    public QuickButton addButtonP(String text) {
        QuickButton button = super.addButton(
                skinInfoManager.skinData.textcolor_quickSymbol,
                skinInfoManager.skinData.backcolor_prefix,
                text);

        LinearLayout.LayoutParams buttonparams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        buttonparams.leftMargin = buttonpadding;
        button.itsLayoutParams = buttonparams;

        layoutforWrapButtons.addView(button, buttonparams);
        return button;
    }

    private int lastState = -1;
    public void refreshState() {
        int prefixnum = Kernel.getPrefixNumber();
        if(lastState == prefixnum)return;
        if (prefixnum > 1 && Global.currentKeyboard == Global.KEYBOARD_T9 && !Global.inLarge) {
            List<String> texts = new ArrayList<>();
            for (int i = prefixnum - 1; i > 0; i--) {
                texts.add(Kernel.getPrefix(i));
            }
            setText(texts);
            softKeyboard8.quickSymbolViewGroup.setVisibility(View.GONE);
            softKeyboard8.specialSymbolChooseViewGroup.setVisibility(View.GONE);
            setVisibility(View.VISIBLE);
        } else {
            softKeyboard8.quickSymbolViewGroup.setVisibility(View.VISIBLE);
            setVisibility(View.GONE);
        }
        lastState = prefixnum;
    }


    public void setText(List<String> texts) {
        int i = 0;
        for (String text : texts) {
            text = text.replace("'", "");
            QuickButton button;
            if (i < buttonList.size()) {
                button = buttonList.get(i++);
            } else {
                button = addButtonP(text);
                button.setOnTouchListener(prefixOnTouchListener);
                buttonList.add(button);
                i++;
            }
            button.setTextSize(2 * WIMath.min(buttonWidth, height) / 9);
            button.setText(text);
            button.setVisibility(View.VISIBLE);
        }
        for (; i < buttonList.size(); i++) {
            layoutforWrapButtons.removeView(buttonList.get(buttonList.size() - 1));
            buttonList.remove(buttonList.size() - 1);
        }
        setButtonWidth(width / Math.min(buttonList.size() + 1, 4) - buttonpadding);
        if (buttonList.size() > 0) ((LinearLayout.LayoutParams) buttonList.get(0).itsLayoutParams).leftMargin = 0;
    }

    public void setBackgroundColorByIndex(int color, int index) {
        ViewsUtil.setBackgroundWithGradientDrawable(buttonList.get(index),color);
//        buttonList.get(index).setBackgroundColor(color);
    }

    public void updateSkin() {
        super.updateSkin(
                skinInfoManager.skinData.textcolor_quickSymbol,
                skinInfoManager.skinData.backcolor_prefix
        );
    }

    public int getChildnum() {
        return buttonList.size();
    }

    private View.OnTouchListener prefixOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            softKeyboard8.transparencyHandle.handleAlpha(event.getAction());
            if (event.getAction() == MotionEvent.ACTION_UP) {
                String words = Kernel.getPrefix(buttonList.size() - buttonList.indexOf(v));
                if (words != null) {
                    softKeyboard8.commitText(words);
                }
                softKeyboard8.refreshDisplay();
            }
            softKeyboard8.keyboardTouchEffect.onTouchEffectWithAnim(v, event.getAction(),
                    skinInfoManager.skinData.backcolor_touchdown,
                    skinInfoManager.skinData.backcolor_prefix,
                    context);
            return false;
        }
    };

}
