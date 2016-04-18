package com.hit.wi.ve.viewGroups;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import com.hit.wi.jni.WIInputMethodNK;
import com.hit.wi.jni.WIInputMethod;

import com.hit.wi.util.CommonFuncs;
import com.hit.wi.util.InputMode;
import com.hit.wi.util.ViewFuncs;
import com.hit.wi.util.WIStringManager;

import com.hit.wi.ve.R;
import com.hit.wi.ve.values.Global;
import com.hit.wi.ve.view.QuickButton;

import java.util.*;

/**
 * 底部的操作栏
 * Created by Administrator on 2015/9/17.
 */
public class BottomBarViewGroup extends NonScrollViewGroup {


    public QuickButton switchKeyboardButton;
    public QuickButton expressionButton;
    public QuickButton spaceButton;
    public QuickButton enterButton;
    public QuickButton zeroButton;
    public QuickButton returnButton;
    String[] button_text;

    private String spaceText;
    private Context mContext;

    public void create(Context context) {
        super.create(context);
        mContext = context;
        button_text = res.getStringArray(R.array.BOTTOMBAR_TEXT);
        spaceText = InputMode.fullToHalf(PreferenceManager.getDefaultSharedPreferences(context).getString("ZH_SPACE_TEXT", button_text[2]));
        switchKeyboardButton = addButtonB(button_text[0]);
        expressionButton = addButtonB(button_text[1]);
        spaceButton = addButtonB(spaceText);
        enterButton = addButtonB(button_text[3]);
        zeroButton = addButtonB("0");
        returnButton = addButtonB("返回");

        switchKeyboardButton.setOnTouchListener(switchKeyONTouchListener);
        expressionButton.setOnTouchListener(expressionOnTouchListener);
        spaceButton.setOnTouchListener(spaceOnTouchListener);
        enterButton.setOnTouchListener(enterOnTouchListener);
        zeroButton.setOnTouchListener(zeroOnTouchListener);
        returnButton.setOnTouchListener(returnOnTouchListener);

        buttonList.add(switchKeyboardButton);
        buttonList.add(expressionButton);
        buttonList.add(spaceButton);
        buttonList.add(enterButton);
        buttonList.add(zeroButton);
        buttonList.add(returnButton);

        removeButton(returnButton);
        removeButton(zeroButton);
    }

    public void setText(List<String> texts) {


        int i = 0;
        for (String text : texts) {
            if (i < buttonList.size()) {
                QuickButton button = buttonList.get(i++);
                button.setText(text);
            } else {
                QuickButton button = addButtonB(text);
                button.setVisibility(View.VISIBLE);
                buttonList.add(button);
                i++;
            }
        }
        while (i < buttonList.size()) {
            removeButton(buttonList.get(buttonList.size() - 1));
        }
    }

    public QuickButton addButtonB(String text) {
        QuickButton button = super.addButton(text,
                skinInfoManager.skinData.textcolors_26keys,
                skinInfoManager.skinData.backcolor_26keys);

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        buttonParams.leftMargin = padding;

        button.itsLayoutParams = buttonParams;
        viewGroupWrapper.addView(button, buttonParams);
        return button;
    }

    public void removeButton(QuickButton button) {
        if (!buttonList.contains(button)) return;
        viewGroupWrapper.removeView(button);
        buttonList.remove(button);
    }

    public void receiveButtonToPosition(QuickButton button, int position) {
        if (!buttonList.contains(button)){
            buttonList.add(position, button);
        }
        if(viewGroupWrapper.indexOfChild(button)<0){
            viewGroupWrapper.addView(button, position);
        }
        button.clearAnimation();
        button.setVisibility(View.VISIBLE);
    }

    public void receiveButtonToTail(QuickButton button) {
        receiveButtonToPosition(button, buttonList.size());
    }

    public void setTextSize() {
        for (QuickButton button : buttonList) {
            button.setTextSize(1 * getHeight() / 4);
        }
    }

