package com.hit.wi.t9.viewGroups;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.Animation;
import android.widget.*;

import com.hit.wi.jni.Kernel;
import com.hit.wi.util.StringUtil;
import com.hit.wi.t9.Interfaces.QuickSymbolInterface;
import com.hit.wi.t9.Interfaces.ViewGroupInterface;
import com.hit.wi.t9.R;
import com.hit.wi.t9.functions.FileManager;
import com.hit.wi.t9.settings.QuickSymbolAddActivity;
import com.hit.wi.t9.values.Global;
import com.hit.wi.t9.values.QuickSymbolsDataStruct;
import com.hit.wi.t9.values.SkinInfoManager;
import com.hit.wi.t9.view.QuickButton;

import java.io.*;

import java.util.List;

/**
 * Created by Administrator on 2015/5/8.
 */
public class QuickSymbolViewGroup extends ScrolledViewGroup implements QuickSymbolInterface, ViewGroupInterface {
    private QuickButton firstButton;
    private QuickButton lastButton;
    //configure
    private int currentSymbolFlag;
    private QuickSymbolsDataStruct symbols;
    private String[] currentSymbols;
    private boolean mLockSymbolState = false;
    /**
     * 符号键盘锁定时“符”键显示的字符
     */
    String mLockSymbol;
    /**
     * 符号键盘未锁定时“符”键显示的字符
     */
    String mUnLockSymbol;

    public QuickSymbolViewGroup() {
        super();
        iniConfiguration();
    }

    private void iniConfiguration() {
        textSize = 0;
        //this is default
        textColor = -1;
        backgroundAlpha = -1;
        backgroundResource = -1;
        backgroundColor = -1;
        buttonAlpha = -1;
        buttonpadding = -1;
    }

