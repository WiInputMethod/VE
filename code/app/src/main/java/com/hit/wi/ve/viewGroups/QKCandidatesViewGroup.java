package com.hit.wi.ve.viewGroups;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputConnection;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import android.widget.TextView;
import com.hit.wi.jni.WIInputMethodNK;
import com.hit.wi.jni.WIInputMethod;
//import com.hit.wi.t9.functions.EmojiUtil;
import com.hit.wi.ve.functions.QKEmojiUtil;
import com.hit.wi.ve.values.Global;
import com.hit.wi.ve.view.QuickButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 全键候选词管理器
 *
 * @author 郭高扬
 */
public class QKCandidatesViewGroup extends ScrolledViewGroup {

    /**
     * Maximum number of displaying candidates par one line (full view mode)
     */
    private final int CAND_DIV_NUM = 4;
    /**
    * min value of the layer show num
    * */
    private final int MIN_LAYER_SHOWNUM = 10;
    /**
     * general information about a display
     */
    private final DisplayMetrics mMetrics = new DisplayMetrics();
    /**
     * EmojiUtil of QK
     */
    private QKEmojiUtil mQKEmojiUtil;
    /**
     * EmojiUtil of T9
     */

    private int mTextColor;
    private int mBackColor;

    public String mQPOrEmoji = Global.QUANPIN;

    private int standardButtonWidth;
    private int standardButtonHeight;

    private List<LinearLayout> layerList;
    private LinearLayout.LayoutParams layerParams;
    private int layerPointer;

    public QKCandidatesViewGroup() {
        mMetrics.setToDefaults();
        layerList = new ArrayList<>();
        layerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    public void create(Context context){
        super.create(vertical,context);
        mTextColor = softKeyboard8.skinInfoManager.skinData.textcolors_candidate_t9;
        mBackColor = softKeyboard8.skinInfoManager.skinData.backcolor_candidate_t9;
        mQKEmojiUtil = new QKEmojiUtil(softKeyboard8);
        standardButtonHeight = 0;
        standardButtonWidth = 0;
//        SharedPreferences sp = SharedPreferenceManager.getDefaultSharedPreferences(context);
    }

    // state machine
    public void refreshState(boolean hide,String type){
        if (hide) {
            WIInputMethodNK.CLeanKernel();
            if (scrollView.isShown()) {hide();}
        } else {
            if (!scrollView.isShown()) {
                show();
            }
            if (WIInputMethod.GetWordsNumber()>0 || WIInputMethodNK.GetWordsNumber()>0){
                displayCandidates(type);
            }
            scrollView.fullScroll(ScrollView.FOCUS_UP);
        }
    }

    @Override
    public void show(){
        for(LinearLayout layout:layerList){
            layout.setVisibility(View.VISIBLE);
        }
        clearAnimation();
        setVisibility(View.VISIBLE);
    }

    public void setSize(int width ,int height){
        super.setSize(width,height);
        standardButtonWidth = width/ CAND_DIV_NUM;
        standardButtonHeight = height;
        resetButtonWidthAndresetLayerHeight();
    }

    public void resetButtonWidthAndresetLayerHeight(){
        for (QuickButton button :buttonList) {
            int length  = measureTextLength(button.getText());
            button.itsLayoutParams.width = length;
            button.itsLayoutParams.height = standardButtonHeight;
        }
        layerParams.height = standardButtonHeight;
        updateViewLayout();
    }

    public void displayCandidates() {
        if (mQPOrEmoji == Global.QUANPIN || mQPOrEmoji == Global.EMOJI){
            displayCandidates(mQPOrEmoji,300);
        } else {
            displayCandidates(mQPOrEmoji,symbols,300);
        }
    }

    public void displayCandidates(String type){
        displayCandidates(type,9);
    }

    public void displayCandidates(String type,int show_num){
        displayCandidates(type, Collections.EMPTY_LIST,show_num);
    }

    List<String> symbols;
    public void displayCandidates(String type,List<String> strings,int show_num) {
        mQPOrEmoji = type;
        int i=0;
        List<String> words = new ArrayList<>();
        symbols = strings;
        if(!strings.equals(Collections.EMPTY_LIST)){
            words = strings.subList(0,Math.min(show_num,strings.size()-1));
        }else if(type == Global.QUANPIN) {
            int total = Math.min(show_num,WIInputMethod.GetWordsNumber()-1);
            while(i<total){
                String text = WIInputMethod.GetWordByIndex(i);
                words.add(mQKEmojiUtil.getShowString(text));
                i++;
            }
        } else {
            int total = Math.min(show_num,WIInputMethodNK.GetWordsNumber()-1);
            while(i<total){
                words.add(mQKEmojiUtil.getShowString(WIInputMethodNK.GetWordByIndex(i)));
                i++;
            }
        }

        setCandidates(words);
        scrollView.fullScroll(View.FOCUS_UP);//go to top
    }

    private LinearLayout getWorkingLayer(int position) {
        if (layerList.size() <= position){
            return addLayer();
        } else {
            return layerList.get(position);
        }
    }

    private int getLayerRemainLength(LinearLayout layer) {
        int remainLength = standardButtonWidth * CAND_DIV_NUM;
        for (int i=layer.getChildCount()-1;i>=0;i--){
            QuickButton button = (QuickButton) layer.getChildAt(i);
            remainLength -= button.itsLayoutParams.width;
        }
        return remainLength;
    }

    public LinearLayout addLayer(){
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setVisibility(View.VISIBLE);
        layoutforWrapButtons.addView(layout,layerParams);
        layerList.add(layout);
        return layout;
    }


    private QuickButton initNewButton(String text){
        QuickButton button = super.addButton(mTextColor,mBackColor,text);
        button.setVisibility(View.VISIBLE);
        button.setPressed(false);
        button.setOnTouchListener(mCandidateOnTouch);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                measureTextLength(text), ViewGroup.LayoutParams.MATCH_PARENT
        );
        button.itsLayoutParams = params;
        button.setLayoutParams(params);

        button.setTextSize(2*Global.textsizeFactor*standardButtonHeight/8);
        return button;
    }