    public void intoReturnState () {
        removeButton(switchKeyboardButton);
        if (buttonList.size()>3)
            removeButton(buttonList.get(3));
        receiveButtonToTail(returnButton);
        ((LinearLayout.LayoutParams)returnButton.itsLayoutParams).leftMargin = padding;
        setButtonWidthByRate(res.getIntArray(R.array.BOTTOMBAR_RETURN_WIDTH));
        returnButton.getBackground().setAlpha((int) (Global.mCurrentAlpha * 255));
    }

    public void backReturnState () {
        removeButton(returnButton);
        receiveButtonToPosition(switchKeyboardButton,0);
        setButtonWidthByRate(res.getIntArray(R.array.BOTTOMBAR_KEY_WIDTH));
    }


    public void switchToKeyboard(int keyboard) {
        receiveButtonToPosition(switchKeyboardButton,0);
        viewGroupWrapper.updateViewLayout(switchKeyboardButton,switchKeyboardButton.itsLayoutParams);
        expressionButton.setText(button_text[1]);
        switch (keyboard) {
            case Global.KEYBOARD_T9:
            case Global.KEYBOARD_QP:
            case Global.KEYBOARD_EN:
                removeButton(zeroButton);
                receiveButtonToPosition(expressionButton, 1);
                removeButton(returnButton);
                receiveButtonToTail(spaceButton);
                setButtonWidthByRate(res.getIntArray(R.array.BOTTOMBAR_KEY_WIDTH));
                break;
            case Global.KEYBOARD_SYM:
                removeButton(zeroButton);
                receiveButtonToPosition(expressionButton, 1);
                receiveButtonToPosition(returnButton,3);
                receiveButtonToTail(enterButton);
                setButtonWidthByRate(res.getIntArray(R.array.BOTTOMBAR_KEY_WIDTH));
                break;
            case Global.KEYBOARD_NUM:
                removeButton(expressionButton);
                removeButton(returnButton);
                removeButton(spaceButton);
                receiveButtonToPosition(zeroButton, 2);
                ((LinearLayout.LayoutParams) zeroButton.itsLayoutParams).leftMargin = padding;//special, i dont know why, but dont delete it
                setButtonWidthByRate(res.getIntArray(R.array.BOTTOMBAR_NUM_KEY_WIDTH));
                break;
            default:
                removeButton(zeroButton);
                receiveButtonToPosition(expressionButton, 1);
                setButtonWidthByRate(res.getIntArray(R.array.BOTTOMBAR_KEY_WIDTH));
                break;
        }
        viewGroupWrapper.updateViewLayout(switchKeyboardButton,switchKeyboardButton.itsLayoutParams);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        switch (keyboard) {
            case Global.KEYBOARD_T9:
                editor.putString("KEYBOARD_SELECTOR", "1");
                break;
            case Global.KEYBOARD_QP:
                editor.putString("KEYBOARD_SELECTOR", "2");
                break;
        }
        editor.commit();
    }

    public void setTypeFace(Typeface typeFace) {
        for (QuickButton button : buttonList) {
            button.setTypeface(typeFace);
        }
    }

