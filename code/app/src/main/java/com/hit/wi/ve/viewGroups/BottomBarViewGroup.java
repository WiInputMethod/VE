package com.hit.wi.ve.viewGroups;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import com.hit.wi.jni.Kernel;

import com.hit.wi.util.CommonFuncs;
import com.hit.wi.util.InputMode;
import com.hit.wi.util.ViewsUtil;
import com.hit.wi.util.StringUtil;

import com.hit.wi.ve.R;
import com.hit.wi.ve.values.Global;
import com.hit.wi.ve.view.QuickButton;

import java.util.*;

/**
 * 底部的操作栏
 * Created by Administrator on 2015/9/17.
 */
public class BottomBarViewGroup extends NonScrollViewGroup {
    private final int KEYBOARD_BUTTON_NUM = 4;

    public QuickButton switchKeyboardButton;
    public QuickButton expressionButton;
    public QuickButton spaceButton;
    public QuickButton enterButton;
    public QuickButton zeroButton;
    public QuickButton returnButton;

    public QuickButton keyboardButtonQK;
    public QuickButton keyboardButtonEn;
    public QuickButton keyboardButtonT9;
    public QuickButton keyboardButtonNum;

    String[] button_text;

    private String spaceText;
    private Context mContext;

    List<QuickButton> keyboardButtons = new ArrayList<>();//不能用数组，数组会复制每个对象，而不是引用
    List<QuickButton> allButtons ;


    private final String text_return = "返回";
    private final String text_qk = "全键";
    private final String text_t9 = "九键";
    private final String text_num = "数字";
    private final String text_en = "英文";

    public void create(Context context) {
        super.create(context);
        mContext = context;
        button_text = res.getStringArray(R.array.BOTTOMBAR_TEXT);
        spaceText = InputMode.fullToHalf(PreferenceManager.getDefaultSharedPreferences(context).getString("ZH_SPACE_TEXT", button_text[2]));
        initButtons();
    }

    private void initButtons() {
        switchKeyboardButton = addButton(button_text[0]);
        expressionButton = addButton(button_text[1]);
        spaceButton = addButton(spaceText);
        enterButton = addButton(button_text[3]);
        zeroButton = addButton("0");
        returnButton = addButton(text_return);

        keyboardButtonQK = addButton(text_qk);
        keyboardButtonEn = addButton(text_en);
        keyboardButtonNum = addButton(text_num);
        keyboardButtonT9 = addButton(text_t9);

        switchKeyboardButton.setOnTouchListener(switchKeyOnTouchListener);
        expressionButton.setOnTouchListener(expressionOnTouchListener);
        spaceButton.setOnTouchListener(spaceOnTouchListener);
        enterButton.setOnTouchListener(enterOnTouchListener);
        zeroButton.setOnTouchListener(zeroOnTouchListener);
        returnButton.setOnTouchListener(returnOnTouchListener);

        keyboardButtonQK.setOnTouchListener(keyboardSwitchKeyOnTouchListenerFactory(Global.KEYBOARD_QK));
        keyboardButtonEn.setOnTouchListener(keyboardSwitchKeyOnTouchListenerFactory(Global.KEYBOARD_EN));
        keyboardButtonNum.setOnTouchListener(keyboardSwitchKeyOnTouchListenerFactory(Global.KEYBOARD_NUM));
        keyboardButtonT9.setOnTouchListener(keyboardSwitchKeyOnTouchListenerFactory(Global.KEYBOARD_T9));

        buttonList.add(switchKeyboardButton);
        buttonList.add(expressionButton);
        buttonList.add(spaceButton);
        buttonList.add(enterButton);
        buttonList.add(zeroButton);
        buttonList.add(returnButton);

        buttonList.add(keyboardButtonQK);
        buttonList.add(keyboardButtonEn);
        buttonList.add(keyboardButtonNum);
        buttonList.add(keyboardButtonT9);

        allButtons = new ArrayList<>(buttonList);
        keyboardButtons.add(keyboardButtonQK);
        keyboardButtons.add(keyboardButtonEn);
        keyboardButtons.add(keyboardButtonNum);
        keyboardButtons.add(keyboardButtonT9);

        returnButton.setVisibility(View.GONE);
        zeroButton.setVisibility(View.GONE);
    }

