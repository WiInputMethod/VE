package com.hit.wi.t9.viewGroups;

import android.content.Context;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;

import com.hit.wi.util.DisplayUtil;
import com.hit.wi.util.ViewsUtil;
import com.hit.wi.jni.Kernel;
import com.hit.wi.util.StringUtil;

import com.hit.wi.t9.R;
import com.hit.wi.t9.SoftKeyboard;
import com.hit.wi.t9.datastruct.InputAction;
import com.hit.wi.t9.values.Global;
import com.hit.wi.t9.view.QuickButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by purebluesong on 2016/2/29.
 */
public class T9InputViewGroup extends NonScrollViewGroup {

    private final float TEXTSIZE_RATE = (float) 0.7;
    private final float BUTTON_WIDTH_RATE = (float) 0.328;

    /**
     * 九键切换出去时的动画资源
     */
    private final int[] keySwitchOut = {
            R.anim.key_1_switch_out,
            R.anim.key_2_switch_out,
            R.anim.key_3_switch_out,
            R.anim.key_4_switch_out,
            R.anim.key_5_switch_out,
            R.anim.key_6_switch_out,
            R.anim.key_7_switch_out,
            R.anim.key_8_switch_out,
            R.anim.key_9_switch_out,
    };


    /**
     * 九键切换回来时的动画资源
     */
    private final int[] keySwitchShow = {
            R.anim.key_1_switch_in,
            R.anim.key_2_switch_in,
            R.anim.key_3_switch_in,
            R.anim.key_4_switch_in,
            R.anim.key_5_switch_in,
            R.anim.key_6_switch_in,
            R.anim.key_7_switch_in,
            R.anim.key_8_switch_in,
            R.anim.key_9_switch_in,
    };

    /**
     * 九键键盘打开时的动画资源
     */
    private final int[] keyShow = {
            R.anim.key_1_in,
            R.anim.key_2_in,
            R.anim.key_3_in,
            R.anim.key_4_in,
            R.anim.key_5_in,
            R.anim.key_6_in,
            R.anim.key_7_in,
            R.anim.key_8_in,
            R.anim.key_9_in,
    };

    /**
     * 九键隐藏时的动画资源
     */
    private final int[] keyHide = {
            R.anim.key_1_out,
            R.anim.key_2_out,
            R.anim.key_3_out,
            R.anim.key_4_out,
            R.anim.key_5_out,
            R.anim.key_6_out,
            R.anim.key_7_out,
            R.anim.key_8_out,
            R.anim.key_9_out,
    };

    private String[] mSlideText;
    private String[] mT9keyText;
    private String[] mNumKeyText;
    private String[] mSymbolKeyText;
    private String[] mSymbolKeySendText;
    private String[] mOtherSymbolTypeList;
    private String[] mOtherSymbolTypeSendKeyList;
    private final int KEY_NUM = 9;
    private final int LAYER_NUM = 3;
    private final int KEY_OTHER_INDEX = 8;

    public QuickButton deleteButton;
    private LinearLayout[] linears = new LinearLayout[LAYER_NUM];
    private LinearLayout.LayoutParams[] linearParams = new LinearLayout.LayoutParams[LAYER_NUM];


    @Override
    public void create(Context context) {
        super.create(context);
        viewGroupWrapper.setOrientation(LinearLayout.VERTICAL);
        mSlideText = res.getStringArray(R.array.KEY_SLIDE_TEXT);
        mT9keyText = res.getStringArray(R.array.KEY_TEXT);
        mNumKeyText = res.getStringArray(R.array.NUM_KEY_TEXT);
        mSymbolKeySendText = res.getStringArray(R.array.KEY_SYMBOL_SEND_TEXT);
        mOtherSymbolTypeList = res.getStringArray(R.array.OTHER_SYMBOL_TEXT);
        mOtherSymbolTypeSendKeyList = res.getStringArray(R.array.OTHER_SYMBOL_SEND_TEXT);
        mSymbolKeyText = res.getStringArray(R.array.KEY_SYMBOL_TEXT);


        int count = 0;
        for (int i = 0; i < LAYER_NUM; ++i) {
            linears[i] = new LinearLayout(context);
            linearParams[i] = new LinearLayout.LayoutParams(0, 0);
            linears[i].setOrientation(LinearLayout.HORIZONTAL);
            for (int j = 0; j < KEY_NUM / LAYER_NUM; j++) {
                QuickButton button = addButton(mT9keyText[count++]);
                linears[i].addView(button, button.itsLayoutParams);
                buttonList.add(button);
            }
            linears[i].setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL);
            viewGroupWrapper.addView(linears[i], linearParams[i]);
        }