    public void updateSkin() {
        switchKeyboardButton.setTextColor(skinInfoManager.skinData.textcolors_enter);
        //switchKeyboardButton.setBackgroundColor(skinInfoManager.skinData.backcolor_enter);
        switchKeyboardButton.getBackground().setColorFilter(skinInfoManager.skinData.backcolor_enter, PorterDuff.Mode.SRC);

        expressionButton.setTextColor(skinInfoManager.skinData.textcolors_space);
        //expressionButton.setBackgroundColor(skinInfoManager.skinData.backcolor_space);
        expressionButton.getBackground().setColorFilter(skinInfoManager.skinData.backcolor_space, PorterDuff.Mode.SRC);

        spaceButton.setTextColor(skinInfoManager.skinData.textcolors_space);
        //spaceButton.setBackgroundColor(skinInfoManager.skinData.backcolor_space);
        spaceButton.getBackground().setColorFilter(skinInfoManager.skinData.backcolor_space, PorterDuff.Mode.SRC);

        enterButton.setTextColor(skinInfoManager.skinData.textcolors_enter);
        //enterButton.setBackgroundColor(skinInfoManager.skinData.backcolor_enter);
        enterButton.getBackground().setColorFilter(skinInfoManager.skinData.backcolor_enter, PorterDuff.Mode.SRC);

        zeroButton.setTextColor(skinInfoManager.skinData.textcolors_zero);
        //zeroButton.setBackgroundColor(skinInfoManager.skinData.backcolor_zero);
        zeroButton.getBackground().setColorFilter(skinInfoManager.skinData.backcolor_zero, PorterDuff.Mode.SRC);

        returnButton.setTextColor(skinInfoManager.skinData.textcolors_enter);
        //returnButton.setBackgroundColor(skinInfoManager.skinData.backcolor_enter);
        returnButton.getBackground().setColorFilter(skinInfoManager.skinData.backcolor_enter, PorterDuff.Mode.SRC);

        setBackgroundAlpha((int) (Global.mCurrentAlpha * 255));
    }


    private void onTouchEffect(View view, int action, int touchdowncolor, int nomalcolor) {
        softKeyboard8.transparencyHandle.handleAlpha(action);
        softKeyboard8.keyBoardTouchEffect.onTouchEffectWithAnim(view, action, touchdowncolor, nomalcolor, context);
    }


