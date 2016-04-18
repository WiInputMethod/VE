package com.hit.wi.ve;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.inputmethodservice.InputMethodService;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.*;

import com.hit.wi.jni.NKInitDictFile;
import com.hit.wi.jni.InitInputParam;
import com.hit.wi.util.CommonFuncs;
import com.hit.wi.util.InputMode;
import com.hit.wi.util.ViewFuncs;
import com.hit.wi.jni.*;
import com.hit.wi.ve.Interfaces.SoftKeyboardInterface;
import com.hit.wi.ve.effect.KeyBoardTouchEffect;
import com.hit.wi.ve.functions.GenerateMessage;
import com.hit.wi.ve.functions.SharedPreferenceManager;
import com.hit.wi.ve.functions.SymbolsManager;
import com.hit.wi.ve.values.Global;
import com.hit.wi.ve.values.SkinInfoManager;
import com.hit.wi.ve.view.LightViewManager;
import com.hit.wi.ve.view.PreEditPopup;
import com.hit.wi.ve.view.QuickButton;
import com.hit.wi.ve.view.SetKeyboardSizeView;
import com.hit.wi.ve.view.SetKeyboardSizeView.OnChangeListener;
import com.hit.wi.ve.view.SetKeyboardSizeView.SettingType;
import com.hit.wi.ve.viewGroups.*;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public final class SoftKeyboard extends InputMethodService implements SoftKeyboardInterface {

    /**
     * 功能：加载输入法内核链接库
     * 调用时机：该输入法类加载时调用
     */
    static {
        System.loadLibrary("WIIM_NK");
        System.loadLibrary("WIIM");
    }

    /**
     * 屏幕方向
     */
    private String orientation;

    /**
     * 横屏状态字符
     */
    private final String ORI_HOR = "_H";

    /**
     * 竖屏状态字符
     */
    private final String ORI_VER = "_V";

    /**
     * 设置键盘大小界面是否显示
     */
    boolean mSetKeyboardSizeViewOn = false;

    /**
     * 是全拼还是Emoji
     */
    public String mQPOrEmoji = Global.QUANPIN;

    /**
     * 中文键盘类型
     */
    int zhKeyboard;

    /**
     * 屏幕宽度
     */
    int mScreenWidth;

    /**
     * 屏幕高度
     */
    int mScreenHeight;

    /**
     * 状态栏高度
     */
    int mStatusBarHeight;

    public String[] mFuncKeyboardText;

    /**
     * 浮动窗口状态标识符
     */
    private static final int DISABLE_LAYOUTPARAMS_FLAG = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
            | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
    private static final int LOCK_LAYOUTPARAMS_FLAG = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
    private static final int ABLE_LAYOUTPARAMS_FLAG = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
    //默认是FLAG_NOT_FOCUSABLE，否则会抢夺输入框的焦点导致键盘收回

    //透明度有关
//    public static final int SET_ALPHA_VIEW_DESTROY = -803;

    private final int MSG_HIDE = 0;
    public final int MSG_REPEAT = 1;
    private final int MSG_SEND_TO_KERNEL = 2;
    private final int QP_MSG_SEND_TO_KERNEL = 3;
    private final int MSG_CHOOSE_WORD = 4;
    private final int MSG_HEART = 5;
    private final int MSG_LAZY_LOAD_CANDIDATE = 6;
    private final int MSG_DOUBLE_CLICK_REFRESH = 7;
    private final int MSG_KERNEL_CLEAN = 8;

    private static final int REPEAT_INTERVAL = 50; // 重复按键的时间
    public static final int REPEAT_START_DELAY = 400;// 重复按键

    //屏幕信息
    private int DEFAULT_FULL_WIDTH;
    private int DEFAULT_FULL_WIDTH_X;
    private String FULL_WIDTH_S = "FULL_WIDTH";
    private String FULL_WIDTH_X_S = "FULL_WIDTH_X";
    private int DEFAULT_KEYBOARD_X;
    private int DEFAULT_KEYBOARD_Y;
    private int DEFAULT_KEYBOARD_WIDTH;
    private int DEFAULT_KEYBOARD_HEIGHT;

    private final String KEYBOARD_X_S = "KEYBOARD_X";
    private final String KEYBOARD_Y_S = "KEYBOARD_Y";
    private final String KEYBOARD_WIDTH_S = "KEYBOARD_WIDTH";
    private final String KEYBOARD_HEIGHT_S = "KEYBOARD_HEIGHT";

    private boolean mWindowShown = false;
    /**
     * 左手模式
     */
    private boolean mLeftHand = false;
    private boolean show = true;
    /**
     * 光标位置处理
     */
    private int mNewStart;
    private int mNewEnd;
    private int mCandicateStart;
    private int mCandicateEnd;

    public int keyboardWidth = 0;
    private int keyboardHeight = 0;
    private int standardVerticalGapDistance = 10;
    private int standardHorizantalGapDistance = 0;
    private int maxFreeKernelTime = 60;

    private InitInputParam initInputParam;
    public Typeface mTypeface;

    public LinearLayout keyboardLayout;
    public WindowManager.LayoutParams keyboardParams = new WindowManager.LayoutParams();
    public LinearLayout secondLayerLayout;
    private LinearLayout.LayoutParams secondParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    public ViewGroup mInputViewGG;
    public LinearLayout.LayoutParams mGGParams = new LinearLayout.LayoutParams(0, 0);

    public WindowManager.LayoutParams mSetKeyboardSizeParams = new WindowManager.LayoutParams();
    public SetKeyboardSizeView mSetKeyboardSizeView;
    private QuickButton largeCandidateButton;

    private Listeners listeners = new Listeners();
    public KeyBoardSwitcherC keyBoardSwitcher = new KeyBoardSwitcherC();
    public SkinUpdateC skinUpdateC = new SkinUpdateC();
    public ScreenInfoC screenInfoC = new ScreenInfoC();
    public TransparencyHandle transparencyHandle = new TransparencyHandle();
    public ViewManagerC viewManagerC = new ViewManagerC();
    public FunctionsC functionsC = new FunctionsC();
    public ViewSizeUpdateC viewSizeUpdate = new ViewSizeUpdateC();

    public QKInputViewGroups qkInputViewGroups;
    public SpecialSymbolChooseViewGroup specialSymbolChooseViewGroup;
    public FunctionViewGroup functionViewGroup;
    public QuickSymbolViewGroup quickSymbolViewGroup;
    public PreFixViewGroup preFixViewGroup;
    public BottomBarViewGroup bottomBarViewGroup;
    public QKCandidatesViewGroup qkCandidatesViewGroup;
    public T9InputViewGroup t9InputViewGroup;
    public LightViewManager lightViewManager;
    public PreEditPopup preEditPopup;

    public SymbolsManager symbolsManager;
    public KeyBoardTouchEffect keyBoardTouchEffect;
    public SkinInfoManager skinInfoManager;
    private Resources res;

    /**
     * 处理消息事件
     */
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_HIDE:
                    if (Global.keyboardRestTimeCount > 7) {
                        transparencyHandle.UpAlpha();
                        Global.keyboardRestTimeCount = 0;
                    } else
                        Global.keyboardRestTimeCount++;
                    mHandler.removeMessages(MSG_HIDE);
                    mHandler.sendEmptyMessageDelayed(MSG_HIDE, Global.metaRefreshTime);
                    break;
                case MSG_REPEAT:
                    deleteLast();
                    sendEmptyMessageDelayed(MSG_REPEAT, REPEAT_INTERVAL);
                    break;
                case MSG_SEND_TO_KERNEL:
                    Kernel.inputPinyin((String) msg.obj);
                    refreshDisplay();
                    t9InputViewGroup.updateFirstKeyText();
                    break;
                case QP_MSG_SEND_TO_KERNEL:
                    innerEdit((String) msg.obj, false);
                    break;
                case MSG_CHOOSE_WORD:
                    chooseWord(msg.arg1);
                    break;
                case MSG_LAZY_LOAD_CANDIDATE:
                    qkCandidatesViewGroup.setCandidates((List<String>) msg.obj);
                    break;
                case MSG_DOUBLE_CLICK_REFRESH:
                    if (bottomBarViewGroup != null) {
                        bottomBarViewGroup.expressionFlag = 0;
                    }
                    mHandler.removeMessages(MSG_DOUBLE_CLICK_REFRESH);
                    mHandler.sendEmptyMessageDelayed(MSG_DOUBLE_CLICK_REFRESH, 3 * Global.metaRefreshTime);
                    break;
                case MSG_KERNEL_CLEAN:
                    mHandler.removeMessages(MSG_KERNEL_CLEAN);
                    break;
            }

            super.handleMessage(msg);
        }
    };

    /**
     * 功能：，首先加载输入法词典，并初始化内核参数，并对界面元素进行创建
     * 调用时机：输入法服务创建时调用
     */
    @Override
    public void onCreate() {
        /*
         * 加载内核
		 */
        Log.i("Test","onCreate");
        NKInitDictFile.NKInitWiDict(this);
        initInputParam = new InitInputParam();

		/*
         * 初始化SharedPreferences数据
		 */
        SharedPreferenceManager.initSharedPreferencesData(this);//初始化系统默认的点划设置

        iniComponent();

        GenerateMessage gm = new GenerateMessage(this, 1);
        gm.generate();
        screenInfoC.refreshScreenInfo();
        KeyBoardCreate keyBoardCreate = new KeyBoardCreate();
        keyBoardCreate.createKeyboard();
        transparencyHandle.startAutoDownAlpha();
        super.onCreate();
    }

    private void iniComponent() {
        res = getResources();
        keyBoardTouchEffect = new KeyBoardTouchEffect(this);
        specialSymbolChooseViewGroup = new SpecialSymbolChooseViewGroup();
        functionViewGroup = new FunctionViewGroup();
        quickSymbolViewGroup = new QuickSymbolViewGroup();
        preFixViewGroup = new PreFixViewGroup();
        bottomBarViewGroup = new BottomBarViewGroup();
        qkCandidatesViewGroup = new QKCandidatesViewGroup();
        qkInputViewGroups = new QKInputViewGroups();
        t9InputViewGroup = new T9InputViewGroup();
        preEditPopup = new PreEditPopup();
        lightViewManager = new LightViewManager();

        secondLayerLayout = new LinearLayout(this);
        keyboardLayout = new LinearLayout(this);
        keyboardLayout.setOrientation(LinearLayout.VERTICAL);
        skinInfoManager = SkinInfoManager.getSkinInfoManagerInstance();
        symbolsManager = new SymbolsManager(this);
    }

    /**
     * 功能：钩子函数，在配置改变时调用会被系统调用，可以在此针对不同的屏幕方向，加载相应的配置文件
     * 调用时机：屏幕旋转方向时调用
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        screenInfoC.refreshScreenInfo();
        screenInfoC.LoadKeyboardSizeInfoFromSharedPreference();

        viewSizeUpdate.updateViewSizeAndPosition();

        if (mWindowShown) {
            updateWindowManager();
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onUpdateSelection(int oldSelStart, int oldSelEnd,
                                  int newSelStart, int newSelEnd,
                                  int candidatesStart, int candidatesEnd) {
        this.mNewStart = newSelStart;
        this.mNewEnd = newSelEnd;
        this.mCandicateStart = candidatesStart;
        this.mCandicateEnd = candidatesEnd;
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd,
                candidatesStart, candidatesEnd);
    }

    /**
     * 功能：服务销毁时调用的钩子函数，可以在此释放内存
     * 调用时机：服务结束时调用
     */
    @Override
    public void onDestroy() {
        Log.i("Test","onDestroy");
        viewManagerC.removeInputView();
        lightViewManager.removeView();
        Kernel.cleanKernel();
        Kernel.resetWiIme(InitInputParam.RESET);
        Kernel.freeIme();
        super.onDestroy();
    }

    /**
     * 功能：编辑功能，负责向内核传递字符，句内编辑也是在这里做
     * 调用时机：{@link #mHandler}处理{@link #MSG_SEND_TO_KERNEL}时调用
     * @author huangzhiwei
     * @auther purebluesong
     * @param s 向内核传入的字符,delete 是否删除操作
     */
    public void innerEdit(String s, boolean delete) {
        if (Global.currentKeyboard != Global.KEYBOARD_QP && Global.currentKeyboard != Global.KEYBOARD_EN) return ;
        mQPOrEmoji = Global.QUANPIN;
        final int selectionStart = Math.min(mNewStart, mNewEnd);
        final int selectionEnd = Math.max(mNewStart, mNewEnd);
        final int candicateStart = Math.min(mCandicateStart, mCandicateEnd);
        final int candicateEnd = Math.max(mCandicateStart, mCandicateEnd);
        preEditPopup.setCursor(selectionStart,selectionEnd);
        if (selectionStart <= candicateStart || selectionStart > candicateEnd) {
            if (delete) {
                this.sendDownUpKeyEvents(KeyEvent.KEYCODE_DEL);
                return;
            }
        }
        if (candicateEnd <= selectionStart) {
            if (delete) {
                Kernel.deleteAction();
            } else {
                Kernel.inputPinyin(s);
            }
            qkInputViewGroups.refreshQKKeyboardPredict();
            refreshDisplay();
            return;
        }
        final InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        //开始处理真正的句内编辑
        final String candicateString = Kernel.getWordsShowPinyin();
        if (candicateString.length() != candicateEnd - candicateStart) return;
        //切割字符串
        final int isDel = delete && selectionStart == selectionEnd ? 1 : 0;
        String sBegin = selectionStart > candicateStart ? candicateString.substring(0, selectionStart - candicateStart - isDel).replace("'", "") : "";
        sBegin += s;
        String sEnd = selectionEnd <= candicateStart ? candicateString : (selectionEnd < candicateEnd ? candicateString.substring(selectionEnd - candicateStart).replace("'", "") : "");
        int i, j;
        if (sBegin.length() > 0 && Character.getType(sBegin.charAt(0)) != Character.OTHER_LETTER) {
            final String sylla = sBegin + sEnd;
            Kernel.cleanKernel();
            Kernel.inputPinyin(sylla);
            refreshDisplay();
            qkInputViewGroups.refreshQKKeyboardPredict();
            String r = Kernel.getWordsShowPinyin();
            for (i = 0, j = 0; i < sBegin.length() && j < r.length(); i++) {
                if (r.charAt(j) == '\'')
                    j++;
                j++;
            }
            ic.setComposingText(r, 1);
            ic.setSelection(candicateStart + j, candicateStart + j);
            return ;
        } else if ((sBegin.length() == 0) && (sEnd.length() > 0 ? Character.getType(sEnd.charAt(0)) != Character.OTHER_LETTER : true)) {
            Kernel.cleanKernel();
            Kernel.inputPinyin(sEnd);
            qkInputViewGroups.refreshQKKeyboardPredict();
            refreshDisplay();
            ic.setSelection(0, 0);
            //未上屏字符中有中文
        } else {
            for (i = 0; i < sBegin.length() && Character.getType(sBegin.charAt(i)) == Character.OTHER_LETTER; i++)
            ;
            // 光标前是汉字
            if (i == sBegin.length() || (i == sBegin.length() - 1 && !delete)) {
                for (j = 0; j < sEnd.length()
                        && Character.getType(sEnd.charAt(j)) == Character.OTHER_LETTER; j++)
                    ;
                if (j != 0) {
                    if (!delete) {
                        Kernel.inputPinyin(s);
                        qkInputViewGroups.refreshQKKeyboardPredict();
                        refreshDisplay();
                        return ;
                    }
                }
                if (delete) {
                    ic.commitText(sBegin, 1);
                    ic.commitText(sEnd.substring(0, j), 1);
                    final String sylla = sEnd.substring(j);
                    Kernel.cleanKernel();
                    Kernel.inputPinyin(sylla);
                    qkInputViewGroups.refreshQKKeyboardPredict();
                    refreshDisplay();
                    String r = Kernel.getWordsShowPinyin();
                    ic.setComposingText(r, 1);
                    ic.setSelection(candicateStart + i + j, candicateStart + i + j);
                    return ;
                }
                if (!delete && j == 0) {
                    if (sBegin.length() > 0) {
                        ic.commitText(sBegin.substring(0, sBegin.length() - 1), 1);
                        Kernel.cleanKernel();
                        Kernel.inputPinyin(s + sEnd);
                        qkInputViewGroups.refreshQKKeyboardPredict();
                        refreshDisplay();
                        ic.setSelection(mCandicateStart + 1, mCandicateStart + 1);
                    }
                }
            } else {
                // 光标前不是汉字
                ic.commitText(sBegin.substring(0, i), 1);
                final String sylla = sBegin.substring(i) + sEnd;
                Kernel.cleanKernel();
                Kernel.inputPinyin(sylla);
                qkInputViewGroups.refreshQKKeyboardPredict();
                refreshDisplay();
                final String r = Kernel.getWordsShowPinyin();
                int k;
                for (k = i, j = 0; k < sBegin.length()
                        && j < r.length(); k++) {
                    if (r.charAt(j) == '\'')
                        j++;
                    j++;
                }
                ic.setComposingText(r, 1);
                ic.setSelection(candicateStart + i + j, candicateStart + i + j);
            }
        }
    }

    /**
     * 功能：更新键盘的尺寸视图
     * 调用时机：初始化尺寸视图或调整键盘尺寸时
     */
    public void updateSetKeyboardSizeViewPos() {
        Rect keyboardRect = new Rect(keyboardParams.x, keyboardParams.y,
                keyboardParams.x + keyboardParams.width,
                keyboardParams.y + keyboardParams.height);
        mSetKeyboardSizeView.SetScreenInfo(mScreenWidth, mScreenHeight, mStatusBarHeight);
        mSetKeyboardSizeView.SetPos(keyboardRect);
    }

    /**
     * 每次有候选词跟新的时候统一刷新界面，因为影响到的因素比较多，统一使用状态机解决
     */
    public void refreshDisplay(boolean special) {
        Log.i("Test","refreshDisplay");
        boolean isNK = Global.currentKeyboard == Global.KEYBOARD_T9 || Global.currentKeyboard == Global.KEYBOARD_SYM;
        int candidatenum = Kernel.getWordsNumber();
        boolean hidecandidate = candidatenum != 0 ? false : (special ? false : true);
        Kernel.setKernelType(isNK?Kernel.NINE_KEY:Kernel.QWERT);

        if (mInputViewGG.isShown()) mInputViewGG.setVisibility(View.VISIBLE);
        functionViewGroup.refreshState(hidecandidate);
        functionsC.refreshStateForSecondLayout(hidecandidate);
        functionsC.refreshStateForLargeCandidate(hidecandidate);
        if (!hidecandidate) {
            viewSizeUpdate.UpdatePreEditSize();
            viewSizeUpdate.UpdateQKCandidateSize();
            viewSizeUpdate.UpdateLargeCandidateSize();
        }
        viewSizeUpdate.UpdateQuickSymbolSize();
        qkCandidatesViewGroup.refreshState(hidecandidate, isNK ? Global.EMOJI : Global.QUANPIN);
        specialSymbolChooseViewGroup.refreshState(hidecandidate);
        preFixViewGroup.refresh();
        t9InputViewGroup.refreshState();
        qkInputViewGroups.refreshState();
        quickSymbolViewGroup.refreshState(hidecandidate);
        functionsC.computeCursorPosition();
        preEditPopup.refresh();
    }

    public void refreshDisplay() {
        refreshDisplay(false);
    }

    public void deleteLast() {
        if ((Kernel.getWordsNumber() == 0) || Global.currentKeyboard == Global.KEYBOARD_SYM) {
            this.sendDownUpKeyEvents(KeyEvent.KEYCODE_DEL);
        } else {
            if (Global.currentKeyboard == Global.KEYBOARD_T9) {
                String pinyin = Kernel.getWordsShowPinyin();
                Global.addToRedo(pinyin.substring(pinyin.length()-1));
                Kernel.deleteAction();
                t9InputViewGroup.updateFirstKeyText();
                refreshDisplay();
            } else if (Global.currentKeyboard == Global.KEYBOARD_QP) {
                if (mQPOrEmoji == Global.QUANPIN) {
                    String pinyin = Kernel.getWordsShowPinyin();
                    Global.addToRedo(pinyin.substring(pinyin.length()-1));
                    innerEdit("", true);
                } else if (mQPOrEmoji == Global.EMOJI) {
                    Kernel.cleanKernel();
                    refreshDisplay(true);
                }
            } else if (Global.currentKeyboard == Global.KEYBOARD_EN) {
                Kernel.cleanKernel();
                refreshDisplay(true);
            }
        }
    }

    public void chooseWord(int index) {
        String text = Kernel.getWordSelectedWord(index);
        refreshDisplay();
        if (Global.currentKeyboard == Global.KEYBOARD_T9) {
            t9InputViewGroup.updateFirstKeyText();
        }
        if (text != null) {
            InputConnection ic = getCurrentInputConnection();
            if (ic != null) {
                Global.addToRedo(text);
                ic.commitText(text, 1);
            }
        }
        qkInputViewGroups.refreshQKKeyboardPredict();//刷新点滑预测
    }

    /**
     * 上屏,从这里传输的文字会直接填写到文本框里
     *
     * @param text
     */
    public void commitText(CharSequence text) {
        InputConnection ic = getCurrentInputConnection();
        if (text != null && ic != null) {
            Global.addToRedo(text);
            ic.commitText(text, 1);
        }
        qkInputViewGroups.refreshQKKeyboardPredict();//刷新点滑预测
    }

    /**
     * 如其名，向九键内核传递拼音串
     * @param msg
    * */
    public void sendMsgToKernel(String msg) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SEND_TO_KERNEL, msg));
    }

    /**
     * 向全键内核传递拼音串
     *
     * @param msg
     * */
    public void sendMsgToQKKernel(String msg) {
        mHandler.sendMessage(mHandler.obtainMessage(QP_MSG_SEND_TO_KERNEL, msg));
    }

    public OnChangeListener mOnSizeChangeListener = new OnChangeListener() {

        public void onSizeChange(Rect keyboardRect) {
            keyboardWidth = keyboardRect.width();
            keyboardHeight = keyboardRect.height();

            keyboardParams.x = keyboardRect.left;
            keyboardParams.y = keyboardRect.top;
            keyboardParams.width = keyboardWidth;
            keyboardParams.height = keyboardHeight;

            standardHorizantalGapDistance = keyboardWidth * 2 / 100;
            standardVerticalGapDistance = keyboardHeight * 2 / 100;

            viewSizeUpdate.updateViewSizeAndPosition();
            updateWindowManager();
        }

        public void onFinishSetting() {
            screenInfoC.WriteKeyboardSizeInfoToSharedPreference();
            viewManagerC.removeSetKeyboardSizeView();
        }

        public void onPosChange(Rect keyboardRect) {
            keyboardParams.x = keyboardRect.left % mScreenWidth;
            keyboardParams.y = keyboardRect.top % mScreenHeight;
            viewSizeUpdate.UpdateLightSize();//因为是独立的，所以这里要独立重定位
            screenInfoC.WriteKeyboardSizeInfoToSharedPreference();
            updateWindowManager();
        }

        public void onResetSetting() {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SoftKeyboard.this);
            Editor edit = sp.edit();
            edit.remove(KEYBOARD_HEIGHT_S + orientation);
            edit.remove(KEYBOARD_WIDTH_S + orientation);
            edit.remove(KEYBOARD_X_S + orientation);
            edit.remove(KEYBOARD_Y_S + orientation);

            edit.remove(FULL_WIDTH_S + orientation);
            edit.remove(FULL_WIDTH_X_S + orientation);
            edit.commit();
            screenInfoC.LoadKeyboardSizeInfoFromSharedPreference();

            viewSizeUpdate.updateViewSizeAndPosition();

            updateSetKeyboardSizeViewPos();
            mSetKeyboardSizeView.invalidate();
        }
    };

    @SuppressWarnings("deprecation")
    public void updateWindowManager() {
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
        wm.updateViewLayout(keyboardLayout, keyboardParams);
    }

    public void switchKeyboardTo(int keyboard, boolean showAnim) {
        keyBoardSwitcher.switchKeyboard(keyboard, showAnim);
    }

    /**
     * 键盘切换类，实际运用基本只调用其中的SwitchKeyBoard
     */
    public class KeyBoardSwitcherC {

        /**
         * 功能：使键盘占满屏幕的Width
         * 调用时机：从九键切到英文键盘
         */
        private void fullWidth() {
            quickSymbolViewGroup.setSize(keyboardWidth - 2 * standardHorizantalGapDistance, ViewGroup.LayoutParams.MATCH_PARENT);
            preFixViewGroup.setSize(keyboardWidth * -2 * standardHorizantalGapDistance, ViewGroup.LayoutParams.MATCH_PARENT);
            updateWindowManager();
        }

        /**
         * 功能：使键盘宽度不占满整个Width
         * 调用时机：英文键盘切向九键
         */
        private void fullWidthBack() {
            quickSymbolViewGroup.setSize(keyboardWidth * res.getInteger(R.integer.PREEDIT_WIDTH) / 100, ViewGroup.LayoutParams.MATCH_PARENT);
            preFixViewGroup.setSize(keyboardWidth * res.getInteger(R.integer.PREEDIT_WIDTH) / 100, ViewGroup.LayoutParams.MATCH_PARENT);
            updateWindowManager();
        }

        private void hideKeyboard(int keyboard, boolean showAnim) {
            switch (Global.currentKeyboard) {
                case Global.KEYBOARD_QP:
                case Global.KEYBOARD_EN:
                    if (keyboard != Global.KEYBOARD_QP && keyboard != Global.KEYBOARD_EN) {
                        qkInputViewGroups.startHideAnimation(showAnim);
                    }
                    break;
                default:
                    if (keyboard == Global.KEYBOARD_NUM || keyboard == Global.KEYBOARD_SYM) {
                        t9InputViewGroup.T9ToNum(showAnim);
                    } else {
                        t9InputViewGroup.hideT9(showAnim);
                        fullWidth();
                    }
                    break;
            }
            if (preFixViewGroup.isShown()) preFixViewGroup.setVisibility(View.GONE);
        }

        private void showKeyboard(int keyboard, boolean showAnim) {
            switch (keyboard) {
                case Global.KEYBOARD_QP:
                case Global.KEYBOARD_EN:
                    if (Global.currentKeyboard != Global.KEYBOARD_QP && Global.currentKeyboard != Global.KEYBOARD_EN) {
                        qkInputViewGroups.startShowAnimation(showAnim);
                    }
                    break;
                default:
                    t9InputViewGroup.showT9(showAnim);
                    fullWidthBack();
                    break;
            }
            quickSymbolViewGroup.updateCurrentSymbolsAndSetTheContent(keyboard);
        }

        /**
         * 功能：切换键盘
         * 调用时机：首次弹出键盘或者按下键盘上相应的切换键时调用
         *
         * @param keyboard 要切换到的键盘
         * @param showAnim 是否播放动画
         */
        public void switchKeyboard(int keyboard, boolean showAnim) {
            qkInputViewGroups.reloadPredictText(keyboard);
            Kernel.cleanKernel();

            hideKeyboard(keyboard, showAnim);
            showKeyboard(keyboard, showAnim);

            Global.currentKeyboard = keyboard;
            refreshDisplay();
            bottomBarViewGroup.switchToKeyboard(keyboard);
            viewSizeUpdate.updateViewSizeAndPosition();
        }
    }

    /**
     * 一些工具函数集中存放的地方
     */
    public class  FunctionsC {
        //For Listeners
        public void deleteAll() {
            if (Kernel.getWordsNumber()==0 || Global.currentKeyboard == Global.KEYBOARD_SYM) {
                InputConnection ic = SoftKeyboard.this.getCurrentInputConnection();
                if (ic != null) {
                    Global.redoTextForDeleteAll = ic.getTextBeforeCursor(200,0);
                    ic.deleteSurroundingText(Integer.MAX_VALUE, 0);
                }
            } else {
                Global.redoTextForDeleteAll_preedit = Kernel.getWordsShowPinyin();
                Global.redoTextForDeleteAll = "";
            }
            Kernel.cleanKernel();
            if (Global.currentKeyboard == Global.KEYBOARD_T9 || Global.currentKeyboard == Global.KEYBOARD_SYM || Global.currentKeyboard == Global.KEYBOARD_NUM) {
                t9InputViewGroup.updateFirstKeyText();
                refreshDisplay();
            } else if (Global.currentKeyboard == Global.KEYBOARD_QP) {
                refreshDisplay(mQPOrEmoji != Global.QUANPIN);
            } else if (Global.currentKeyboard == Global.KEYBOARD_EN) {
                refreshDisplay();
            }
            qkInputViewGroups.refreshQKKeyboardPredict();//刷新点滑预测
        }

        //For others
        public void computeCursorPosition() {
            //计算光标位置
            int cursor = Kernel.getWordsShowPinyin() == null ? 0 : Kernel.getWordsShowPinyin().length();
            //上屏
            InputConnection ic = getCurrentInputConnection();
            if (ic != null) {
                ic.beginBatchEdit();
                ic.setComposingText(Kernel.getWordsShowPinyin(), cursor);
                ic.endBatchEdit();
            }
        }

        /**
         * 功能：判断当前输入框是否要输入网址，为了显示相应的字符
         * 调用时机：切换到符号键盘时调用
         *
         * @param ei 输入框信息
         * @return 是否要输入网址
         */
        private boolean isToShowUrl(EditorInfo ei) {
            if (ei != null) {
                if ((ei.inputType & EditorInfo.TYPE_MASK_CLASS) == EditorInfo.TYPE_CLASS_TEXT
                        && (EditorInfo.TYPE_MASK_VARIATION & ei.inputType) == EditorInfo.TYPE_TEXT_VARIATION_URI) {
                    return true;
                }
                if ((ei.imeOptions & (EditorInfo.IME_MASK_ACTION | EditorInfo.IME_FLAG_NO_ENTER_ACTION)) == EditorInfo.IME_ACTION_GO) {
                    return true;
                }
            }
            return false;
        }

        /**
         * 功能：判断输入框是否要输入邮箱
         * 调用时机：切换为符号键盘
         *
         * @param ei
         * @return
        */
        private boolean isToShowEmail(EditorInfo ei) {
            if (ei != null) {
                if ((ei.inputType & EditorInfo.TYPE_MASK_CLASS) == EditorInfo.TYPE_CLASS_TEXT
                        && (EditorInfo.TYPE_MASK_VARIATION & ei.inputType) == EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS) {
                    return true;
                }
            }
            return false;
        }

        /**
         * 功能：调用候选框需要显示的符号集
         * 调用时机：切换为符号键盘
         */
        public final void showDefaultSymbolSet() {
            final EditorInfo ei = SoftKeyboard.this.getCurrentInputEditorInfo();
            if (isToShowUrl(ei)) {
                sendMsgToKernel("'u");
            } else if (isToShowEmail(ei)) {
                sendMsgToKernel("'e");
            } else {
                sendMsgToKernel("'");
            }
        }

        public void refreshStateForSecondLayout(boolean show) {
            if (Global.inLarge) {
                secondLayerLayout.setVisibility(View.GONE);
            } else {
                secondLayerLayout.setVisibility(View.VISIBLE);
            }
        }

        public void refreshStateForLargeCandidate(boolean show) {
            if (Global.inLarge || show) {
                largeCandidateButton.setVisibility(View.GONE);
            } else {
                largeCandidateButton.setVisibility(View.VISIBLE);
            }
            largeCandidateButton.getBackground().setAlpha((int) (Global.mCurrentAlpha * 255));
        }

        public void updateSkin(TextView v, int textcolor, int backgroundcolor) {
            v.setTextColor(textcolor);
            //v.setBackgroundColor(backgroundcolor);
            v.getBackground().setColorFilter(backgroundcolor, PorterDuff.Mode.SRC);
            v.getBackground().setAlpha((int) (Global.mCurrentAlpha * 255));
        }

        /**
         * 判断输入域类型，返回确定键盘类型
         */
        private final int getKeyboardType(EditorInfo pEditorInfo) {
            int keyboardType;
            // 判断输入框类型,选择对应的键盘
            switch (pEditorInfo.inputType & EditorInfo.TYPE_MASK_CLASS) {
                case EditorInfo.TYPE_CLASS_NUMBER:
                case EditorInfo.TYPE_CLASS_DATETIME:
                case EditorInfo.TYPE_CLASS_PHONE:
                    keyboardType = Global.KEYBOARD_NUM;
                    break;
                case EditorInfo.TYPE_CLASS_TEXT:
                    switch (EditorInfo.TYPE_MASK_VARIATION & pEditorInfo.inputType) {
                        case EditorInfo.TYPE_TEXT_VARIATION_URI:
                        case EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS:
                        case EditorInfo.TYPE_TEXT_VARIATION_PASSWORD:
                        case EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD:
                            keyboardType = Global.KEYBOARD_EN;
                            break;
                        default:
                            keyboardType = zhKeyboard;
                            break;
                    }
                    break;
                default:// 默认为英文键盘——
                    keyboardType = zhKeyboard;
                    break;
            }
            return keyboardType;
        }
    }

    /**
     * 存放listener
     */
    private class Listeners {
        OnTouchListener largeCandidateOnTouchListener = new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                transparencyHandle.handleAlpha(motionEvent.getAction());
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    qkCandidatesViewGroup.displayCandidates();
                    qkCandidatesViewGroup.largeTheCandidate();
                    Global.inLarge = true;
                    bottomBarViewGroup.intoReturnState();
                }
                keyBoardTouchEffect.onTouchEffectWithAnim(view, motionEvent.getAction(),
                        skinInfoManager.skinData.backcolor_touchdown,
                        skinInfoManager.skinData.backcolor_quickSymbol,
                        SoftKeyboard.this);
                return false;
            }
        };
    }

    /**
     * 各种view的创建函数，不初始化位置
     */
    private class KeyBoardCreate {

        /**
         * 加载资源
         */
        private void LoadResources() {
            Resources res = getResources();
            mTypeface = Typeface.createFromAsset(getAssets(), res.getString(R.string.font_file_path));// 加载自定义字体
            mFuncKeyboardText = res.getStringArray(R.array.FUNC_KEYBOARD_TEXT);

            Global.shadowRadius = res.getInteger(R.integer.SHADOW_RADIUS);

            // 初始化，具体加载在onWindowShown里面实现
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SoftKeyboard.this);
            Global.slideDeleteSwitch = sharedPreferences.getBoolean("SLIDE_DELETE_CHECK", false);
            Global.shadowSwitch = sharedPreferences.getBoolean("SHADOW_TEXT_CHECK", true);
            /*
            * 设置当前键盘皮肤
            */
            int themeType = sharedPreferences.getInt("THEME_TYPE", 0);
            boolean isDiy = sharedPreferences.getBoolean("IS_DIY", false);
            if (isDiy) {
                skinInfoManager.loadConfigurationFromDIY(SoftKeyboard.this);
            } else {
                skinInfoManager.loadConfigurationFromXML(themeType, res);
            }
            Global.currentSkinType = themeType;
        }

        private void CreateCandidateView() {
            qkCandidatesViewGroup.setSoftKeyboard(SoftKeyboard.this);
            qkCandidatesViewGroup.create(SoftKeyboard.this);
            qkCandidatesViewGroup.addThisToView(keyboardLayout);
        }

        private void CreatePreEditView() {
            preEditPopup.setSoftKeyboard(SoftKeyboard.this);
            preEditPopup.create(SoftKeyboard.this);
        }

        /**
         * 功能：点滑产生光效果
         */
        private void CreateLightView() {
            lightViewManager.setSoftkeyboard(SoftKeyboard.this);
            lightViewManager.create(SoftKeyboard.this);
            lightViewManager.setTypeface(mTypeface);
        }

        private void CreateFuncKey() {
            functionViewGroup.setSoftKeyboard(SoftKeyboard.this);
            functionViewGroup.create(SoftKeyboard.this);
            functionViewGroup.setTypeface(mTypeface);
            functionViewGroup.addThisToView(keyboardLayout);
        }

        private void CreatePrefixView() {
            preFixViewGroup.setSoftKeyboard(SoftKeyboard.this);
            preFixViewGroup.create(SoftKeyboard.this);
            preFixViewGroup.addThisToView(secondLayerLayout);
        }

        private void CreateQuickSymbol() {
            quickSymbolViewGroup.setSoftKeyboard(SoftKeyboard.this);
            quickSymbolViewGroup.setTypeface(mTypeface);
            quickSymbolViewGroup.create(SoftKeyboard.this);
            quickSymbolViewGroup.updateCurrentSymbolsAndSetTheContent(Global.currentKeyboard);
            quickSymbolViewGroup.addThisToView(secondLayerLayout);
        }

        private void CreateSpecialSymbolChoose() {
            specialSymbolChooseViewGroup.setSoftKeyboard(SoftKeyboard.this);
            specialSymbolChooseViewGroup.create(SoftKeyboard.this);
            specialSymbolChooseViewGroup.setTypeface(mTypeface);
            specialSymbolChooseViewGroup.addThisToView(secondLayerLayout);
        }

        private void CreateSetKeyboardSizeView() {
            mSetKeyboardSizeView = new SetKeyboardSizeView(SoftKeyboard.this, mOnSizeChangeListener);
            mSetKeyboardSizeView.SetTypeface(mTypeface);
            mSetKeyboardSizeView.SetMovingIcon((String) functionViewGroup.buttonList.get(0).getText());
        }

        private void CreateBottomBarViewGroup() {
            bottomBarViewGroup.setSoftKeyboard(SoftKeyboard.this);
            bottomBarViewGroup.create(SoftKeyboard.this);
            bottomBarViewGroup.setTypeFace(mTypeface);
            bottomBarViewGroup.addThisToView(keyboardLayout);
        }

        private void CreateLargeButton() {
            largeCandidateButton = new QuickButton(SoftKeyboard.this);
            largeCandidateButton.setTextColor(skinInfoManager.skinData.textcolors_quickSymbol);
            largeCandidateButton.setBackgroundColor(skinInfoManager.skinData.backcolor_quickSymbol);
            largeCandidateButton.getBackground().setAlpha((int) (Global.mCurrentAlpha * 255));
            largeCandidateButton.setTypeface(mTypeface);
            largeCandidateButton.setText(res.getString(R.string.largecandidate));
            largeCandidateButton.setOnTouchListener(listeners.largeCandidateOnTouchListener);
            if (Global.shadowSwitch)
                largeCandidateButton.setShadowLayer(Global.shadowRadius, 0, 0, skinInfoManager.skinData.shadow);
            largeCandidateButton.setVisibility(View.GONE);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 0);
            largeCandidateButton.itsLayoutParams = params;

            secondLayerLayout.addView(largeCandidateButton, params);
        }

        private void CreateEnglishKeyboard() {
            qkInputViewGroups.setSoftKeyboard(SoftKeyboard.this);
            qkInputViewGroups.create(SoftKeyboard.this);
            qkInputViewGroups.setTypeface(mTypeface);
            qkInputViewGroups.addThisToView(mInputViewGG);
        }

        private void CreateT9Keyboard() {
            t9InputViewGroup.setSoftKeyboard(SoftKeyboard.this);
            t9InputViewGroup.create(SoftKeyboard.this);
            t9InputViewGroup.setTypeface(mTypeface);
            t9InputViewGroup.addThisToView(mInputViewGG);
        }

        private void createKeyboard() {
            if (keyboardLayout.getChildCount() < 1) {
                LoadResources();
                CreateFuncKey();
                CreatePreEditView();
                CreateCandidateView();
                keyboardLayout.addView(secondLayerLayout, secondParams);
                CreatePrefixView();
                CreateQuickSymbol();
                CreateSpecialSymbolChoose();
                CreateLargeButton();
                mInputViewGG = new LinearLayout(SoftKeyboard.this);
                mGGParams = new LinearLayout.LayoutParams(0, 0);
                CreateT9Keyboard();
                CreateEnglishKeyboard();
                keyboardLayout.addView(mInputViewGG, mGGParams);
                CreateBottomBarViewGroup();
                CreateLightView();
                CreateSetKeyboardSizeView();
            }
        }
    }

    /**
     * 整合所有view大小和位置更新方法到一个内部类中
     */
    public class ViewSizeUpdateC {
        private int[] layerHeightRate;

        private void UpdatePrefixSize() {
            preFixViewGroup.setPosition(0, 0);
            if (Global.currentKeyboard == Global.KEYBOARD_NUM || Global.currentKeyboard == Global.KEYBOARD_SYM || Global.currentKeyboard == Global.KEYBOARD_T9) {
                preFixViewGroup.setSize(keyboardWidth * 44 / 100, keyboardHeight * layerHeightRate[1] / 100);
            } else {
                preFixViewGroup.setSize(keyboardWidth - standardHorizantalGapDistance, keyboardHeight * layerHeightRate[1] / 100);
            }
            preFixViewGroup.setButtonPadding(standardHorizantalGapDistance);
            preFixViewGroup.updateViewLayout();
        }

        private void UpdatePreEditSize() {
            preEditPopup.upadteSize(keyboardParams.width-2*standardHorizantalGapDistance,
                    (int) (0.5*layerHeightRate[0]*keyboardParams.height/100),
                    standardHorizantalGapDistance);
        }

        private void UpdateEnglishKeyboardSize() {
            qkInputViewGroups.setPosition(0, 0);
            qkInputViewGroups.setSize(
                    keyboardWidth,
                    keyboardHeight * layerHeightRate[2] / 100,
                    standardHorizantalGapDistance
            );
            qkInputViewGroups.updateViewLayout();
        }

        private void UpdateQuickSymbolSize() {
            int height = keyboardHeight * layerHeightRate[1] / 100;
            int width = keyboardWidth - 2 * standardHorizantalGapDistance;
            int buttonWidth = width / 6;
            quickSymbolViewGroup.setPosition(0, 0);
            quickSymbolViewGroup.setButtonPadding(standardHorizantalGapDistance);
            quickSymbolViewGroup.setButtonWidth(buttonWidth);
            float textSize = 2 * Math.min(buttonWidth, height) / 5;
            quickSymbolViewGroup.setTextSize(textSize);
            if (t9InputViewGroup.deleteButton.isShown() && largeCandidateButton.isShown()) {
                quickSymbolViewGroup.setSize(keyboardWidth * 44 / 100, height);
            } else if (t9InputViewGroup.deleteButton.isShown() || largeCandidateButton.isShown()) {
                quickSymbolViewGroup.setSize(keyboardWidth * res.getInteger(R.integer.PREEDIT_WIDTH) / 100, height);
            } else {
                quickSymbolViewGroup.setSize(width, height);
            }
            quickSymbolViewGroup.updateViewLayout();
        }

        private void UpdateSpecialSymbolChooseSize() {
            int height = keyboardHeight * layerHeightRate[1] / 100;
            specialSymbolChooseViewGroup.updateSize(keyboardWidth * res.getInteger(R.integer.PREEDIT_WIDTH) / 100, height);
            specialSymbolChooseViewGroup.setPosition(0, 0);
            specialSymbolChooseViewGroup.setTextSize(2 * height / 5);
        }

        private void UpdateT9Size() {
            UpdateT9Layout();
        }

        private void UpdateLightSize() {
            lightViewManager.setSize(keyboardWidth, keyboardHeight * layerHeightRate[2] / 100,
                    keyboardParams.x,
                    keyboardParams.y + keyboardHeight * (layerHeightRate[0] + layerHeightRate[1]) / 100 +2*standardVerticalGapDistance //计算y位置
            );
        }

        private void UpdateFunctionsSize() {
            int height = keyboardHeight * layerHeightRate[0] / 100;
            int buttonwidth = keyboardWidth / 5 - standardHorizantalGapDistance * 6 / 5;
            functionViewGroup.updatesize(keyboardWidth, height);
            functionViewGroup.setButtonPadding(standardHorizantalGapDistance);
            functionViewGroup.setButtonWidth(buttonwidth);
            functionViewGroup.setTextSize(50 * Math.min(buttonwidth, height) / 100);
        }

        public void UpdateQKCandidateSize() {
            qkCandidatesViewGroup.setPosition(standardHorizantalGapDistance, 0);
            qkCandidatesViewGroup.setSize(keyboardWidth - 2 * standardHorizantalGapDistance, keyboardHeight * layerHeightRate[0] / 100);
            qkCandidatesViewGroup.updateViewLayout();
        }

        private void UpdateBottomBarSize() {

            bottomBarViewGroup.setPosition(0, standardVerticalGapDistance);
            bottomBarViewGroup.setSize(keyboardWidth - standardHorizantalGapDistance, keyboardHeight * layerHeightRate[3] / 100);
            bottomBarViewGroup.setButtonPadding(standardHorizantalGapDistance);
            if (Global.currentKeyboard == Global.KEYBOARD_NUM) {
                bottomBarViewGroup.setButtonWidthByRate(res.getIntArray(R.array.BOTTOMBAR_NUM_KEY_WIDTH));
            } else {
                bottomBarViewGroup.setButtonWidthByRate(res.getIntArray(R.array.BOTTOMBAR_KEY_WIDTH));
            }
            bottomBarViewGroup.setTextSize();
            bottomBarViewGroup.updateViewLayout();
        }

        private void UpdateT9Layout() {
            mGGParams.topMargin = standardVerticalGapDistance;
            mGGParams.height = keyboardHeight * layerHeightRate[2] / 100;
            mGGParams.width = keyboardWidth;
            t9InputViewGroup.setSize(keyboardWidth, mGGParams.height,
                    standardHorizantalGapDistance);
            t9InputViewGroup.deleteButton.getPaint().setTextSize(
                    3 * Math.min(t9InputViewGroup.deleteButton.itsLayoutParams.width, layerHeightRate[1] * keyboardHeight / 100) / 5);
            keyboardLayout.updateViewLayout(mInputViewGG, mGGParams);
        }

        private void UpdateLargeCandidateSize() {
            largeCandidateButton.itsLayoutParams.height = LayoutParams.MATCH_PARENT;
            largeCandidateButton.itsLayoutParams.width = keyboardWidth - 3 * standardHorizantalGapDistance - res.getInteger(R.integer.PREEDIT_WIDTH) * keyboardWidth / 100;
            ((LinearLayout.LayoutParams) largeCandidateButton.itsLayoutParams).leftMargin = standardHorizantalGapDistance;

            float textsize = Math.min(3 * Math.min(secondParams.height, (100 - res.getInteger(R.integer.PREEDIT_WIDTH)) * keyboardWidth / 100) / 5, 30);
            largeCandidateButton.getPaint().setTextSize(textsize);
        }

        public void updateViewSizeAndPosition() {
            layerHeightRate = res.getIntArray(R.array.LAYER_HEIGHT);
            UpdateT9Size();
            secondParams.height = keyboardHeight * layerHeightRate[1] / 100;
            secondParams.topMargin = standardVerticalGapDistance;
            secondParams.leftMargin = standardHorizantalGapDistance;
            UpdateQKCandidateSize();
            UpdatePreEditSize();
            UpdateEnglishKeyboardSize();
            UpdatePrefixSize();
            UpdateFunctionsSize();
            UpdateLargeCandidateSize();
            UpdateQuickSymbolSize();
            UpdateSpecialSymbolChooseSize();
            UpdateBottomBarSize();
            UpdateLightSize();
        }
    }

    /**
     * 整合了皮肤更新的所有方法到一个内部类中
     */
    public class SkinUpdateC {
        /**
         * 功能：更新键盘皮肤和透明度
         * 调用时机：每次唤出键盘
         */
        public void updateSkin() {
            /*
             * 设置当前键盘皮肤
             */
            int themeType = PreferenceManager.getDefaultSharedPreferences(SoftKeyboard.this).getInt("THEME_TYPE", 0);
            boolean isDiy = PreferenceManager.getDefaultSharedPreferences(SoftKeyboard.this).getBoolean("IS_DIY", false);
            if (themeType != Global.currentSkinType) {
                skinInfoManager.loadConfigurationFromXML(themeType, res);
            }
            if (isDiy) {
                skinInfoManager.loadConfigurationFromDIY(SoftKeyboard.this);
            }

            qkCandidatesViewGroup.updateSkin();
            t9InputViewGroup.updateSkin();
            preEditPopup.updateSkin();
            preFixViewGroup.updateSkin();
            specialSymbolChooseViewGroup.updateSkin();
            quickSymbolViewGroup.updateSkin();
            qkInputViewGroups.updateSkin();
            functionViewGroup.updateSkin();
            bottomBarViewGroup.updateSkin();
            functionsC.updateSkin(largeCandidateButton, skinInfoManager.skinData.textcolors_quickSymbol, skinInfoManager.skinData.backcolor_quickSymbol);
            if (keyboardLayout.getBackground() != null)
                keyboardLayout.getBackground().setAlpha((int) (Global.keyboardViewBackgroundAlpha * 255));
            else {
                keyboardLayout.setBackgroundResource(R.drawable.blank);

                keyboardLayout.setBackgroundColor(skinInfoManager.skinData.backcolor_touchdown);
                //keyboardLayout.getBackground().setColorFilter(skinInfoManager.skinData.backcolor_touchdown, PorterDuff.Mode.SRC);
                keyboardLayout.getBackground().setAlpha((int) (Global.keyboardViewBackgroundAlpha * 255));
            }
        }

        public void updateShadowLayer() {
            qkCandidatesViewGroup.setShadowLayer(Global.shadowRadius, skinInfoManager.skinData.shadow);
            preFixViewGroup.setShadowLayer(Global.shadowRadius, skinInfoManager.skinData.shadow);
            quickSymbolViewGroup.setShadowLayer(Global.shadowRadius, skinInfoManager.skinData.shadow);
            functionViewGroup.setShadowLayer(Global.shadowRadius, skinInfoManager.skinData.shadow);
            bottomBarViewGroup.setShadowLayer(Global.shadowRadius, skinInfoManager.skinData.shadow);
            specialSymbolChooseViewGroup.setShadowLayer(Global.shadowRadius, skinInfoManager.skinData.shadow);
            largeCandidateButton.setShadowLayer(Global.shadowRadius, 0, 0, skinInfoManager.skinData.shadow);
        }
    }

    /**
     * 屏幕信息处理类
     */
    public class ScreenInfoC {

        /**
         * 功能：刷新界面信息
         * 调用时机：在初始化和旋转手机方向时需要调用以及时刷新信息
         */
        private void refreshScreenInfo() {
            WindowManager wm = (WindowManager) getApplicationContext()
                    .getSystemService(WINDOW_SERVICE);
            Display dis = wm.getDefaultDisplay();
            mScreenWidth = dis.getWidth();
            mScreenHeight = dis.getHeight();
            orientation = mScreenWidth > mScreenHeight ? ORI_HOR : ORI_VER;
            mStatusBarHeight = getStatusBarHeight();

            DEFAULT_KEYBOARD_Y = mScreenHeight / 3;
            DEFAULT_KEYBOARD_WIDTH = mScreenWidth * 2 / 3;
            DEFAULT_KEYBOARD_HEIGHT = mScreenHeight / 2;

            DEFAULT_FULL_WIDTH = mScreenWidth;
            DEFAULT_FULL_WIDTH_X = 0;

            if (mLeftHand) {
                DEFAULT_KEYBOARD_X = DEFAULT_KEYBOARD_WIDTH;
            } else {
                DEFAULT_KEYBOARD_X = mScreenWidth / 3;
            }
        }

        public int getStatusBarHeight() {
            int result = 0;
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = getResources().getDimensionPixelSize(resourceId);
            }
            return result;
        }

        /**
         * 功能：从SharedPreference中加载键盘的尺寸信息
         * 调用时机：初始化或旋转手机方向
         */
        private void LoadKeyboardSizeInfoFromSharedPreference() {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SoftKeyboard.this);

            /**
             * 若为英文键盘，则为FULL_WIDTH
             */