    /**
     *
     */
    public void create(Context context) {

        super.create(horizontal, context);
        symbols = new QuickSymbolsDataStruct();
        skinInfoManager = SkinInfoManager.getSkinInfoManagerInstance();

        try {
            symbols = loadSymbolFromFile("default");
        } catch (Exception e) {
            symbols = loadSymbolsFromXML();
            try {
                writeSymbolsToFile("default", symbols);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        String[] temp = res.getStringArray(R.array.LOCK_SYM);
        mLockSymbol = temp[1];
        mUnLockSymbol = temp[0];

        addFirstButton();
        addLastButton();
        updateCurrentSymbolsAndSetTheContent(currentSymbolFlag);

        horizontalScrollView.setSmoothScrollingEnabled(true);
        horizontalScrollView.setHorizontalScrollBarEnabled(false);
    }

    private void setText(List<String> quickSymbolContent) {
        int i = 1;
        buttonList.remove(lastButton);
        for (String buttonText : quickSymbolContent) {
            if (i < buttonList.size()) {
                buttonList.get(i).setText(buttonText);
            } else {
                QuickButton button = addButton(buttonText, i);
                buttonList.add(button);
                button.setOnTouchListener(commonButtonOnTouchListener);
            }
            i++;
        }
        while (i < buttonList.size() - 1) {
            layoutforWrapButtons.removeView(buttonList.get(i));
            buttonList.remove(i);
        }
        buttonList.add(lastButton);
    }

    public void updateSkin() {
        super.updateSkin(skinInfoManager.skinData.textcolor_quickSymbol,
                skinInfoManager.skinData.backcolor_quickSymbol);
    }

    public void updateCurrentSymbolsAndSetTheContent(int keyboard) {
        currentSymbolFlag = keyboard;
        setCurrentSymbolsByKeyboard(keyboard);
        firstButton.setText(res.getString(R.string.symbol));
        List<String> quickSymbolContent = StringUtil.convertStringstoList(currentSymbols);
        setText(quickSymbolContent);
    }

    public void updateSymbolsFromFile() throws IOException {
        symbols = loadSymbolFromFile("default");
        updateCurrentSymbolsAndSetTheContent(currentSymbolFlag);
    }

    public void refreshState() {
        clearAnimation();
        if (softKeyboard.specialSymbolChooseViewGroup.isShown() || softKeyboard.prefixViewGroup.isShown()) {
            if (isShown()) hide();
        } else {
            if (!isShown()) show();
        }
    }

    public boolean isLock() {
        return mLockSymbolState;
    }

    private void setCurrentSymbolsByKeyboard(int keyboard) {
        switch (keyboard) {
            case Global.KEYBOARD_QK:
            case Global.KEYBOARD_T9:
                currentSymbols = symbols.chineseSymbols;
                break;
            case Global.KEYBOARD_EN:
                currentSymbols = symbols.englishSymbols;
                break;
            case Global.KEYBOARD_SYM:
            case Global.KEYBOARD_NUM:
                currentSymbols = symbols.numberSymbols;
                break;
        }
    }

    private QuickButton addNewButton(String text) {
        return addButton(text, buttonList.size());
    }

    private QuickButton addButton(String text, int position) {
        QuickButton button = super.addButton(skinInfoManager.skinData.textcolor_quickSymbol,
                skinInfoManager.skinData.backcolor_quickSymbol,
                text);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.leftMargin = buttonpadding;

        button.itsLayoutParams = layoutParams;
        button.setVisibility(View.VISIBLE);
        layoutforWrapButtons.addView(button, position, layoutParams);

        return button;
    }

    private void addFirstButton() {
        firstButton = addNewButton(res.getString(R.string.symbol));
        buttonList.add(firstButton);
        firstButton.setOnTouchListener(firstButtonOnTouchListener);
    }

    private void addLastButton() {
        lastButton = addNewButton("\u2647");
        buttonList.add(lastButton);
        lastButton.setOnTouchListener(lastButtonOnTouchListener);
    }

    private QuickSymbolsDataStruct loadSymbolsFromXML() {
        QuickSymbolsDataStruct mSymbols = new QuickSymbolsDataStruct();

        mSymbols.chineseSymbols = res.getStringArray(R.array.QUICK_SYMBOL);//res defined in super.create
        mSymbols.englishSymbols = res.getStringArray(R.array.QUICK_SYMBOL_EN);
        mSymbols.numberSymbols = res.getStringArray(R.array.QUICK_SYMBOL_NUM);

        return mSymbols;
    }

    public QuickSymbolsDataStruct loadSymbolFromFile(String symbolModeName) throws IOException {
        FileManager fileManager = FileManager.getInstance();
        QuickSymbolsDataStruct symbolsDataStruct = new QuickSymbolsDataStruct();
        String flagstring = ".Symbols";

        String enfilename = symbolModeName + ".en" + flagstring;
        symbolsDataStruct.englishSymbols = fileManager.readStringArrayFromFile(enfilename);

        String chfilename = symbolModeName + ".ch" + flagstring;
        symbolsDataStruct.chineseSymbols = fileManager.readStringArrayFromFile(chfilename);

        String numfilename = symbolModeName + ".num" + flagstring;
        symbolsDataStruct.numberSymbols = fileManager.readStringArrayFromFile(numfilename);

        return symbolsDataStruct;
    }

    public void writeSymbolsToFile(String symbolModeName, QuickSymbolsDataStruct symbolsDataStruct) throws IOException {
        String flagstring = ".Symbols";
        FileManager fileManager = FileManager.getInstance();

        String enfilename = symbolModeName + ".en" + flagstring;
        fileManager.writeToFile(enfilename, symbolsDataStruct.englishSymbols);

        String chfilename = symbolModeName + ".ch" + flagstring;
        fileManager.writeToFile(chfilename, symbolsDataStruct.chineseSymbols);

        String numfilename = symbolModeName + ".num" + flagstring;
        fileManager.writeToFile(numfilename, symbolsDataStruct.numberSymbols);
    }

    @Override
    public void startAnimation(Animation anim) {
        super.startAnimation(anim);
    }

    private void onTouchEffect(View v, MotionEvent event) {
        softKeyboard.transparencyHandle.handleAlpha(event.getAction());
        softKeyboard.keyboardTouchEffect.onTouchEffectWithAnim(v, event.getAction(),
                skinInfoManager.skinData.backcolor_touchdown,
                skinInfoManager.skinData.backcolor_quickSymbol
        );
    }

    private View.OnTouchListener lastButtonOnTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            onTouchEffect(v, event);
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Kernel.cleanKernel();
                Intent intent = new Intent(context, QuickSymbolAddActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle = new Bundle();
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
            return true;
        }
    };

    private View.OnTouchListener commonButtonOnTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            onTouchEffect(v, event);
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Kernel.cleanKernel();
                softKeyboard.commitText(((Button) v).getText());
            }
            return true;
        }
    };

    private View.OnTouchListener firstButtonOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            onTouchEffect(view, motionEvent);
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                Kernel.cleanKernel();
                Global.refreshState(softKeyboard);
                if (Global.currentKeyboard == Global.KEYBOARD_SYM) {
                    firstButton.setText(mLockSymbolState ? mUnLockSymbol : mLockSymbol);
                    mLockSymbolState = !mLockSymbolState;
                } else {
                    softKeyboard.switchKeyboardTo(Global.KEYBOARD_SYM, true);
                    softKeyboard.functionsC.showDefaultSymbolSet();
                    firstButton.setText(mUnLockSymbol);
                }
            }
            return true;
        }
    };

}