    /**
     * Add a candidate into the list
     */
    public QuickButton addButtonQ(String text, QuickButton button) {
        LinearLayout layer = getWorkingLayer(layerPointer);
        int remainLength = getLayerRemainLength(layer);
        button.setText(text);
        button.itsLayoutParams.width = measureTextLength(text);
        if(remainLength < button.itsLayoutParams.width){
            if(remainLength > 0){
                QuickButton fillButton = initNewButton("");
                fillButton.setWidth(remainLength);
                layer.addView(fillButton);
            }
            layer = getWorkingLayer(layerPointer++);
        }
        layer.addView(button,button.itsLayoutParams);
        button.clearAnimation();
        button.setEllipsize(standardButtonWidth * CAND_DIV_NUM <= measureTextLength(text)?TextUtils.TruncateAt.END:null);
        button.getBackground().setAlpha(Global.getCurrentAlpha());
        button.setPressed(false);
        button.setTextSize(2*Global.textsizeFactor*standardButtonHeight/8);
        return button;

    }

    @SuppressLint("NewApi")
    public void setCandidates(List<String> words) {
        int i = 0;
        for (LinearLayout layout:layerList){
            layout.removeAllViews();
        }
        layerPointer = 0;

        for (;i<buttonList.size() && i<words.size();i++){
            addButtonQ(words.get(i),buttonList.get(i));
        }
        for (;i<words.size();i++){
            QuickButton button = initNewButton(words.get(i));
            button.setVisibility(View.VISIBLE);
            buttonList.add(button);
            addButtonQ(words.get(i),button);
        }
        for (;i<buttonList.size();i++){
            buttonList.get(i).setText("");//clear button
        }

        int j=0;
        for (;j<layerPointer;j++){
            layerList.get(j).setVisibility(View.VISIBLE);
        }
        if(j<MIN_LAYER_SHOWNUM)j=MIN_LAYER_SHOWNUM;
        for(;j<layerList.size();j++){
            layerList.get(j).setVisibility(View.GONE);
        }
        touched = false;
    }

    public int measureTextLength(CharSequence text) {
        Paint paint = new Paint();
        return Math.min(((int)(3*paint.measureText((String) text)))/standardButtonWidth +1, 4)*standardButtonWidth;
    }

    public void updateSkin() {
        if(Global.currentKeyboard == Global.KEYBOARD_QP || Global.currentKeyboard == Global.KEYBOARD_EN){
            mBackColor = softKeyboard8.skinInfoManager.skinData.backcolor_candidate_qk;
            mTextColor = softKeyboard8.skinInfoManager.skinData.textcolors_candidate_qk;
        }else {
            mBackColor = softKeyboard8.skinInfoManager.skinData.backcolor_candidate_t9;
            mTextColor = softKeyboard8.skinInfoManager.skinData.textcolors_candidate_t9;
        }
        super.updateSkin(mTextColor,mBackColor);
    }