//            if (zhKeyboard == Global.KEYBOARD_QP) {
            keyboardParams.x = sp.getInt(FULL_WIDTH_X_S + orientation, DEFAULT_FULL_WIDTH_X);
            keyboardWidth = sp.getInt(FULL_WIDTH_S + orientation, DEFAULT_FULL_WIDTH);
//            } else {
//                keyboardParams.x = sp.getInt(KEYBOARD_X_S + orientation, DEFAULT_KEYBOARD_X);
//                keyboardWidth = sp.getInt(KEYBOARD_WIDTH_S + orientation, DEFAULT_KEYBOARD_WIDTH);
//            }
            keyboardParams.y = sp.getInt(KEYBOARD_Y_S + orientation, DEFAULT_KEYBOARD_Y);
            keyboardHeight = sp.getInt(KEYBOARD_HEIGHT_S + orientation, DEFAULT_KEYBOARD_HEIGHT);

            keyboardParams.width = keyboardWidth;
            keyboardParams.height = keyboardHeight;

            standardVerticalGapDistance = keyboardHeight * 2 / 100;
            standardHorizantalGapDistance = keyboardWidth * 2 / 100;

//            mCandiParams.x = sp.getInt(CANDIDATE_X_S + orientation, DEFAULT_CANDIDATE_X);
//            mCandiParams.y = sp.getInt(CANDIDATE_Y_S + orientation, DEFAULT_CANDIDATE_Y);
//            mCandiParams.width = sp.getInt(CANDIDATE_WIDTH + orientation, DEFAULT_CANDIDATE_WIDTH);
//            mCandiParams.height = sp.getInt(CANDIDATE_HEIGHT + orientation, DEFAULT_CANDIDATE_HEIGHT);
        }

        /**
         * 功能：将键盘尺寸信息写入SharedPreference
         * 调用时机：调整键盘尺寸
         */
        public void WriteKeyboardSizeInfoToSharedPreference() {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SoftKeyboard.this);
            Editor editor = sp.edit();

            editor.putInt(KEYBOARD_Y_S + orientation, keyboardParams.y);
            editor.putInt(KEYBOARD_HEIGHT_S + orientation, keyboardHeight);

