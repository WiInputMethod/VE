package com.hit.wi.ve.viewGroups;

import android.content.Context;
import android.view.*;
import android.widget.LinearLayout;
import com.hit.wi.util.InputMode;
import com.hit.wi.util.WIStringManager;

import com.hit.wi.ve.Interfaces.ViewGroupInterface;
import com.hit.wi.ve.R;
import com.hit.wi.ve.values.Global;
import com.hit.wi.ve.view.QuickButton;

/**
 * Created by Administrator on 2015/7/28.
 */
public class SpecialSymbolChooseViewGroup extends NonScrollViewGroup implements ViewGroupInterface {

    QuickButton leftbutton;
    QuickButton middlebutton;
    QuickButton rightbutton;
    String[] flagtext;
    String[] keys;

    int margin = 0;
    int listflag = 0;

    public void create(Context context) {
        super.create(context);
        viewGroupWrapper.setOrientation(LinearLayout.HORIZONTAL);

        String[] text = res.getStringArray(R.array.SYMBOL_FUNC);
        leftbutton = addButtonS(InputMode.halfToFull(text[0]));
        middlebutton = addButtonS(InputMode.halfToFull(text[1]));
        rightbutton = addButtonS(InputMode.halfToFull(text[2]));
        buttonList.add(leftbutton);
        buttonList.add(middlebutton);
        buttonList.add(rightbutton);
        for (QuickButton button : buttonList) {
            button.setOnTouchListener(mOnTouchListener);
        }
    }

    @Override
    public void updateSkin() {
        setBackgroundColor(skinInfoManager.skinData.backcolor_quickSymbol);
        setBackgroundAlpha(Global.getCurrentAlpha());
    }

    public void refreshState(boolean show) {
        if (Global.currentKeyboard == Global.KEYBOARD_SYM) {
            if(show && isShown()){
                setVisibility(View.VISIBLE);
            } else {
                setVisibility(View.GONE);
            }
        } else {
            clearAnimation();
            setVisibility(View.GONE);
        }
    }
    public void updateSize(int width, int height) {
        setSize(width, height);
        this.setButtonSize(width, height);
    }

    public void setButtonSize(int width, int height) {
        int unitwidth = (width - 3 * margin) / 4;
        super.setButtonSize(unitwidth, height);
        buttonList.get(1).itsLayoutParams.width = unitwidth * 2;
    }

    public void setPosition(int x, int y) {
        paramsForViewGroup.leftMargin = x;
        paramsForViewGroup.topMargin = y;
        for (QuickButton button : buttonList) {
            ((LinearLayout.LayoutParams) button.itsLayoutParams).leftMargin = x;
        }
        ((LinearLayout.LayoutParams) buttonList.get(0).itsLayoutParams).leftMargin = 0;
    }

    public void setFlagTextandKeys(String[] flagtext, String[] keys) {
        this.flagtext = flagtext;
        this.keys = keys;
        middlebutton.setText(flagtext[0]);
    }

    public QuickButton addButtonS(String text) {
        QuickButton button = super.addButton(text,
                skinInfoManager.skinData.textcolors_quickSymbol,
                skinInfoManager.skinData.backcolor_quickSymbol
        );
        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        button.setId(Global.generateViewId());
        button.itsLayoutParams = buttonLayoutParams;
        viewGroupWrapper.addView(button, buttonLayoutParams);
        return button;
    }



    private void showSymbolsByIndex(int index){
        if (index == 0){
            softKeyboard8.candidatesViewGroup.displayCandidates(
                    Global.SYMBOL,
                    WIStringManager.convertStringstoList(softKeyboard8.symbolsManager.SPECIAL),100);
        } else if (index == 1){
            softKeyboard8.candidatesViewGroup.displayCandidates(
                    Global.SYMBOL,
                    WIStringManager.convertStringstoList(softKeyboard8.symbolsManager.BU_SHOU),100);
        } else if (index == 2){
            softKeyboard8.candidatesViewGroup.displayCandidates(
                    Global.SYMBOL,
                    WIStringManager.convertStringstoList(softKeyboard8.symbolsManager.PHONETIC),100);
        } else if (index == 3){
            softKeyboard8.candidatesViewGroup.displayCandidates(
                    Global.SYMBOL,
                    WIStringManager.convertStringstoList(softKeyboard8.symbolsManager.RUSSIAN),100);
        } else if (index == 4){
            softKeyboard8.candidatesViewGroup.displayCandidates(
                    Global.SYMBOL,
                    WIStringManager.convertStringstoList(softKeyboard8.symbolsManager.JAPANESE),100);
        } else if (index == 5){
            softKeyboard8.candidatesViewGroup.displayCandidates(
                    Global.SYMBOL,
                    WIStringManager.convertStringstoList(softKeyboard8.symbolsManager.BOPOMOFO),100);
        } else if (index == 6){
            softKeyboard8.candidatesViewGroup.displayCandidates(
                    Global.SYMBOL,
                    WIStringManager.convertStringstoList(softKeyboard8.symbolsManager.GREECE),50);
        }
    }

    View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            softKeyboard8.keyboardTouchEffect.onTouchEffectWithAnim(v, event.getAction(),
                    skinInfoManager.skinData.backcolor_touchdown,
                    skinInfoManager.skinData.backcolor_quickSymbol,
                    context
            );
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (v.getId() == rightbutton.getId()) {
                    listflag++;
                    listflag %= flagtext.length;
                } else if (v.getId() == leftbutton.getId()) {
                    listflag--;
                    if (listflag < 0) {
                        listflag = flagtext.length - 1;
                    }
                }
                showSymbolsByIndex(listflag);
                middlebutton.setText(flagtext[listflag]);
            }
            return true;
        }
    };
}