    public void largeTheCandidate() {
        softKeyboard8.functionViewGroup.setVisibility(View.GONE);
        softKeyboard8.mInputViewGG.setVisibility(View.GONE);
        softKeyboard8.secondLayerLayout.setVisibility(View.GONE);
        int largeheight = softKeyboard8.keyboardParams.height - softKeyboard8.bottomBarViewGroup.getHeight();
        if (getHeight() != largeheight)
            setHeight(largeheight);
    }

    public  void smallTheCandidate() {
        softKeyboard8.viewSizeUpdate.UpdateQKCandidateSize();
        if (Global.currentKeyboard == Global.KEYBOARD_QP || Global.currentKeyboard == Global.KEYBOARD_EN) {
            softKeyboard8.functionViewGroup.setVisibility(View.VISIBLE);
        } else {
            softKeyboard8.secondLayerLayout.setVisibility(View.VISIBLE);
        }
        softKeyboard8.mInputViewGG.setVisibility(View.VISIBLE);
    }

    private void commitQKCandidate(View v) {
        String text = WIInputMethod.GetWordSelectedWord(buttonList.indexOf(v));
        scrollView.fullScroll(ScrollView.FOCUS_UP);
        InputConnection ic = softKeyboard8.getCurrentInputConnection();
        if (ic != null && text!= null) {
            try {
                mQKEmojiUtil.commitEmoji(softKeyboard8, text);
            } catch (Exception e ){
                Log.d("WIVE","expception"+e.toString());
            }
        }
    }

    private void commitT9Candidate(View v){
        String text = WIInputMethodNK.GetWordSelectedWord(buttonList.indexOf(v));
        if ( text!=null ){
            softKeyboard8.commitText(text);
        }
    }

    float downX, downY;
    private boolean touched = false;
    /**
     * Event listener for touching a candidate
     */
    private OnTouchListener mCandidateOnTouch = new OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            softKeyboard8.keyBoardTouchEffect.onTouchEffectWithAnim(v,event.getAction(),
                    skinInfoManager.skinData.backcolor_touchdown,
                    mBackColor,
                    context
            );
            softKeyboard8.transparencyHandle.handleAlpha(event.getAction());
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!touched){
                        displayCandidates();
                        touched = true;
                    }
                    downX = event.getX();downY = event.getY();
                case MotionEvent.ACTION_MOVE:
                    if (Math.abs(event.getY() - downY) + Math.abs(event.getX() - downX) < 50) break;
                    if (!Global.inLarge) {
                        largeTheCandidate();
                        softKeyboard8.bottomBarViewGroup.intoReturnState();
                        Global.inLarge = true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (!Global.isInView(v, event)) break;
                    if (Global.inLarge) {
                        smallTheCandidate();
                        softKeyboard8.bottomBarViewGroup.backReturnState();
                        Global.inLarge = false;
                    }
                    if(Global.isInView(v,event)) {
                        if (Global.currentKeyboard == Global.KEYBOARD_SYM){
                            CharSequence text = ((TextView)v).getText();
                            if (!softKeyboard8.quickSymbolViewGroup.islock()) {
                                int inputKeyboard = PreferenceManager.getDefaultSharedPreferences(context).getString("KEYBOARD_SELECTOR", "2").equals("1") ?
                                        Global.KEYBOARD_T9 : Global.KEYBOARD_QP;
                                softKeyboard8.keyBoardSwitcher.switchKeyboard(inputKeyboard, true);
                                softKeyboard8.refreshDisplay();
                            }
                            softKeyboard8.commitText(text);
                        } else if (mQPOrEmoji == Global.SYMBOL) {
                            CharSequence text = ((TextView)v).getText();
                            softKeyboard8.commitText(text);
                            softKeyboard8.refreshDisplay();
                        } else if (mQPOrEmoji == Global.QUANPIN ) {
                            commitQKCandidate(v);
                            softKeyboard8.refreshDisplay();
                        } else {
                            commitT9Candidate(v);
                            softKeyboard8.refreshDisplay();
                        }
                        touched = false;
                    }
                    break;
            }
            return true;
        }
    };
}
