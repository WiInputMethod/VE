package com.hit.wi.ve.view;

import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.PopupWindow;
import com.hit.wi.jni.Kernel;
import com.hit.wi.ve.R;
import com.hit.wi.ve.SoftKeyboard;
import com.hit.wi.ve.values.Global;

/**
 * Created by purebleusong on 2016/4/7.
 */
public class PreEditPopup {
    private PopupWindow container;
    private EditText editText;
    private SoftKeyboard softKeyboard;

    private int leftMargin = 0;
    private int selectStart;
    private int selectStop;

    public void setSoftKeyboard(SoftKeyboard softKeyboard){
        this.softKeyboard = softKeyboard;
    }

    public void create(Context context){
        editText = new EditText(context);
        editText.setPadding(0, 0, 0, 0);
        editText.setGravity(Gravity.LEFT);
        editText.setVisibility(View.VISIBLE);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        editText.setBackgroundResource(R.drawable.blank);
        editText.setBackgroundColor(softKeyboard.skinInfoManager.skinData.backcolor_editText);
        editText.getBackground().setAlpha((int) (Global.mCurrentAlpha * 255));
        editText.setOnClickListener(editOnClickListener);
        if (Global.shadowSwitch) editText.setShadowLayer(Global.shadowRadius, 0, 0, softKeyboard.skinInfoManager.skinData.shadow);

        container = new PopupWindow(editText, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        container.setFocusable(false);
        container.setTouchable(true);
        container.setClippingEnabled(false);
        container.setBackgroundDrawable(null);
    }

    public void upadteSize(int width,int height,int leftMargin){
        container.setHeight(height);
        container.setWidth(width);
        this.leftMargin = leftMargin;
    }

    public void updateSkin() {
        editText.setBackgroundResource(R.drawable.blank);
        editText.setBackgroundColor(softKeyboard.skinInfoManager.skinData.backcolor_preEdit);
        editText.setTextColor(softKeyboard.skinInfoManager.skinData.textcolors_preEdit);
        editText.getBackground().setAlpha((int) (Global.mCurrentAlpha*255));
        editText.setShadowLayer(Global.shadowRadius,0,0,softKeyboard.skinInfoManager.skinData.shadow);
    }

    public boolean isShown() {
        return container.isShowing() | editText.isShown();
    }

    public void refresh(){
        if(editText==null) return;
        String pinyin = Kernel.getWordsShowPinyin();
        if(pinyin.length()>0){
            show(pinyin);
            editText.setSelection(Math.min(selectStart,pinyin.length()),Math.min(selectStop,pinyin.length()));
            Log.d("WIVE",selectStart+"  fuck"+selectStop);
        } else {
            dismiss();
        }
    }

    public void show(CharSequence text){
        editText.setText(text);
        Log.d("WIVE","length " + container.getWidth()/(1+(text.length()/2)));
        editText.setTextSize(Math.min((float) (container.getHeight()*0.33),container.getWidth()/(1+(text.length()/2))));
        if (!isShown()){
            container.showAsDropDown(softKeyboard.keyboardLayout,leftMargin,-container.getHeight()-softKeyboard.keyboardParams.height);
        }
    }

    public void dismiss(){
        container.dismiss();
    }

    public void setCursor(int cursor) {
        this.selectStart = Math.max(cursor,0);
        this.selectStop = Math.max(cursor,0);
    }

    public void setCursor(int start,int stop) {
        this.selectStart = Math.max(start,0);
        this.selectStop = Math.max(stop,0);
    }

    private View.OnClickListener editOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            InputConnection ic = softKeyboard.getCurrentInputConnection();
            Log.d("WIVE",editText.getSelectionEnd()+" "+editText.getSelectionStart());
            ic.setSelection(editText.getSelectionStart(),editText.getSelectionEnd());
        }
    };

}