//            if (mSetKeyboardSizeView.isJustKeyboard()) {
            editor.putInt(FULL_WIDTH_X_S + orientation, keyboardParams.x);
            editor.putInt(FULL_WIDTH_S + orientation, keyboardWidth);
//            } else {
//                editor.putInt(KEYBOARD_X_S + orientation, keyboardParams.x);
//                editor.putInt(KEYBOARD_WIDTH_S + orientation, keyboardWidth);
//            }
//            editor.putInt(CANDIDATE_X_S + orientation, mCandiParams.x);
//            editor.putInt(CANDIDATE_Y_S + orientation, mCandiParams.y);
//            editor.putInt(CANDIDATE_WIDTH + orientation, mCandiParams.width);
//            editor.putInt(CANDIDATE_HEIGHT + orientation, mCandiParams.height);
            editor.commit();
        }
    }

    /**
     * 键盘透明度处理，包括一些动画
     */
    public class TransparencyHandle {
        private void startAutoDownAlpha() {
            mHandler.sendEmptyMessageDelayed(MSG_HIDE, 1000);
        }

        /**
         * @param alpha
         * @author:purebluesong 参数说明：一个0到1的单精度浮点型
         */
        public void setKeyBoardAlpha(float alpha) {
            int alphaInt = (int) (alpha * 255);
            setKeyboardAlphaRaw(alphaInt, 1);
        }

        /**
         * @param alphaInt
         * @param alphaFloat 参数是0~255的整形值 和0~1的单精度浮点,不应该对外使用
         */
        @SuppressLint("NewApi")
        private void setKeyboardAlphaRaw(int alphaInt, float alphaFloat) {
            t9InputViewGroup.setBackgroundAlpha(alphaInt);
            functionViewGroup.setBackgroundAlpha(alphaInt);
            functionViewGroup.setButtonAlpha(alphaFloat);
            bottomBarViewGroup.setBackgroundAlpha(alphaInt);
            bottomBarViewGroup.setButtonAlpha(alphaFloat);
            qkCandidatesViewGroup.setBackgroundAlpha(alphaInt);
            specialSymbolChooseViewGroup.setBackgroundAlpha(alphaInt);
            specialSymbolChooseViewGroup.setButtonAlpha(alphaFloat);
            preFixViewGroup.setBackgroundAlpha(alphaInt);
            preFixViewGroup.setButtonAlpha(alphaFloat);
            qkInputViewGroups.setBackgroundAlpha(alphaInt);
            quickSymbolViewGroup.setBackgroundAlpha(alphaInt);
            quickSymbolViewGroup.setButtonAlpha(alphaFloat);
            largeCandidateButton.getBackground().setAlpha(alphaInt);
        }

        private void UpAlpha() {
            if (!mWindowShown) return;
            if (Kernel.getWordsNumber() >0 || !show )
                return;
            Animation anim = AnimationUtils.loadAnimation(SoftKeyboard.this, R.anim.hide);
            if (Global.currentKeyboard == Global.KEYBOARD_T9 ||
                    Global.currentKeyboard == Global.KEYBOARD_NUM ||
                    Global.currentKeyboard == Global.KEYBOARD_SYM) {
                t9InputViewGroup.startAnimation(anim);
            } else {
                qkInputViewGroups.startAnimation(anim);
            }
            if (bottomBarViewGroup.isShown()) bottomBarViewGroup.startAnimation(anim);
            if (functionViewGroup.isShown()) functionViewGroup.startAnimation(anim);
            if (specialSymbolChooseViewGroup.isShown()) specialSymbolChooseViewGroup.startAnimation(anim);
            if (quickSymbolViewGroup.isShown())
                quickSymbolViewGroup.startAnimation(anim);
            if (qkCandidatesViewGroup.isShown())
                qkCandidatesViewGroup.startAnimation(anim);
            if (largeCandidateButton.isShown())
                largeCandidateButton.startAnimation(anim);
            show = false;
        }

        /**
         * 功能：减小透明度
         * 调用时机：键盘上的任意touch事件
         */
        public void DownAlpha() {
            if (show || !mWindowShown) return;
            show = true;
            Animation anim = AnimationUtils.loadAnimation(SoftKeyboard.this, R.anim.show);
            if (Global.currentKeyboard == Global.KEYBOARD_T9 || Global.currentKeyboard == Global.KEYBOARD_NUM || Global.currentKeyboard == Global.KEYBOARD_SYM) {
                if (t9InputViewGroup.isShown()) {
                    t9InputViewGroup.startAnimation(anim);
                }
            } else {
                qkInputViewGroups.startAnimation(anim);
            }
            if (bottomBarViewGroup.isShown()) bottomBarViewGroup.startAnimation(anim);
            if (functionViewGroup.isShown()) functionViewGroup.startAnimation(anim);
            if (specialSymbolChooseViewGroup.isShown()) specialSymbolChooseViewGroup.startAnimation(anim);
            if (quickSymbolViewGroup.isShown()) quickSymbolViewGroup.startAnimation(anim);
            if (preFixViewGroup.isShown()) preFixViewGroup.startAnimation(anim);
            if (qkCandidatesViewGroup.isShown()) qkCandidatesViewGroup.startAnimation(anim);
            if (largeCandidateButton.isShown()) largeCandidateButton.startAnimation(anim);
        }

        public void handleAlpha(int eventAction) {
            Global.keyboardRestTimeCount = 0;
            if (eventAction == MotionEvent.ACTION_DOWN) {
                DownAlpha();
            }
        }
    }

    /**
     * keyboardLayout、设置键盘大小的view的创建与删除
     */
    public class ViewManagerC {
        /**
         */
        public void addInputView() {
            if (!keyboardLayout.isShown() ) {
                WindowManager wm = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
                screenInfoC.LoadKeyboardSizeInfoFromSharedPreference();

                keyboardParams.type = LayoutParams.TYPE_PHONE;
                keyboardParams.format = 1;
                keyboardParams.flags = DISABLE_LAYOUTPARAMS_FLAG;
                keyboardParams.gravity = Gravity.TOP | Gravity.LEFT;

                viewSizeUpdate.updateViewSizeAndPosition();
                wm.addView(keyboardLayout, keyboardParams);
            }
        }

        /***/
        public void removeInputView() {
            if (null != keyboardLayout && keyboardLayout.isShown()) {
                WindowManager wm = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
                wm.removeView(keyboardLayout);
            }
        }

        /**
         * 功能：添加调整键盘尺寸的视图
         * 调用时机：touch RESIZE功能键
         *
         * @param type
         */
        public void addSetKeyboardSizeView(SettingType type) {
            mSetKeyboardSizeViewOn = true;//todo: what is this?
            WindowManager wm = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);

            mSetKeyboardSizeParams.type = LayoutParams.TYPE_PHONE;
            mSetKeyboardSizeParams.format = 1;
            mSetKeyboardSizeParams.gravity = Gravity.TOP | Gravity.LEFT;
            mSetKeyboardSizeParams.x = 0;
            mSetKeyboardSizeParams.y = 0;
            mSetKeyboardSizeParams.width = mScreenWidth;
            mSetKeyboardSizeParams.height = mScreenHeight;

            mSetKeyboardSizeParams.flags = DISABLE_LAYOUTPARAMS_FLAG;
            updateSetKeyboardSizeViewPos();
            mSetKeyboardSizeView.SetSettingType(type);
            wm.addView(mSetKeyboardSizeView, mSetKeyboardSizeParams);
        }

        public void removeSetKeyboardSizeView() {
            if (mSetKeyboardSizeViewOn) {
                WindowManager wm = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
                wm.removeView(mSetKeyboardSizeView);
            }
            mSetKeyboardSizeViewOn = false;
        }

    }

    /**
     * 键盘的的出场动画
     */
    private void startAnimation() {
        if (Global.currentKeyboard == Global.KEYBOARD_T9 || Global.currentKeyboard == Global.KEYBOARD_NUM || Global.currentKeyboard == Global.KEYBOARD_SYM) {
            t9InputViewGroup.startShowAnimation();
            qkInputViewGroups.setVisibility(View.GONE);
        } else {
            qkInputViewGroups.startShowAnimation();
            t9InputViewGroup.setVisibility(View.GONE);
        }

        preFixViewGroup.setVisibility(View.GONE);
        quickSymbolViewGroup.setVisibility(View.VISIBLE);
        functionViewGroup.startshowAnimation();
        functionViewGroup.setVisibility(View.VISIBLE);
        bottomBarViewGroup.startShowAnimation();
        bottomBarViewGroup.setVisibility(View.VISIBLE);
        if (keyboardLayout.getBackground() != null) {
            keyboardLayout.getBackground().setAlpha((int) (Global.keyboardViewBackgroundAlpha * 255));
        }
        keyboardParams.flags = ABLE_LAYOUTPARAMS_FLAG;

        viewSizeUpdate.UpdateQuickSymbolSize();
        if (keyboardLayout.isShown()) {
            WindowManager wm = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
            wm.updateViewLayout(keyboardLayout, keyboardParams);
        }
        show = true;
    }

    /**
     * 键盘的退出动画
     * */
    private void startOutAnimation() {
        if (!ViewFuncs.isQK(Global.currentKeyboard)) {
            qkInputViewGroups.setVisibility(View.GONE);
            t9InputViewGroup.startHideAnimation();
        } else {
            t9InputViewGroup.setVisibility(View.GONE);
            qkInputViewGroups.startHideAnimation();
        }
        if (keyboardLayout.getBackground() != null)
            keyboardLayout.getBackground().setAlpha(0);

        if (largeCandidateButton.isShown()) largeCandidateButton.setVisibility(View.GONE);
         if (quickSymbolViewGroup.isShown()) {
            quickSymbolViewGroup.hide();
            quickSymbolViewGroup.startAnimation(R.anim.key_1_out);
        }
        if (specialSymbolChooseViewGroup.isShown()) {
            specialSymbolChooseViewGroup.startAnimation(R.anim.key_1_out);
            specialSymbolChooseViewGroup.setVisibility(View.GONE);
        }
        preFixViewGroup.hide();
        functionViewGroup.starthideAnimation();
        bottomBarViewGroup.startHideAnimation();
        qkCandidatesViewGroup.setVisibility(View.GONE);

        lightViewManager.invisibleLightView();
        keyboardParams.flags = DISABLE_LAYOUTPARAMS_FLAG;
        if (keyboardLayout.isShown()) {
            WindowManager wm = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
            wm.updateViewLayout(keyboardLayout, keyboardParams);
        }
        show = false;
    }

    @Override
    public View onCreateInputView() {
        Log.i("Test","onCreateInputView");
        return super.onCreateInputView();
    }

    /**
     * 启动输入法，在切换输入法时起作用，貌似安装后有一段时间不出来就是耗在这里
     * */
    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        Log.i("Test","onStartInputView");
        initInputParam.initKernal(this.getApplicationContext());

        Global.inLarge = false;
        //设置enter_text
        BottomBarViewGroup.setEnterText(bottomBarViewGroup.enterButton, info, Global.currentKeyboard);
        /*
         * 获取中文键盘类型
		 */
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        zhKeyboard = sharedPreferences.getString("KEYBOARD_SELECTOR", "2").equals("1") ? Global.KEYBOARD_T9 : Global.KEYBOARD_QP;
        if (mWindowShown) {
            int keyboard = functionsC.getKeyboardType(info);
            if (Global.currentKeyboard != keyboard && info.inputType != 0) {
                keyBoardSwitcher.switchKeyboard(keyboard, true);
            }
            transparencyHandle.DownAlpha();
        }
        Global.keyboardRestTimeCount = 0;
        mHandler.removeMessages(MSG_DOUBLE_CLICK_REFRESH);
        mHandler.removeMessages(MSG_KERNEL_CLEAN);
        mHandler.sendEmptyMessageDelayed(MSG_DOUBLE_CLICK_REFRESH, 0);
        mHandler.sendEmptyMessageDelayed(MSG_KERNEL_CLEAN, maxFreeKernelTime * Global.metaRefreshTime);
        super.onStartInputView(info, restarting);
    }

    /**
     * 弹出输入框
     * */
    @Override
    public void onWindowShown() {
        Log.i("Test","onWindowShown");
        MobclickAgent.onResume(this);
        mWindowShown = true;
        mHandler.removeMessages(MSG_HIDE);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        Global.mCurrentAlpha = sp.getFloat("CURRENT_ALPHA", 1f);
        Global.shadowRadius = Integer.parseInt(sp.getString("SHADOW_TEXT_RADIUS", "5"));
        Global.slideDeleteSwitch = sp.getBoolean("SLIDE_DELETE_CHECK", true);
        skinUpdateC.updateShadowLayer();
        viewManagerC.addInputView();
        lightViewManager.removeView();//真是种lowB方式，为了保证光线片在键盘之上也是醉了……，问题是wm就没有别的方式来保证啊
        lightViewManager.addToWindow();
        mInputViewGG.setVisibility(View.VISIBLE);

        try {
            quickSymbolViewGroup.updateSymbolsFromFile();
        } catch (IOException e) {
            CommonFuncs.showToast(this, "Sorry,There is an error when program load symbols from file");
        }

        EditorInfo info = this.getCurrentInputEditorInfo();
        keyBoardSwitcher.switchKeyboard(functionsC.getKeyboardType(info), false);
        //显示键盘出场动画
        startAnimation();
        //设置空格键文字
        bottomBarViewGroup.spaceButton.setText(InputMode.halfToFull(sp.getString("ZH_SPACE_TEXT", "空格")));
        //更新当前键盘皮肤
        skinUpdateC.updateSkin();
        //音效设置加载
        keyBoardTouchEffect.loadSetting(sp);
        super.onWindowShown();
    }

    /**
     * 回收输入框
     * */
    @Override
    public void onWindowHidden() {
        Log.i("Test","onWindowHidden");
        MobclickAgent.onPause(this);
        mWindowShown = false;
        Kernel.cleanKernel();
        refreshDisplay();
        t9InputViewGroup.updateFirstKeyText();
        startOutAnimation();
        if (mSetKeyboardSizeViewOn) {
            mOnSizeChangeListener.onFinishSetting();
        }
        super.onWindowHidden();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.i("Test","onKeyUp");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mSetKeyboardSizeViewOn) {
                mOnSizeChangeListener.onFinishSetting();
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("Test","onKeyDown");
        if (keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_Z) {
            char key = (char) ('a' + keyCode - KeyEvent.KEYCODE_A);
            Kernel.inputPinyin(key + "");
            refreshDisplay();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_DEL && Kernel.getWordsNumber() > 0) {
            Kernel.deleteAction();
            refreshDisplay();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