        addDeleteButton();
    }

    private void addDeleteButton() {
        deleteButton = super.addButton(res.getString(R.string.delete_text),
                skinInfoManager.skinData.textcolor_delete,
                skinInfoManager.skinData.backcolor_delete);
        deleteButton.setTypeface(softKeyboard.mTypeface);
        LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        deleteButton.itsLayoutParams = deleteParams;
        softKeyboard.secondLayerLayout.addView(deleteButton, deleteParams);
        deleteButton.setOnTouchListener(mDeleteOnTouchListener);
    }

    private QuickButton addButton(String text) {
        QuickButton button = super.addButton(text,
                skinInfoManager.skinData.textcolor_t9keys,
                skinInfoManager.skinData.backcolor_t9keys);
        button.setOnTouchListener(mInputViewOnTouchListener);
        button.itsLayoutParams = new LinearLayout.LayoutParams(0, 0);
        return button;
    }

    public void updateSkin() {
        for (QuickButton button : buttonList) {
            button.setTextColor(skinInfoManager.skinData.textcolor_t9keys);
            ViewsUtil.setBackgroundWithGradientDrawable(button, skinInfoManager.skinData.backcolor_t9keys);
            button.getBackground().setAlpha(Global.getCurrentAlpha());
            button.setShadowLayer(Global.shadowRadius, 0, 0, skinInfoManager.skinData.shadow);
        }

        deleteButton.setTextColor(skinInfoManager.skinData.textcolor_delete);
        ViewsUtil.setBackgroundWithGradientDrawable(deleteButton, skinInfoManager.skinData.backcolor_delete);
        deleteButton.getBackground().setAlpha(Global.getCurrentAlpha());
        deleteButton.setShadowLayer(Global.shadowRadius, 0, 0, skinInfoManager.skinData.shadow);
    }

    public void updateFirstKeyText() {
        if (Global.currentKeyboard == Global.KEYBOARD_T9) {
            buttonList.get(0).setText(Kernel.getWordsShowPinyin() == null || Kernel.getWordsShowPinyin().length() == 0 ? mT9keyText[0] : "'");
        }
    }

    @Override
    public void clearAnimation() {
        super.clearAnimation();
        deleteButton.clearAnimation();
    }

    @Override
    public void setBackgroundAlpha(int alpha) {
        super.setBackgroundAlpha(alpha);
        deleteButton.getBackground().setAlpha(alpha);
    }

    @Override
    public void startAnimation(Animation anim) {
        super.startAnimation(anim);
        deleteButton.startAnimation(anim);
    }

    public void startShowAnimation() {
        int i = 0;
        for (QuickButton button : buttonList) {
            button.startAnimation(AnimationUtils.loadAnimation(context, keyShow[i++]));
            button.setVisibility(View.VISIBLE);
        }
        deleteButton.clearAnimation();
        deleteButton.setVisibility(View.VISIBLE);
    }

    public void startHideAnimation() {
        int i = 0;
        for (final QuickButton button : buttonList) {
            Animation anim = AnimationUtils.loadAnimation(context, keyHide[i++]);
            anim.setAnimationListener(getMyAnimationListener(button));
            if (button.isShown()) button.startAnimation(anim);
        }
        Animation anim = AnimationUtils.loadAnimation(context, keyHide[2]);
        anim.setAnimationListener(getMyAnimationListener(deleteButton));
        if (deleteButton.isShown()) deleteButton.startAnimation(anim);
    }

    public void setSize(int keyboardWidth, int height, int horGap) {
        for (LinearLayout.LayoutParams params : linearParams) {
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = height / LAYER_NUM - 2 * horGap / 3;
            params.bottomMargin = horGap;
        }
        linearParams[2].bottomMargin = 0;
        int margin = horGap / 2;
        int keyWidth = (int) (keyboardWidth * BUTTON_WIDTH_RATE - horGap);
        for (QuickButton button : buttonList) {
            button.itsLayoutParams.width = keyWidth;
            button.itsLayoutParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
            ((LinearLayout.LayoutParams) button.itsLayoutParams).leftMargin = margin;
            ((LinearLayout.LayoutParams) button.itsLayoutParams).rightMargin = margin;
            button.getPaint().setTextSize(DisplayUtil.px2sp(context, Math.min(keyboardWidth / 3 - horGap, height / LAYER_NUM - 2 * horGap / 3) * TEXTSIZE_RATE));
        }

        ((LinearLayout.LayoutParams) deleteButton.itsLayoutParams).leftMargin = horGap;
        ((LinearLayout.LayoutParams) deleteButton.itsLayoutParams).rightMargin = horGap;
        deleteButton.itsLayoutParams.width = keyboardWidth - 3 * horGap - res.getInteger(R.integer.PREEDIT_WIDTH) * keyboardWidth / 100;
        deleteButton.itsLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
    }

    private int lastState = -1;

    public void refreshState() {
        if (lastState == Global.currentKeyboard) return;
        int i = 0;
        if (Global.currentKeyboard == Global.KEYBOARD_T9) {
            if (!isShown()) setVisibility(View.VISIBLE);
            for (QuickButton button : buttonList) {
                button.setOnTouchListener(mInputViewOnTouchListener);
                button.setText(mT9keyText[i++]);
            }
        } else if (Global.currentKeyboard == Global.KEYBOARD_NUM) {
            if (!isShown()) setVisibility(View.VISIBLE);
            for (QuickButton button : buttonList) {
                button.setOnTouchListener(mNumKeyboardOnTouchListener);
                button.setText(mNumKeyText[i++]);
            }
        } else if (Global.currentKeyboard == Global.KEYBOARD_SYM) {
            if (!isShown()) setVisibility(View.VISIBLE);
            for (QuickButton button : buttonList) {
                button.setOnTouchListener(mSymbolKeyOnTouchListener);
                button.setText(mSymbolKeyText[i++]);
            }
        } else {
            if (isShown()) setVisibility(View.GONE);
        }
        lastState = Global.currentKeyboard;
    }

    /**
     * 功能：显示九键
     * 调用时机：切换键盘时调用
     *
     * @param showAnim 是否播放动画
     */
    public void show(boolean showAnim) {
        for (QuickButton button : buttonList) {
            button.setVisibility(View.VISIBLE);
            button.setEnabled(true);
            button.setClickable(true);
        }
        if (showAnim) startAnimation(keySwitchShow);
        deleteButton.setVisibility(View.VISIBLE);
    }

    /**
     * 功能：隐藏九键键盘
     * 调用时机：切换键盘时调用
     *
     * @param showAnim 是否播放动画
     */
    public void hideT9(boolean showAnim) {
        int i = 0;
        for (QuickButton button : buttonList) {
            if (showAnim) {
                button.startAnimation(AnimationUtils.loadAnimation(context, keySwitchOut[i]));
            } else {
                button.clearAnimation();
            }
            button.setVisibility(View.GONE);
            button.setEnabled(false);
            button.setClickable(false);
            i++;
        }
        deleteButton.clearAnimation();
        deleteButton.setVisibility(View.GONE);
    }

    /**
     * 功能：九键之间的切换（九键中文，数字键盘，符号键盘）
     * 调用时机：九键之间切换时
     *
     * @param showAnim 是否播放动画
     */
    public void T9ToNum(boolean showAnim) {
        if (showAnim) {
            final Animation anim = AnimationUtils.loadAnimation(context, R.anim.scale_down);
            final Animation anim2 = AnimationUtils.loadAnimation(context, R.anim.scale_up);
            Animation.AnimationListener animLis = new Animation.AnimationListener() {

                public void onAnimationStart(Animation animation) {

                }

                public void onAnimationRepeat(Animation animation) {

                }

                public void onAnimationEnd(Animation animation) {
                    for (QuickButton button : buttonList) {
                        button.startAnimation(anim2);
                    }
                }
            };
            anim.setAnimationListener(animLis);
            for (QuickButton button : buttonList) {
                button.startAnimation(anim);
            }
        }
    }

    /**
     * 功能：切换出字符键盘显示更多选项后的功能键
     * 调用时机：字符键盘界面按下“其他”或者“emoji”时
     *
     * @param text        要显示的字符串列表
     * @param sendKeyList 要向内核传递的字符
     */
    private void switchSymbolToFunc(String[] text, String[] sendKeyList) {
        softKeyboard.quickSymbolViewGroup.hide();
        softKeyboard.specialSymbolChooseViewGroup.show();
        softKeyboard.specialSymbolChooseViewGroup.setFlagTextandKeys(text, sendKeyList);
    }

    /**
     * 功能：切换回字符键盘显示更多选项后的功能键
     * 调用时机：切换键盘或者按下除“其他”或者“emoji时”调用
     */
    private void switchBackFunc() {
        softKeyboard.quickSymbolViewGroup.setVisibility(View.VISIBLE);
        softKeyboard.specialSymbolChooseViewGroup.hide();
    }

    private void onTouchEffectWithAnim(View v, int action, int backcolor) {
        softKeyboard.keyboardTouchEffect.onTouchEffectWithAnim(v, action,
                skinInfoManager.skinData.backcolor_touchdown,
                backcolor
        );
    }

    /**
     * 功能：监听数字键盘的touch事件
     * 调用时机：touch 数字键盘
     */
    private View.OnTouchListener mNumKeyboardOnTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            softKeyboard.transparencyHandle.handleAlpha(event.getAction());
            if (event.getAction() == MotionEvent.ACTION_UP) {
                softKeyboard.commitText(((QuickButton) v).getText());
            }
            onTouchEffectWithAnim(v, event.getAction(), skinInfoManager.skinData.backcolor_t9keys);
            return true;
        }
    };

    private List<String> cleanFormat(List<String> list) {
        List<String> tmp = new ArrayList<>();
        for (int j=0;j<list.size();j++) {
            String bitch = list.get(j).trim();
            tmp.add(bitch);
        }
        return tmp;
    }


    /**
     * 功能：监听符号键盘的touch 事件
     * 调用时机：touch 符号键盘
     */
    private View.OnTouchListener mSymbolKeyOnTouchListener = new View.OnTouchListener() {

        public boolean onTouch(View v, MotionEvent event) {
            softKeyboard.transparencyHandle.handleAlpha(event.getAction());
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Kernel.cleanKernel();
                int index = buttonList.indexOf(v);
                if (buttonList.indexOf(v) == KEY_OTHER_INDEX) {
                    switchSymbolToFunc(mOtherSymbolTypeList, mOtherSymbolTypeSendKeyList);
                    softKeyboard.candidatesViewGroup.displayCandidates(Global.SYMBOL, cleanFormat(StringUtil.convertStringstoList(softKeyboard.symbolsManager.SPECIAL)), 1000);
                } else if (index == 6) {
                    softKeyboard.candidatesViewGroup.displayCandidates(Global.SYMBOL, cleanFormat(StringUtil.convertStringstoList(softKeyboard.symbolsManager.NUMBER)), 1000);
                    softKeyboard.refreshDisplay(true);
                } else if (index == 7) {
                    softKeyboard.candidatesViewGroup.displayCandidates(Global.SYMBOL, cleanFormat(StringUtil.convertStringstoList(softKeyboard.symbolsManager.MATH)), 1000);
                    softKeyboard.refreshDisplay(true);
                } else {
                    switchBackFunc();
                    softKeyboard.sendMsgToKernel("'" + mSymbolKeySendText[index]);
                }
            }
            onTouchEffectWithAnim(v, event.getAction(), skinInfoManager.skinData.backcolor_t9keys);
            return true;
        }
    };

    /**
     * 功能：监听九键键盘的touch事件
     * 调用时机：touch 九键键盘
     */
    private View.OnTouchListener mInputViewOnTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            softKeyboard.transparencyHandle.handleAlpha(event.getAction());
            String text = "";
            int buttonIndex = buttonList.indexOf(v);
            if (buttonList.size() <= mSlideText.length) {
                text = mSlideText[buttonIndex];
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    softKeyboard.lightViewManager.HideLightView(event.getX(), event.getY(), v.getWidth(), v.getHeight());
                    softKeyboard.lightViewManager.ShowLightView(event.getX(), event.getY(), v.getWidth(), v.getHeight(), text);
                    break;
                case MotionEvent.ACTION_UP:
                    Global.inLarge = false;
                    char ch = (char) ('1' + buttonIndex);
                    String commitText = ch + "";
                    int index = softKeyboard.lightViewManager.lightViewAnimate(v, event);
                    int[] follow = {1, 3, 0, 2};//对应于左上下右，因为三个字符串分别要这么对应，所以需要表驱动
                    if (index > 0 && text.length() > follow[index])
                        commitText = text.substring(follow[index], follow[index] + 1);
                    if (buttonIndex == 0) {
                        if (Kernel.getWordsNumber() > 0) {
                            if (Global.isInView(v, event)) {
                                softKeyboard.sendMsgToKernel("'");
                            } else {
                                softKeyboard.chooseWord(0);
                                softKeyboard.commitText(commitText);
                            }
                        } else {
                            if (commitText.equals("1")) commitText = ",";
                            softKeyboard.commitText(commitText);
                        }
                        softKeyboard.prefixViewGroup.setVisibility(View.GONE);
                    } else {
                        Global.redoText_single.clear();
                        softKeyboard.sendMsgToKernel(commitText);
                    }
                    Global.keyboardRestTimeCount = 0;
//                    softKeyboard.mHandler.removeMessages(softKeyboard.MSG_DOUBLE_CLICK_REFRESH);
//                    softKeyboard.mHandler.sendEmptyMessageDelayed(softKeyboard.MSG_DOUBLE_CLICK_REFRESH,Global.metaRefreshTime);
                    break;
            }
            onTouchEffectWithAnim(v, event.getAction(), skinInfoManager.skinData.backcolor_t9keys);
            return true;
        }
    };

    private View.OnTouchListener mDeleteOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            softKeyboard.transparencyHandle.handleAlpha(event.getAction());
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                softKeyboard.mHandler.sendEmptyMessageDelayed(softKeyboard.MSG_REPEAT, SoftKeyboard.REPEAT_START_DELAY);
            } else if (event.getAction() == MotionEvent.ACTION_MOVE && Global.slideDeleteSwitch) {
                softKeyboard.lightViewManager.HideLightView(event.getX(), event.getY(), v.getWidth(), v.getHeight());
                if (event.getX() < 0) {
                    softKeyboard.lightViewManager.ShowLightView(event.getX(), event.getY(), v.getWidth(), v.getHeight(), "清空");
                    softKeyboard.mHandler.removeMessages(softKeyboard.MSG_REPEAT);
                }
                if (event.getY() < 0) {
                    softKeyboard.lightViewManager.ShowLightView(event.getX(), event.getY(), v.getWidth(), v.getHeight(), "恢复");
                    softKeyboard.mHandler.removeMessages(softKeyboard.MSG_REPEAT);
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                softKeyboard.mHandler.removeMessages(softKeyboard.MSG_REPEAT);
                softKeyboard.lightViewManager.lightViewAnimate(v, event);
                if (event.getX() < 0 && Global.slideDeleteSwitch) {
                    softKeyboard.deleteAll();
                } else if (event.getY() < 0) {
                    if (Global.redoTextForDeleteAll != "") {
                        softKeyboard.commitText(Global.redoTextForDeleteAll);
                        Global.redoTextForDeleteAll = "";
                    }
                    if (!Global.redoTextForDeleteAll_preedit.equals("")) {
                        softKeyboard.sendMsgToKernel(Global.redoTextForDeleteAll_preedit);
                        Global.redoTextForDeleteAll_preedit = "";
                    } else {
                        if (Global.redoText_single.size() > 0) {
                            InputAction ia = Global.redoText_single.pop();
                            if (ia.Type == InputAction.TEXT_TO_KERNEL) {
                                softKeyboard.sendMsgToKernel(ia.text.toString());
                            } else {
                                softKeyboard.commitText(ia.text.toString());
                            }
                        }
                    }
                } else {
                    softKeyboard.deleteLast();
                }
            }
            onTouchEffectWithAnim(v, event.getAction(), skinInfoManager.skinData.backcolor_delete);
            return false;
        }
    };
}
