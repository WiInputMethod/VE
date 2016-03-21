package com.hit.wi.t9.viewGroups;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.hit.wi.jni.WIInputMethod;
import com.hit.wi.t9.R;
import com.hit.wi.t9.datastruct.InputAction;
import com.hit.wi.t9.functions.PredictManager;
import com.hit.wi.t9.values.Global;
import com.hit.wi.t9.view.QuickButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zrp on 2016/2/26.
 */
public class QKInputViewGroups extends NonScrollViewGroup {


    /**
     * 英文显示时的动画资源
     */
    int[] enShow = {
            R.anim.en_key_q_switch_in,
            R.anim.en_key_w_switch_in,
            R.anim.en_key_e_switch_in,
            R.anim.en_key_r_switch_in,
            R.anim.en_key_t_switch_in,
            R.anim.en_key_y_switch_in,
            R.anim.en_key_u_switch_in,
            R.anim.en_key_i_switch_in,
            R.anim.en_key_o_switch_in,
            R.anim.en_key_p_switch_in,
            R.anim.en_key_a_switch_in,
            R.anim.en_key_s_switch_in,
            R.anim.en_key_d_switch_in,
            R.anim.en_key_f_switch_in,
            R.anim.en_key_g_switch_in,
            R.anim.en_key_h_switch_in,
            R.anim.en_key_j_switch_in,
            R.anim.en_key_k_switch_in,
            R.anim.en_key_l_switch_in,
            R.anim.en_key_z_switch_in,
            R.anim.en_key_x_switch_in,
            R.anim.en_key_c_switch_in,
            R.anim.en_key_v_switch_in,
            R.anim.en_key_b_switch_in,
            R.anim.en_key_n_switch_in,
            R.anim.en_key_m_switch_in,
    };

    /**
     * 英文隐藏时的动画资源
     */
    int[] enHide = {
            R.anim.en_key_q_switch_out,
            R.anim.en_key_w_switch_out,
            R.anim.en_key_e_switch_out,
            R.anim.en_key_r_switch_out,
            R.anim.en_key_t_switch_out,
            R.anim.en_key_y_switch_out,
            R.anim.en_key_u_switch_out,
            R.anim.en_key_i_switch_out,
            R.anim.en_key_o_switch_out,
            R.anim.en_key_p_switch_out,
            R.anim.en_key_a_switch_out,
            R.anim.en_key_s_switch_out,
            R.anim.en_key_d_switch_out,
            R.anim.en_key_f_switch_out,
            R.anim.en_key_g_switch_out,
            R.anim.en_key_h_switch_out,
            R.anim.en_key_j_switch_out,
            R.anim.en_key_k_switch_out,
            R.anim.en_key_l_switch_out,
            R.anim.en_key_z_switch_out,
            R.anim.en_key_x_switch_out,
            R.anim.en_key_c_switch_out,
            R.anim.en_key_v_switch_out,
            R.anim.en_key_b_switch_out,
            R.anim.en_key_n_switch_out,
            R.anim.en_key_m_switch_out,
    };

    private String[] enKeyText;
    private int EN_KEY_TEXT_SIZE = 80;
    private int SMILE_KEYS_NUM = 4;
    private int[] linear_keys_num = {10,9,7};
    private String[] shiftText;
    private String smileText;
    public boolean mShiftOn = false;

    private LinearLayout[] linears = new LinearLayout[3];
    private LinearLayout.LayoutParams[] linearsParams = new LinearLayout.LayoutParams[3];
    private PredictManager predictManager  = new PredictManager();
    public QuickButton shiftButton ;
    public QuickButton smileButton ;
    public QuickButton deleteButton;
    public List<LinearLayout> buttonList;
    private String[] mAllSmileText;