    public QuickButton addButton(String text) {
        QuickButton button = super.addButton(text,
                skinInfoManager.skinData.textcolor_26keys,
                skinInfoManager.skinData.backcolor_26keys
        );

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        buttonParams.leftMargin = padding;

        button.itsLayoutParams = buttonParams;
        viewGroupWrapper.addView(button, buttonParams);
        return button;
    }

    private void moveButtonToPosition(QuickButton button,int position) {
        viewGroupWrapper.removeView(button);
        viewGroupWrapper.addView(button,position,button.itsLayoutParams);
        buttonList.remove(button);
        buttonList.add(position,button);
    }

    public void intoReturnState () {
        setShownButton(expressionButton,spaceButton,enterButton,returnButton);
    }

    public void backReturnState () {
        refreshState();
    }

    public void setShownButton(QuickButton... buttons) {
        for (QuickButton button:allButtons){
            button.setVisibility(View.GONE);
        }
        buttonList.clear();
        for (int i=0;i<buttons.length;i++) {
            buttonList.add(buttons[i]);
            if (viewGroupWrapper.getChildAt(i) != buttons[i]){
                moveButtonToPosition(buttons[i],i);
            }
        }
        setVisibility(View.VISIBLE);
    }

    private void writeKeyboardToSharedPreference(int keyboard) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        switch (keyboard) {
            case Global.KEYBOARD_T9:
                editor.putString("KEYBOARD_SELECTOR", "1");
                break;
            case Global.KEYBOARD_QK:
                editor.putString("KEYBOARD_SELECTOR", "2");
                break;
        }
        editor.commit();
    }

    public void refreshState() {
        expressionButton.setText(button_text[1]);
        if (Global.inLarge){
            setShownButton(switchKeyboardButton,expressionButton,spaceButton,enterButton);
        } else {
            switch (Global.currentKeyboard) {
                case Global.KEYBOARD_T9:
                case Global.KEYBOARD_QK:
                case Global.KEYBOARD_EN:
                    setShownButton(switchKeyboardButton,expressionButton,spaceButton,enterButton);
                    setButtonWidthByRate(res.getIntArray(R.array.BOTTOMBAR_KEY_WIDTH));
                    writeKeyboardToSharedPreference(Global.currentKeyboard);
                    break;
                case Global.KEYBOARD_SYM:
                    setShownButton(switchKeyboardButton,expressionButton,spaceButton,returnButton);
                    setButtonWidthByRate(res.getIntArray(R.array.BOTTOMBAR_KEY_WIDTH));
                    break;
                case Global.KEYBOARD_NUM:
                    setShownButton(switchKeyboardButton,zeroButton,spaceButton);
                    setButtonWidthByRate(res.getIntArray(R.array.BOTTOMBAR_NUM_KEY_WIDTH));
                    break;
            }
        }
    }

    public void setTypeFace(Typeface typeFace) {
        for (QuickButton button : buttonList) {
            button.setTypeface(typeFace);
        }
    }

    private void tool_updateSkin(QuickButton button,int textColor,int backgroundColor) {
        button.setTextColor(textColor);
        button.getBackground().setAlpha(Global.getCurrentAlpha());
        ViewsUtil.setBackgroundWithGradientDrawable(button,backgroundColor);
    }

    public void updateSkin() {
        tool_updateSkin(switchKeyboardButton,
                skinInfoManager.skinData.textcolor_enter,
                skinInfoManager.skinData.backcolor_enter
        );
        tool_updateSkin(expressionButton,
                skinInfoManager.skinData.textcolor_space,
                skinInfoManager.skinData.backcolor_space
        );
        tool_updateSkin(enterButton,
                skinInfoManager.skinData.textcolor_enter,
                skinInfoManager.skinData.backcolor_enter
        );
        tool_updateSkin(spaceButton,
                skinInfoManager.skinData.textcolor_space,
                skinInfoManager.skinData.backcolor_space
        );
        tool_updateSkin(zeroButton,
                skinInfoManager.skinData.textcolor_zero,
                skinInfoManager.skinData.backcolor_zero
        );
        tool_updateSkin(returnButton,
                skinInfoManager.skinData.textcolor_enter,
                skinInfoManager.skinData.backcolor_enter
        );
        tool_updateSkin(keyboardButtonQK,
                skinInfoManager.skinData.textcolor_26keys,
                skinInfoManager.skinData.backcolor_26keys
        );
        tool_updateSkin(keyboardButtonEn,
                skinInfoManager.skinData.textcolor_26keys,
                skinInfoManager.skinData.backcolor_26keys
        );
        tool_updateSkin(keyboardButtonNum,
                skinInfoManager.skinData.textcolor_26keys,
                skinInfoManager.skinData.backcolor_26keys
        );
        tool_updateSkin(keyboardButtonT9,
                skinInfoManager.skinData.textcolor_26keys,
                skinInfoManager.skinData.backcolor_26keys
        );
    }

    private void onTouchEffect(View view, int action, int touchdownColor, int normalColor) {
        softKeyboard.transparencyHandle.handleAlpha(action);
        softKeyboard.keyboardTouchEffect.onTouchEffectWithAnim(view, action, touchdownColor, normalColor);
    }

    private void setSpaceText() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        if (Global.currentKeyboard == Global.KEYBOARD_EN) {
            spaceButton.setText(sp.getString("EN_SPACE_TEXT", "space"));
        } else if (Global.currentKeyboard == Global.KEYBOARD_QK) {
            spaceButton.setText(sp.getString("ZH_SPACE_TEXT", "空格"));
        }
    }

    @Override
    public void setButtonAlpha(float alpha) {
        for (QuickButton button:allButtons) {
            button.setAlpha(alpha);
        }
    }

    private boolean isSwitchState = false;
    private View.OnTouchListener switchKeyOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int position = (int)((event.getRawX() - softKeyboard.keyboardParams.x) / (softKeyboard.keyboardParams.width/(KEYBOARD_BUTTON_NUM+1))) % (KEYBOARD_BUTTON_NUM+1);
            if (!Global.inLarge){
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isSwitchState = keyboardButtonQK.isShown();
                        setShownButton(switchKeyboardButton,keyboardButtonQK,keyboardButtonEn,keyboardButtonNum,keyboardButtonT9);
                        setButtonWidth(paramsForViewGroup.width / (KEYBOARD_BUTTON_NUM+1) - padding);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        setBackgroundColor(skinInfoManager.skinData.backcolor_26keys);
                        if (position>0)
                            ViewsUtil.setBackgroundWithGradientDrawable(keyboardButtons.get(position-1),skinInfoManager.skinData.backcolor_touchdown);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (position != 0){
                            int[] keyboards = {Global.currentKeyboard, Global.KEYBOARD_QK, Global.KEYBOARD_EN, Global.KEYBOARD_NUM, Global.KEYBOARD_T9};
                            softKeyboard.switchKeyboardTo(keyboards[position], true);
                            setEnterText(softKeyboard.getCurrentInputEditorInfo(), keyboards[position]);
                            setSpaceText();
                            updateSkin();
                        } else if (isSwitchState) {
                            refreshState();
                        }
                        break;
                }
            }
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
            expression = StringUtil.convertStringstoList(softKeyboard.symbolsManager.EmojiFace);
        } else if (flag == 1){
            expression = StringUtil.convertStringstoList(softKeyboard.symbolsManager.SMILE);
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
                    Kernel.cleanKernel();
                    List<String> expressions = getExpression(expressionFlag);
                    if(expressions != Collections.EMPTY_LIST){
                        softKeyboard.candidatesViewGroup.displayCandidates(Global.SYMBOL,expressions, Global.inLarge?200:9);
                        if (Global.inLarge) softKeyboard.candidatesViewGroup.largeTheCandidate();
                        softKeyboard.refreshDisplay(true);
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
                softKeyboard.lightViewManager.lightViewAnimate(v,event);
                if (Kernel.getWordsNumber()>0 && Global.currentKeyboard != Global.KEYBOARD_SYM) {
                    softKeyboard.chooseWord(0);
                } else{
                    int index = ViewsUtil.computeDirection(event.getX(), event.getY(), v.getHeight(), v.getWidth());
                    if (index == Global.ERR)index = 4;
                    softKeyboard.commitText(commit_text_space[index]);
                }

                if (Global.inLarge && softKeyboard.candidatesViewGroup.isShown()) {
                    softKeyboard.candidatesViewGroup.largeTheCandidate();
                } else {
                    softKeyboard.refreshDisplay();
                    backReturnState();
                }
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                softKeyboard.lightViewManager.HideLightView(event.getX(),event.getY(),v.getWidth(),v.getHeight());
                int index = ViewsUtil.computeDirection(event.getX(), event.getY(), v.getHeight(), v.getWidth());
                if (index == Global.ERR)index = 4;
                softKeyboard.lightViewManager.ShowLightView(event.getX(),event.getY(),v.getWidth(),v.getHeight(),commit_text_space[index]);
            }
            onTouchEffect(v, event.getAction(), skinInfoManager.skinData.backcolor_touchdown, skinInfoManager.skinData.backcolor_space);
            return false;
        }
    };

    private View.OnTouchListener enterOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (Kernel.getWordsNumber()>0) {
                    if (event.getY() > 0) {
                        softKeyboard.commitText(Kernel.returnAction());
                    }
                    softKeyboard.t9InputViewGroup.updateFirstKeyText();
                } else {
                    if (!softKeyboard.sendDefaultEditorAction(true) && Global.isInView(v,event)) {
                        softKeyboard.sendDownUpKeyEvents(KeyEvent.KEYCODE_ENTER);
                    }
                }
                softKeyboard.candidatesViewGroup.smallTheCandidate();
                backReturnState();
                Global.inLarge = false;
                Global.refreshState(softKeyboard);
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
                softKeyboard.commitText("0");
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
                    softKeyboard.candidatesViewGroup.smallTheCandidate();
                    backReturnState();
                    Global.inLarge = false;
                    Global.refreshState(softKeyboard);
                } else if (Global.currentKeyboard == Global.KEYBOARD_SYM) {
                    int inputKeyboard = PreferenceManager.getDefaultSharedPreferences(context).getString("KEYBOARD_SELECTOR", "2").equals("1") ?
                            Global.KEYBOARD_T9 : Global.KEYBOARD_QK;
                    softKeyboard.switchKeyboardTo(inputKeyboard, true);
                }
            }
            onTouchEffect(view, motionEvent.getAction(),
                    skinInfoManager.skinData.backcolor_touchdown,
                    skinInfoManager.skinData.backcolor_enter
            );
            return false;
        }
    };


    private View.OnTouchListener keyboardSwitchKeyOnTouchListenerFactory(final int keyboard){
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP)
                            softKeyboard.switchKeyboardTo(keyboard ,true);
                onTouchEffect(v, event.getAction(),
                        skinInfoManager.skinData.backcolor_touchdown,
                        skinInfoManager.skinData.backcolor_26keys
                );
                return false;
            }
        };
    }


    public void setEnterText(EditorInfo info, int mCurrentKeyboard) {
        Boolean isEn =  mCurrentKeyboard == Global.KEYBOARD_EN ;
        String[] text_CH = {"下一行", "发送", "搜索", "完成", "前往", };
        String[] text_EN = {"next", "send", "search", "done", "go", };
        String textDefault = "\u21b5";
        final int[] actionCases = {
                EditorInfo.IME_ACTION_NEXT,
                EditorInfo.IME_ACTION_SEND,
                EditorInfo.IME_ACTION_SEARCH,
                EditorInfo.IME_ACTION_DONE,
                EditorInfo.IME_ACTION_GO,
        };
        for(int i=0;i<actionCases.length;i++){
            if (actionCases[i] == info.imeOptions){
                enterButton.setText(isEn?text_EN[i]:text_CH[i]);
                return;
            }
        }
        enterButton.setText(textDefault);
    }

    private void tool_showAnimation(QuickButton button){
        button.startAnimation(AnimationUtils.loadAnimation(context,R.anim.func_key_1_in));
    }

    public void startShowAnimation(){
        tool_showAnimation(switchKeyboardButton);
        tool_showAnimation(expressionButton);
        tool_showAnimation(spaceButton);
        tool_showAnimation(enterButton);
    }

    private void tool_hideAnimation(QuickButton button){
        Animation anim = AnimationUtils.loadAnimation(context,R.anim.func_key_1_out);
        if(button.isShown()){
            button.startAnimation(anim);
        }
    }

    @Override
    public void clearAnimation(){
        for (QuickButton button : allButtons) {
            button.clearAnimation();
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
