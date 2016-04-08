package com.hit.wi.ve.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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
    public PopupWindow container;
    private EditText editText;
    private SoftKeyboard softKeyboard;

    int leftMargin = 0;

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
        if (Global.shadowSwitch) editText.setShadowLayer(Global.shadowRadius, 0, 0, softKeyboard.skinInfoManager.skinData.shadow);

        container = new PopupWindow(editText, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        container.setFocusable(false);
        container.setTouchable(true);
        container.setClippingEnabled(false);
        container.setAnimationStyle(R.style.anim_preedit);
        container.setBackgroundDrawable(null);
    }

    public void upadteSize(int width,int height,int leftMargin){
        editText.setTextSize((float) (height*0.33));
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
        if(editText==null)
             return;
        String pinyin = Kernel.getWordsShowPinyin();
        if(pinyin.length()>0){
            show(pinyin,pinyin.length());
        } else {
            dismiss();
        }
    }

    public void show(CharSequence text,int cursor){
        editText.setText(text);
        editText.setSelection(cursor);
        if (!isShown()){
            container.showAsDropDown(softKeyboard.keyboardLayout,leftMargin,-container.getHeight()-softKeyboard.keyboardParams.height);
        }
    }

    public void dismiss(){
        container.dismiss();
    }
}