    @Override
    public void create(Context context) {
        super.create(context);
        viewGroupWrapper.setOrientation(LinearLayout.VERTICAL);
        enKeyText = res.getStringArray(R.array.EN_KEY_TEXT);
        shiftText = res.getStringArray(R.array.EN_SHIFT_AND_SYM_KEY_TEXT);
        smileText = res.getString(R.string.smile_key_text);
        mAllSmileText = res.getStringArray(R.array.ALL_SMILE_VIEW_TEXT);
        predictManager.init(context);
        buttonList = new ArrayList<>();

        int count = 0;
        for(int i=0;i<3;i++){
            linears[i] = new LinearLayout(context);
            linearsParams[i] = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0);
            linears[i].setOrientation(LinearLayout.HORIZONTAL);
            linears[i].setGravity(Gravity.CENTER);
            for (int j=0;j<linear_keys_num[i];j++){
                LinearLayout button = addButton(enKeyText[count],predictManager.qkPredict[count]);
                button.setOnTouchListener(qkInputOnTouchListener);
                buttonList.add(button);
                linears[i].addView(button);
                count++;
            }
            viewGroupWrapper.addView(linears[i],linearsParams[i]);
        }
        addShiftButton();
        addSmileButton();
        addDeleteButton();
    }

    private void addShiftButton(){
        shiftButton = super.addButton(shiftText[0],
                skinInfoManager.skinData.textcolors_shift,
                skinInfoManager.skinData.backcolor_shift);
        shiftButton.setOnTouchListener(mShiftKeyOnTouchListener);
        LinearLayout.LayoutParams shiftparams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        shiftButton.itsLayoutParams = shiftparams;
        linears[2].addView(shiftButton,0);
    }

    private void addSmileButton(){
        smileButton = super.addButton(smileText,
                skinInfoManager.skinData.textcolors_shift,
                skinInfoManager.skinData.backcolor_shift);
        smileButton.setOnTouchListener(mSmileKeyOnTouchListener);
        LinearLayout.LayoutParams smileparams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        smileButton.itsLayoutParams = smileparams;
        linears[2].addView(smileButton,1);
    }

    private void addDeleteButton(){
        deleteButton = super.addButton(res.getString(R.string.delete_text),
                skinInfoManager.skinData.textcolors_delete,
                skinInfoManager.skinData.backcolor_delete
        );
        deleteButton.setOnTouchListener(mDeleteOnTouchListener);
        LinearLayout.LayoutParams deleteparams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        deleteButton.itsLayoutParams = deleteparams;
        linears[2].addView(deleteButton);
    }

    public void reloadPredictText(int keyboard) {
        predictManager.refresh(context);
        switch (keyboard) {
            case Global.KEYBOARD_QP:
                int i=0;
                for (LinearLayout button : buttonList) {
                    ((TextView) button.findViewById(R.id.predict_text))
                            .setText(Global.halfToFull(predictManager.qkPredict[i++]));
                }
                break;
            default:
                for (LinearLayout button : buttonList) {
                    ((TextView) button.findViewById(R.id.predict_text)).setText("");
                }
                break;
        }
    }

    public void toolfunc_refreshQKKeyboardPredict(String text, LinearLayout button, String predictText) {
        text = text.length()<1 ? Global.currentKeyboard == Global.KEYBOARD_EN ? "" : predictText:text;
        ((TextView) button.findViewById(R.id.predict_text)).setText(Global.halfToFull(text));
    }

    public void refreshQKKeyboardPredict() {
        toolfunc_refreshQKKeyboardPredict(WIInputMethod.GetPredictA(),
                buttonList.get(10),predictManager.qkPredict[10]);

        toolfunc_refreshQKKeyboardPredict(WIInputMethod.GetPredictE(),
                buttonList.get(2),predictManager.qkPredict[2]);

        toolfunc_refreshQKKeyboardPredict(WIInputMethod.GetPredictI(),
                buttonList.get(7),predictManager.qkPredict[7]);

        toolfunc_refreshQKKeyboardPredict(WIInputMethod.GetPredictO(),
                buttonList.get(8),predictManager.qkPredict[8]);

        toolfunc_refreshQKKeyboardPredict(WIInputMethod.GetPredictU(),
                buttonList.get(6),predictManager.qkPredict[6]);

        toolfunc_refreshQKKeyboardPredict(WIInputMethod.GetPredictH(),
                buttonList.get(15),predictManager.qkPredict[15]);
    }

    public void setSize(int width,int height,int horGap){
        int keyWidth = (width-2*horGap)/linear_keys_num[0];
        int keyHeight = height / 3;
        for (int j = 0; j < 3; ++j) {
            linearsParams[j].height = keyHeight;
            linearsParams[j].leftMargin = horGap;
            linearsParams[j].rightMargin = horGap;
        }
        int padding = horGap/2;
        for (LinearLayout button:buttonList){
            button.getLayoutParams().width = keyWidth;
            button.setPadding(padding,padding,padding,padding);
            ((TextView)button.findViewById(R.id.main_text)).getPaint().setTextSize(80 * Math.min(keyWidth, keyHeight * 7/9)/100);
            ((TextView)button.findViewById(R.id.predict_text)).getPaint().setTextSize(80 * Math.min(keyWidth, keyHeight * 7/9)/300);
            ((LinearLayout)button.getParent()).updateViewLayout(button,button.getLayoutParams());
        }

        shiftButton.itsLayoutParams.width = keyWidth * 3 / 2 ;
        ((LinearLayout.LayoutParams)shiftButton.itsLayoutParams).topMargin = padding;
        ((LinearLayout.LayoutParams)shiftButton.itsLayoutParams).bottomMargin = padding;
        ((LinearLayout.LayoutParams)shiftButton.itsLayoutParams).rightMargin = padding;
        linears[2].updateViewLayout(shiftButton,shiftButton.itsLayoutParams);

        smileButton.itsLayoutParams.width = keyWidth * 3 / 2 ;
        ((LinearLayout.LayoutParams)smileButton.itsLayoutParams).topMargin = padding;
        ((LinearLayout.LayoutParams)smileButton.itsLayoutParams).bottomMargin = padding;
        ((LinearLayout.LayoutParams)smileButton.itsLayoutParams).rightMargin = padding;
        linears[2].updateViewLayout(smileButton,smileButton.itsLayoutParams);

        deleteButton.itsLayoutParams.width = keyWidth * 3 / 2;
        ((LinearLayout.LayoutParams)deleteButton.itsLayoutParams).topMargin = padding;
        ((LinearLayout.LayoutParams)deleteButton.itsLayoutParams).bottomMargin = padding;
        ((LinearLayout.LayoutParams)deleteButton.itsLayoutParams).leftMargin = padding;
        deleteButton.getPaint().setTextSize(3 * Math.min(keyWidth *3/2,keyHeight)/5);
        linears[2].updateViewLayout(deleteButton,deleteButton.itsLayoutParams);
    }

    public void setVisibility(int visibility){
        for(LinearLayout button :buttonList){
            button.clearAnimation();
            button.setVisibility(visibility);
        }
        deleteButton.clearAnimation();
        deleteButton.setVisibility(visibility);
    }

    public boolean isShown(){
        boolean shown = false;
        for (LinearLayout layout:buttonList){
            shown |= layout.isShown();
        }
        return shown;
    }

    public void refreshState(){
        if (Global.currentKeyboard == Global.KEYBOARD_QP){
            if(!isShown())setVisibility(View.VISIBLE);
            smileButton.clearAnimation();
            smileButton.setVisibility(View.VISIBLE);
            shiftButton.clearAnimation();//不然后面的View。Gone会不起作用，因为前面用过动画了，所以得清空一下
            shiftButton.setVisibility(View.GONE);
            for (LinearLayout button:buttonList){
                button.setOnTouchListener(qkInputOnTouchListener);
            }
        } else if(Global.currentKeyboard == Global.KEYBOARD_EN){
            if(!isShown())setVisibility(View.VISIBLE);
            shiftButton.clearAnimation();
            shiftButton.setVisibility(View.VISIBLE);
            smileButton.clearAnimation();//不然后面的View。Gone会不起作用，因为前面用过动画了，所以得清空一下
            smileButton.setVisibility(View.GONE);
            for (LinearLayout button:buttonList){
                button.setOnTouchListener(enInputOnTouchListener);
            }
        } else {
            setVisibility(View.GONE);
        }
    }

    public void tool_updateSkin(TextView v, int textcolor, int backgroundcolor) {
        v.setTextColor(textcolor);
        v.setBackgroundColor(backgroundcolor);
        v.getBackground().setAlpha((int) (Global.mCurrentAlpha * 255));
        v.setShadowLayer(Global.shadowRadius,0,0,skinInfoManager.skinData.shadow);
    }

    public void updateSkin(){
        for (LinearLayout button :buttonList) {
            TextView main_text = ((TextView) button.findViewById(R.id.main_text));
            TextView predict_text = ((TextView) button.findViewById(R.id.predict_text));
            main_text.setTextColor(skinInfoManager.skinData.textcolors_26keys);
            main_text.setShadowLayer(Global.shadowRadius,0,0,skinInfoManager.skinData.shadow);
            main_text.setBackgroundColor(skinInfoManager.skinData.backcolor_26keys);
            main_text.getBackground().setAlpha((int) (Global.mCurrentAlpha*255));
            predict_text.setTextColor(skinInfoManager.skinData.textcolors_26keys);
            predict_text.setShadowLayer(Global.shadowRadius,0,0,skinInfoManager.skinData.shadow);
            predict_text.setBackgroundColor(skinInfoManager.skinData.backcolor_26keys);
            predict_text.getBackground().setAlpha((int) (Global.mCurrentAlpha * 255)); //设置透明度
        }

        tool_updateSkin(shiftButton,
                skinInfoManager.skinData.textcolors_shift,
                skinInfoManager.skinData.backcolor_shift
        );
        tool_updateSkin(smileButton,
                skinInfoManager.skinData.textcolors_shift,
                skinInfoManager.skinData.backcolor_shift
        );
        tool_updateSkin(deleteButton,
                skinInfoManager.skinData.textcolors_26keys,
                skinInfoManager.skinData.backcolor_delete
        );
    }

    public void startHideAnimation(){
        startHideAnimation(true);
    }

    public void startHideAnimation(boolean show){
        if(show){
            shiftButton.clearAnimation();
            smileButton.clearAnimation();
            Animation anim = AnimationUtils.loadAnimation(context, enHide[19]);
            if (Global.currentKeyboard == Global.KEYBOARD_QP){
                anim.setAnimationListener(getMyAnimationListener(smileButton));
                smileButton.startAnimation(anim);
                shiftButton.setVisibility(View.GONE);
            } else if (Global.currentKeyboard == Global.KEYBOARD_EN){
                anim.setAnimationListener(getMyAnimationListener(shiftButton));
                shiftButton.startAnimation(anim);
                smileButton.setVisibility(View.GONE);
            }
        }
        deleteButton.setVisibility(View.GONE);
        int i = 0;
        for (LinearLayout button :buttonList) {
            if(show) {
                Animation anim = AnimationUtils.loadAnimation(context, enHide[i++]);
                anim.setAnimationListener(getMyAnimationListener(button));
                button.startAnimation(anim);
            } else {
                button.setVisibility(View.GONE);
            }
        }
    }

    public void startShowAnimation(boolean show){
        int i = 0;
        clearAnimation();
        for (LinearLayout button : buttonList) {
            button.setVisibility(View.VISIBLE);
            if(show)button.startAnimation(AnimationUtils.loadAnimation(context, enShow[i++]));
        }
        shiftButton.clearAnimation();
        smileButton.clearAnimation();
        if (Global.currentKeyboard == Global.KEYBOARD_EN) {
            shiftButton.setVisibility(View.VISIBLE);
            smileButton.setVisibility(View.GONE);
            if(show)shiftButton.startAnimation(AnimationUtils.loadAnimation(context, enShow[19]));
        } else if (Global.currentKeyboard == Global.KEYBOARD_QP) {
            smileButton.setVisibility(View.VISIBLE);
            shiftButton.setVisibility(View.GONE);
            if(show)smileButton.startAnimation(AnimationUtils.loadAnimation(context, enShow[19]));
        }
        deleteButton.clearAnimation();
        deleteButton.setVisibility(View.VISIBLE);
        if(show)deleteButton.startAnimation(AnimationUtils.loadAnimation(context, enShow[25]));
    }

    public void startShowAnimation(){
        startShowAnimation(true);
    }

    public void startAnimation(Animation anim){
        for (LinearLayout button:buttonList){
            button.startAnimation(anim);
        }
        if (Global.currentKeyboard == Global.KEYBOARD_EN) {
            shiftButton.startAnimation(anim);
        } else if (Global.currentKeyboard == Global.KEYBOARD_QP) {
            smileButton.startAnimation(anim);
        }
        deleteButton.startAnimation(anim);
    }

    public LinearLayout addButton(String text,String predict_text){
        LinearLayout button = (LinearLayout) getEnKeyInflaterView(text,predict_text);
        button.setVisibility(View.GONE);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        button.setLayoutParams(params);

        return button;
    }

    public View getEnKeyInflaterView(String text,String predict){
        LayoutInflater enKeyInflater = LayoutInflater.from(softKeyboard8);
        View button = enKeyInflater.inflate(R.layout.en_key, null);
        TextView main_text = (TextView) button.findViewById(R.id.main_text);
        TextView predict_text = (TextView) button.findViewById(R.id.predict_text);
        main_text.setText(Global.halfToFull(text));
        main_text.setTextColor(skinInfoManager.skinData.textcolors_26keys);
        predict_text.setText(Global.halfToFull(predict));
        predict_text.setTextColor(skinInfoManager.skinData.textcolors_26keys);
        if (Global.shadowSwitch) {
            main_text.setShadowLayer(Global.shadowRadius,0,0,skinInfoManager.skinData.shadow);
            predict_text.setShadowLayer(Global.shadowRadius,0,0,skinInfoManager.skinData.shadow);
        }
        return button;
    }

    @Override
    public void setBackgroundAlpha(int alpha){
        for (LinearLayout button:buttonList){
            if(button.getBackground() != null)button.getBackground().setAlpha(0);
            (button.findViewById(R.id.main_text)).getBackground().setAlpha(alpha);
            (button.findViewById(R.id.predict_text)).getBackground().setAlpha(alpha);
        }
        shiftButton.getBackground().setAlpha(alpha);
        smileButton.getBackground().setAlpha(alpha);
        deleteButton.getBackground().setAlpha(alpha);
    }

    public void setTypeface(Typeface typeface){
        shiftButton.setTypeface(typeface);
        deleteButton.setTypeface(typeface);
        smileButton.setTypeface(typeface);
    }

    private void onTouchEffect(View view, int action,int backgroundcolor){
        softKeyboard8.transparencyHandle.handleAlpha(action);
        softKeyboard8.keyBoardTouchEffect.onTouchEffectWithAnim(
                view,action,
                skinInfoManager.skinData.backcolor_touchdown,
                backgroundcolor,
                context
        );
    }

    private void onTouchEffectSpecial(View view,int action,int backgroundcolor){
        softKeyboard8.transparencyHandle.handleAlpha(action);
        softKeyboard8.keyBoardTouchEffect.onTouchEffectWithAnimForQK(view,action,
                skinInfoManager.skinData.backcolor_touchdown,
                backgroundcolor,
                context
        );
    }

    private void showSmile() {
        softKeyboard8.quickSymbolViewGroup.setVisibility(View.GONE);
        softKeyboard8.preFixViewGroup.setText(Global.convertStringtoList(mAllSmileText));
        softKeyboard8.preFixViewGroup.setBackgroundAlpha((int) (Global.mCurrentAlpha * 255));
        softKeyboard8.preFixViewGroup.updateSkin();
        softKeyboard8.preFixViewGroup.setVisibility(View.VISIBLE);
        softKeyboard8.transparencyHandle.DownAlpha();
    }

    private void hideSmile() {
        softKeyboard8.preFixViewGroup.setBackgroundColor(skinInfoManager.skinData.backcolor_prefix);
        softKeyboard8.preFixViewGroup.setText(Global.convertStringtoList(softKeyboard8.mFuncKeyboardText));
        softKeyboard8.preFixViewGroup.setVisibility(View.GONE);
        softKeyboard8.quickSymbolViewGroup.setVisibility(View.VISIBLE);
        if (WIInputMethod.GetWordsNumber() > 0) {
            softKeyboard8.quickSymbolViewGroup.setVisibility(View.GONE);
            softKeyboard8.secondLayerLayout.setVisibility(View.GONE);
            softKeyboard8.qkCandidatesViewGroup.setVisibility(View.VISIBLE);
        }
        softKeyboard8.transparencyHandle.DownAlpha();
    }

    String alphabetUpCase = "QWERTYUIOPASDFGHJKLZXCVBNM";
    String alphabet = "qwertyuiopasdfghjklzxcvbnm";

    private View.OnTouchListener qkInputOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            View upLightView;
            onTouchEffectSpecial(view,motionEvent.getAction(),skinInfoManager.skinData.backcolor_26keys);
            if(view.getBackground() != null)view.getBackground().setAlpha(0);
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    upLightView = softKeyboard8.lightViewManager.mLightView[0];
                    if (upLightView.isShown()) {
                        upLightView.setVisibility(View.GONE);
                        upLightView.clearAnimation();
                        upLightView.startAnimation(AnimationUtils.loadAnimation(context,R.anim.light_bot_off));
                    }
                    if (motionEvent.getY() < 0) {
                        String commitText = (String)((TextView)view.findViewById(R.id.predict_text)).getText();
                        if (commitText.length() > 0) {
                            commitText = Global.fullToHalf(commitText);
                            if (softKeyboard8.functionsC.isAllLetter(commitText)) {
                                commitText = commitText.toLowerCase();
                                softKeyboard8.sendMsgToQKKernel(Global.fullToHalf(commitText));
                            } else {
                                softKeyboard8.ChooseWord(0);
                                softKeyboard8.CommitText(commitText);
                            }
                        }
                    } else {
                        int index = buttonList.indexOf(view);
                        softKeyboard8.sendMsgToQKKernel(alphabet.substring(index,index+1));
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    upLightView = softKeyboard8.lightViewManager.mLightView[0];
                    if (motionEvent.getY() >= view.getY() && upLightView.isShown()) {
                        upLightView.setVisibility(View.GONE);
                        upLightView.clearAnimation();
                        upLightView.startAnimation(AnimationUtils.loadAnimation(context,R.anim.light_top_off));
                    }
                    boolean mLightShow = false;
                    for (View lightview :softKeyboard8.lightViewManager.mLightView){
                        mLightShow |= lightview.isShown();
                    }
                    if (motionEvent.getY() < view.getY() && !mLightShow) {
                        String commitText = ((TextView) view.findViewById(R.id.predict_text)).getText().toString();
                        if (commitText.length() < 1) break;
                        ((TextView) upLightView).setText(Global.halfToFull(commitText));
                        upLightView.setVisibility(View.VISIBLE);
                        upLightView.clearAnimation();
                        upLightView.startAnimation(AnimationUtils.loadAnimation(context,R.anim.light_top));
                    }
                    break;
            }
            Global.keyboardRestTimeCount = 0;
            return true;
        }
    };

    View.OnTouchListener enInputOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            onTouchEffectSpecial(view,motionEvent.getAction(),skinInfoManager.skinData.backcolor_26keys);
            if(view.getBackground() != null)view.getBackground().setAlpha(0);
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_UP:
                    int index = buttonList.indexOf(view);
                    String commitText = mShiftOn ? alphabetUpCase.substring(index,index+1): alphabet.substring(index,index+1);
                    if (Global.isInView(view,motionEvent)) {
                        softKeyboard8.CommitText(commitText);
                    }
                    break;
            }
            Global.keyboardRestTimeCount = 0;
            return true;
        }
    };

    /**
     * TODO
     * 功能：监听英文键盘Shift键的touch事件
     * 调用时机：touch英文键盘的Shift键
     */
    View.OnTouchListener mShiftKeyOnTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            softKeyboard8.transparencyHandle.handleAlpha(event.getAction());
            if (event.getAction() == MotionEvent.ACTION_UP) {
                mShiftOn = !mShiftOn;
                ((Button) v).setText(mShiftOn ? shiftText[1] : shiftText[0]);
//                int i=0;
//                for (LinearLayout button:buttonList){
//                    ((TextView)button.findViewById(R.id.main_text)).setText(Global.fullToHalf(
//                            mShiftOn?alphabetUpCase.substring(i,i++):alphabet.substring(i,i++)));
//                }
            }
            onTouchEffect(v, event.getAction(), skinInfoManager.skinData.backcolor_shift);
            return true;
        }
    };

    private int[] performActions = {
            android.R.id.selectAll,
            android.R.id.copy,
            android.R.id.cut,
            android.R.id.paste
    };

    View.OnTouchListener mSmileKeyOnTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            softKeyboard8.transparencyHandle.handleAlpha(event.getAction());
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    showSmile();
                    break;
                case MotionEvent.ACTION_MOVE:
                    PreFixViewGroup preFixViewGroup = softKeyboard8.preFixViewGroup;
                    float X = event.getX();
                    float Y = event.getY();
                    float perWidth = softKeyboard8.keyboardWidth / SMILE_KEYS_NUM;
                    if (Y <= 0) {
                        preFixViewGroup.setBackgroundColor(skinInfoManager.skinData.backcolor_prefix);
                        if (0 < X && X < perWidth * 4) {
                            preFixViewGroup.setBackgroundColorByIndex(skinInfoManager.skinData.backcolor_touchdown, (int) (X / perWidth));
                        }
                    } else {
                        preFixViewGroup.setBackgroundColor(skinInfoManager.skinData.backcolor_prefix);
                    }
                    preFixViewGroup.setBackgroundAlpha((int) (Global.mCurrentAlpha * 255));
                    break;
                case MotionEvent.ACTION_UP:
                    hideSmile();
                    float x = event.getX();
                    float y = event.getY();
                    float perwidth = softKeyboard8.keyboardWidth / SMILE_KEYS_NUM;
                    InputConnection ic = softKeyboard8.getCurrentInputConnection();
                    if (y <= 0 && ic != null) {
                        x = Math.max(0,Math.min(4*perwidth,x));
                        int select = ((int) (x / perwidth));
                        ic.performContextMenuAction(performActions[select]);
                        if (select == 2){
                            softKeyboard8.innerEdit("",false);
                        }
                    }
                    break;
            }
            onTouchEffect(v, event.getAction(), skinInfoManager.skinData.backcolor_smile);
            return false;
        }
    };


    View.OnTouchListener mDeleteOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            softKeyboard8.transparencyHandle.handleAlpha(event.getAction());
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                softKeyboard8.mHandler.sendEmptyMessageDelayed(softKeyboard8.MSG_REPEAT, softKeyboard8.REPEAT_START_DELAY);
            } else if (event.getAction() == MotionEvent.ACTION_MOVE && Global.slideDeleteSwitch) {
                View lightView = softKeyboard8.lightViewManager.mLightView[2];
                if (event.getX() < 0 && !lightView.isShown()) {
                    lightView.setVisibility(View.VISIBLE);
                    lightView.startAnimation(AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.light_left));
                    ((TextView) lightView).setText("清空");
                    softKeyboard8.mHandler.removeMessages(softKeyboard8.MSG_REPEAT);
                }
                if (event.getX() >= 0 && lightView.isShown()) {
                    lightView.startAnimation(AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.light_left_off));
                    lightView.setVisibility(View.GONE);
                }
                if(event.getY() <0){
                    softKeyboard8.lightViewManager.ShowLightView(event.getX(),event.getY(),v.getWidth(),v.getHeight(),"恢复");
                    softKeyboard8.mHandler.removeMessages(softKeyboard8.MSG_REPEAT);
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                softKeyboard8.mHandler.removeMessages(softKeyboard8.MSG_REPEAT);
                View lightView = softKeyboard8.lightViewManager.mLightView[2];
                View outView = softKeyboard8.lightViewManager.mOutLightView[2];
                if (event.getX() < 0 && lightView.isShown() && Global.slideDeleteSwitch) {
                    lightView.startAnimation(AnimationUtils.loadAnimation(context.getApplicationContext(),R.anim.light_right_off)) ;
                    lightView.setVisibility(View.GONE);
                    outView.setVisibility(View.VISIBLE);
                    outView.startAnimation(AnimationUtils.loadAnimation(context.getApplicationContext(),R.anim.light_left_out));
                    softKeyboard8.functionsC.DeleteAll();
                } else if (event.getY() < 0) {
                    if(Global.redoTextForDeleteAll != ""){
                        softKeyboard8.CommitText(Global.redoTextForDeleteAll);
                        Global.redoTextForDeleteAll = "";
                    }
                    if(Global.redoTextForDeleteAll_preedit != ""){
                        softKeyboard8.sendMsgToQKKernel(Global.redoTextForDeleteAll_preedit);
                        Global.redoTextForDeleteAll_preedit = "";
                    } else {
                        if (Global.redoText_single.size()>0){
                            InputAction ia = Global.redoText_single.poll();
                            if (ia.Type == InputAction.TEXT_TO_KERNEL){
                                softKeyboard8.sendMsgToQKKernel(ia.text);
                            } else {
                                softKeyboard8.CommitText(ia.text);
                            }
                        }
                    }
                } else {
                    softKeyboard8.DeleteLast();
                }
            }
            onTouchEffect(v, event.getAction(), skinInfoManager.skinData.backcolor_delete);
            return false;
        }
    };
}