    private View.OnTouchListener switchKeyONTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int position = (((int) (event.getRawX() - softKeyboard8.keyboardParams.x)) / (buttonWidth + padding)) % 5;
            if (!Global.inLarge){
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    removeButton(expressionButton);
                    removeButton(spaceButton);
                    removeButton(zeroButton);
                    removeButton(enterButton);
                    setText(WIStringManager.convertStringstoList(res.getStringArray(R.array.KEYBOARD_TYPE)));
                    setButtonWidth(paramsForViewGroup.width / buttonList.size() - padding);
                    setBackgroundColor(skinInfoManager.skinData.backcolor_26keys);
                    break;
                case MotionEvent.ACTION_MOVE:
                    setBackgroundColor(skinInfoManager.skinData.backcolor_26keys);
                    buttonList.get(position).setBackgroundColor(skinInfoManager.skinData.backcolor_touchdown);
                    break;
                case MotionEvent.ACTION_UP:
                    setText(Collections.EMPTY_LIST);
                    int[] keyboards = {Global.currentKeyboard, Global.KEYBOARD_QP, Global.KEYBOARD_EN, Global.KEYBOARD_NUM, Global.KEYBOARD_T9};
                    if (keyboards[position] != Global.KEYBOARD_NUM) receiveButtonToTail(expressionButton);
                    receiveButtonToTail(spaceButton);
                    if (keyboards[position] == Global.KEYBOARD_NUM) receiveButtonToTail(zeroButton);
                    receiveButtonToTail(enterButton);
                    final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
                    if (keyboards[position] == Global.KEYBOARD_EN) {
                        buttonList.get(position).setText(sp.getString("EN_SPACE_TEXT", "space"));
                    }
                    if (keyboards[position] == Global.KEYBOARD_QP) {
                        buttonList.get(position + 1).setText(sp.getString("ZH_SPACE_TEXT", "空格"));
                    }
                    setEnterText(enterButton, softKeyboard8.getCurrentInputEditorInfo(), keyboards[position]);
                    softKeyboard8.switchKeyboardTo(keyboards[position], true);
                    updateSkin();
                    break;
            }
            }
            setBackgroundAlpha((int) (Global.mCurrentAlpha * 255));
            onTouchEffect(v, event.getAction(),
                    skinInfoManager.skinData.backcolor_touchdown,
                    skinInfoManager.skinData.backcolor_enter
            );
            return true;
        }
    };

    public int expressionFlag = 0;

    private List<String> getExpression(int flag){
        List<String> expression;
        if (flag == 0){
            expression = WIStringManager.convertStringstoList(softKeyboard8.symbolsManager.EmojiFace);
        } else if (flag == 1){
            expression = WIStringManager.convertStringstoList(softKeyboard8.symbolsManager.SMILE);
        } else {
            expression = new ArrayList<>();
        }
        return expression;
    }

    private View.OnTouchListener expressionOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if(Global.isInView(v,event)) {
                    Global.clearKernel();
                    List<String> expressions = getExpression(expressionFlag);
                    if(expressions != Collections.EMPTY_LIST){
                        softKeyboard8.qkCandidatesViewGroup.displayCandidates(Global.SYMBOL,expressions, Global.inLarge?200:9);
                        if (Global.inLarge)softKeyboard8.qkCandidatesViewGroup.largeTheCandidate();
                        softKeyboard8.refreshDisplay(true);
                    }else {
                        CommonFuncs.showToast(context, "很抱歉，我们暂时不能再您的手机上获取emoji库，正在修复中……");
                    }
                    expressionFlag = (expressionFlag + 1) % 2;
                }
            }
            onTouchEffect(v, event.getAction(),
                    skinInfoManager.skinData.backcolor_touchdown,
                    skinInfoManager.skinData.backcolor_space
            );
            return true;
        }
    };

    String[] commit_text_space = {"，","。","!","?"," "};

    private View.OnTouchListener spaceOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                softKeyboard8.lightViewManager.lightViewAnimate(v,event);
                if ((WIInputMethodNK.GetWordsNumber() > 0) || (WIInputMethod.GetWordsNumber() > 0) && Global.currentKeyboard != Global.KEYBOARD_SYM) {
                    softKeyboard8.chooseWord(0);
                } else{
                    int index = ViewFuncs.computePosition(event.getX(), event.getY(), v.getHeight(), v.getWidth());
                    if (index == Global.ERR)index = 4;
                    softKeyboard8.commitText(commit_text_space[index]);
                }

                if (Global.inLarge && softKeyboard8.qkCandidatesViewGroup.isShown()) {
                    softKeyboard8.qkCandidatesViewGroup.largeTheCandidate();
                } else {
                    softKeyboard8.refreshDisplay();
                    backReturnState();
                }
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                softKeyboard8.lightViewManager.HideLightView(event.getX(),event.getY(),v.getWidth(),v.getHeight());
                int index = ViewFuncs.computePosition(event.getX(), event.getY(), v.getHeight(), v.getWidth());
                if (index == Global.ERR)index = 4;
                softKeyboard8.lightViewManager.ShowLightView(event.getX(),event.getY(),v.getWidth(),v.getHeight(),commit_text_space[index]);
            }
            onTouchEffect(v, event.getAction(), skinInfoManager.skinData.backcolor_touchdown, skinInfoManager.skinData.backcolor_space);
            return false;
        }
    };

    private View.OnTouchListener enterOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (Global.currentKeyboard == Global.KEYBOARD_T9 && WIInputMethodNK.GetWordsNumber() > 0) {
                    if (event.getY() > 0) {
                        softKeyboard8.commitText(WIInputMethodNK.ReturnAction());
                    }
                    softKeyboard8.t9InputViewGroup.updateFirstKeyText();
                } else if (Global.currentKeyboard == Global.KEYBOARD_QP && WIInputMethod.GetWordsNumber() > 0) {
                    if (event.getY() > 0) {
                        softKeyboard8.commitText(WIInputMethod.ReturnAction());
                    }
                } else {
                    if (!softKeyboard8.sendDefaultEditorAction(true) && Global.isInView(v,event)) {
                        softKeyboard8.sendDownUpKeyEvents(KeyEvent.KEYCODE_ENTER);
                    }
                }
                softKeyboard8.qkCandidatesViewGroup.smallTheCandidate();
                backReturnState();
                Global.inLarge = false;
                Global.refreshState(softKeyboard8);
            }
            onTouchEffect(v, event.getAction(),
                    skinInfoManager.skinData.backcolor_touchdown,
                    skinInfoManager.skinData.backcolor_enter
            );
            return false;
        }
    };

    private View.OnTouchListener zeroOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                softKeyboard8.commitText("0");
            }
            onTouchEffect(v, event.getAction(),
                    skinInfoManager.skinData.backcolor_touchdown,
                    skinInfoManager.skinData.backcolor_zero
            );
            return false;
        }
    };

    private View.OnTouchListener returnOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (Global.isInView(view,motionEvent) && Global.inLarge){
                    softKeyboard8.qkCandidatesViewGroup.smallTheCandidate();
                    backReturnState();
                    Global.inLarge = false;
                    Global.refreshState(softKeyboard8);
                } else if (Global.currentKeyboard == Global.KEYBOARD_SYM) {
                    int inputeyboard = PreferenceManager.getDefaultSharedPreferences(context).getString("KEYBOARD_SELECTOR", "2").equals("1") ?
                            Global.KEYBOARD_T9 : Global.KEYBOARD_QP;
                    softKeyboard8.switchKeyboardTo(inputeyboard, true);
                }
            }
            onTouchEffect(view, motionEvent.getAction(),
                    skinInfoManager.skinData.backcolor_touchdown,
                    skinInfoManager.skinData.backcolor_enter
            );
            return false;
        }
    };

    public static void setEnterText(Button ebt, EditorInfo info, int mCurrentKeyboard) {
        String text_next = mCurrentKeyboard == Global.KEYBOARD_EN ? "next" : "下一行";
        String text_send = mCurrentKeyboard == Global.KEYBOARD_EN ? "send" : "发送";
        String text_search = mCurrentKeyboard == Global.KEYBOARD_EN ? "search" : "搜索";
        String text_done = mCurrentKeyboard == Global.KEYBOARD_EN ? "done" : "完成";
        String text_go = mCurrentKeyboard == Global.KEYBOARD_EN ? "go" : "前往";
        String text_default = "\u21b5";
        switch (info.imeOptions) {
            case EditorInfo.IME_ACTION_NEXT:
                ebt.setText(text_next);
                break;
            case EditorInfo.IME_ACTION_SEND:
                ebt.setText(text_send);
                break;
            case EditorInfo.IME_ACTION_SEARCH:
                ebt.setText(text_search);
                break;
            case EditorInfo.IME_ACTION_DONE:
                ebt.setText(text_done);
                break;
            case EditorInfo.IME_ACTION_GO:
                ebt.setText(text_go);
                break;
            default:
                ebt.setText(text_default);
                break;
        }
    }

    private void tool_showAnimation(QuickButton button){
        button.startAnimation(AnimationUtils.loadAnimation(context,R.anim.func_key_1_in));
    }

    public void startShowAnimation(){
        tool_showAnimation(switchKeyboardButton);
        tool_showAnimation(expressionButton);
        tool_showAnimation(spaceButton);
        tool_showAnimation(zeroButton);
        tool_showAnimation(enterButton);
        tool_showAnimation(returnButton);
    }

    private void tool_hideAnimation(QuickButton button){
        Animation anim = AnimationUtils.loadAnimation(context,R.anim.func_key_1_out);
        if(button.isShown()){
            anim.setAnimationListener(getMyAnimationListener(button));
            button.startAnimation(anim);
        }
    }

    public void startHideAnimation() {
        tool_hideAnimation(switchKeyboardButton);
        tool_hideAnimation(expressionButton);
        tool_hideAnimation(spaceButton);
        tool_hideAnimation(zeroButton);
        tool_hideAnimation(enterButton);
        tool_hideAnimation(returnButton);
        switchKeyboardButton.clearAnimation();
        switchKeyboardButton.setVisibility(View.GONE);
    }
}
