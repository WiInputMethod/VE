package com.hit.wi.ve;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
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
import com.hit.wi.util.DisplayUtil;
import com.hit.wi.util.InputMode;
import com.hit.wi.jni.*;
import com.hit.wi.ve.Interfaces.SoftKeyboardInterface;
import com.hit.wi.ve.effect.KeyBoardTouchEffect;
import com.hit.wi.ve.functions.GenerateMessage;
import com.hit.wi.ve.functions.PinyinEditProcess;
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
import java.util.List;

import javax.microedition.khronos.opengles.GL;

import static java.lang.Thread.sleep;


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
    private boolean mSetKeyboardSizeViewOn = false;

    /**
     * 是全拼还是Emoji
     */
    private String mQKOrEmoji = Global.QUANPIN;

    /**
     * 中文键盘类型
     */
    private int zhKeyboard;
    private boolean keyboard_animation_switch;

    /**
     * 屏幕宽度
     */
    private int mScreenWidth;

    /**
     * 屏幕高度
     */
    private int mScreenHeight;

    /**
     * 状态栏高度
     */
    private int mStatusBarHeight;

    public String[] mFuncKeyboardText;

    /**
     * 浮动窗口状态标识符
     */
    private static final int DISABLE_LAYOUTPARAMS_FLAG = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
            | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
    //默认是FLAG_NOT_FOCUSABLE，否则会抢夺输入框的焦点导致键盘收回

    private static final int ABLE_LAYOUTPARAMS_FLAG = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
    //透明度有关
    //    public static final int SET_ALPHA_VIEW_DESTROY = -803;

    private final int MSG_HIDE = 0;

    public final int MSG_REPEAT = 1;
    private final int MSG_SEND_TO_KERNEL = 2;
    private final int QK_MSG_SEND_TO_KERNEL = 3;
    private final int MSG_CHOOSE_WORD = 4;
    private final int MSG_HEART = 5;
    private final int MSG_LAZY_LOAD_CANDIDATE = 6;
    private final int MSG_DOUBLE_CLICK_REFRESH = 7;
    private final int MSG_KERNEL_CLEAN = 8;
    private final int MSG_CLEAR_ANIMATION = 9;
    private final int MSG_REMOVE_INPUT = 10;

    private final int ALPHA_DOWN_TIME = 7;
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


    public int keyboardWidth = 0;
    public int keyboardHeight = 0;
    public int standardVerticalGapDistance = 10;
    public int standardHorizontalGapDistance = 0;
    private int maxFreeKernelTime = 60;

    private InitInputParam initInputParam;
    public Typeface mTypeface;

    public LinearLayout keyboardLayout;
    public WindowManager.LayoutParams keyboardParams = new WindowManager.LayoutParams();
    public LinearLayout secondLayerLayout;
    private LinearLayout.LayoutParams secondParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    public LinearLayout mInputViewGG;
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

    public QKInputViewGroup qkInputViewGroup;
    public SpecialSymbolChooseViewGroup specialSymbolChooseViewGroup;
    public FunctionViewGroup functionViewGroup;
    public QuickSymbolViewGroup quickSymbolViewGroup;
    public PreFixViewGroup prefixViewGroup;
    public BottomBarViewGroup bottomBarViewGroup;
    public CandidatesViewGroup candidatesViewGroup;
    public T9InputViewGroup t9InputViewGroup;
    public LightViewManager lightViewManager;
    public PreEditPopup preEditPopup;

    public SymbolsManager symbolsManager;
    public KeyBoardTouchEffect keyboardTouchEffect;
    public SkinInfoManager skinInfoManager;
    public PinyinEditProcess pinyinProc;
    private Resources res;

    /**
     * 处理消息事件
     */
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_HIDE:
                    if (candidatesViewGroup.isShown()) Global.keyboardRestTimeCount=0;
                    if (Global.keyboardRestTimeCount > ALPHA_DOWN_TIME) {
                        if(!transparencyHandle.isUpAlpha)transparencyHandle.UpAlpha();
                        Global.keyboardRestTimeCount = 0;
                    } else {
                        Global.keyboardRestTimeCount++;
                    }
                    mHandler.removeMessages(MSG_HIDE);
                    mHandler.sendEmptyMessageDelayed(MSG_HIDE, Global.metaRefreshTime);
                    break;
                case MSG_REPEAT:
                    deleteLast();
                    sendEmptyMessageDelayed(MSG_REPEAT, REPEAT_INTERVAL);
                    break;
                case MSG_SEND_TO_KERNEL:
                    editPinyin((String)msg.obj,false);
                    t9InputViewGroup.updateFirstKeyText();
                    break;
                case QK_MSG_SEND_TO_KERNEL:
                    editPinyin((String) msg.obj, false);
                    qkInputViewGroup.refreshQKKeyboardPredict();
                    break;
                case MSG_CHOOSE_WORD:
                    chooseWord(msg.arg1);
                    break;
                case MSG_LAZY_LOAD_CANDIDATE:
                    candidatesViewGroup.setCandidates((List<String>) msg.obj);
                    break;
                case MSG_DOUBLE_CLICK_REFRESH:
                    mHandler.removeMessages(MSG_DOUBLE_CLICK_REFRESH);
                    mHandler.sendEmptyMessageDelayed(MSG_DOUBLE_CLICK_REFRESH, 3 * Global.metaRefreshTime);
                    break;
                case MSG_KERNEL_CLEAN:
                    mHandler.removeMessages(MSG_KERNEL_CLEAN);
                    break;
                case MSG_CLEAR_ANIMATION:
                    clearAnimation();
                    break;
                case MSG_REMOVE_INPUT:
                    viewManagerC.removeInputView();
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
        NKInitDictFile.NKInitWiDict(this);//todo:第一次加载键盘要读文件到内存中，这一步上耗时很长，尝试使用Handler扔到子线程中执行
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
        super.onCreate();
    }

    private void iniComponent() {
        res = getResources();
        keyboardTouchEffect = new KeyBoardTouchEffect(this);
        specialSymbolChooseViewGroup = new SpecialSymbolChooseViewGroup();
        functionViewGroup = new FunctionViewGroup();
        quickSymbolViewGroup = new QuickSymbolViewGroup();
        prefixViewGroup = new PreFixViewGroup();
        bottomBarViewGroup = new BottomBarViewGroup();
        candidatesViewGroup = new CandidatesViewGroup();
        qkInputViewGroup = new QKInputViewGroup();
        t9InputViewGroup = new T9InputViewGroup();
        preEditPopup = new PreEditPopup();
        lightViewManager = new LightViewManager();
        pinyinProc = new PinyinEditProcess(this);

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
        pinyinProc.mSelStart = Math.min(newSelStart,newSelEnd);
        pinyinProc.mSelEnd = Math.max(newSelStart,newSelEnd);
        pinyinProc.mCandidateStart = Math.min(candidatesStart,candidatesEnd);
        pinyinProc.mCandidateEnd = Math.max(candidatesStart,candidatesEnd);
        transparencyHandle.handleAlpha(MotionEvent.ACTION_DOWN);
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
     * selstart意思是 selection Start 被选中区域的开头部分，selEnd同理
     * 调用时机：{@link #mHandler}处理{@link #MSG_SEND_TO_KERNEL}时调用
     * @param s 向内核传入的字符,delete 是否删除操作
     */
    public void editPinyin(String s, boolean delete) {
        if(pinyinProc.borderEditProcess(s, delete))return;// promise candidateStart<selStart<candidateEnd
        mQKOrEmoji = Global.QUANPIN;

        final InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        final String pinyin = Kernel.getWordsShowPinyin();
        if (pinyin.length() != pinyinProc.mCandidateEnd - pinyinProc.mCandidateStart) return;

        //切割字符串,顺便还做了删除处理，卧槽师兄想的真tm周全，小小的代码里全是坑
        final int isDel = delete && pinyinProc.mSelStart == pinyinProc.mSelEnd ? 1 : 0;
        int cursorBefore = pinyinProc.mSelStart - pinyinProc.mCandidateStart - isDel;
        String sBefore = cursorBefore > pinyin.length() ? pinyin.substring(0,cursorBefore):pinyin;
        sBefore = sBefore.replace("'", "")+s;
        String sAfter = pinyinProc.mSelEnd <= pinyinProc.mCandidateStart ? pinyin :
                (pinyinProc.mSelEnd >= pinyinProc.mCandidateEnd ? "":
                        pinyin.substring(pinyinProc.mSelEnd - pinyinProc.mCandidateStart).replace("'", ""));
        pinyinProc.innerEditProcess(ic,sBefore,s,sAfter,delete);
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

    private boolean lastHideState = false;

    /**
     * 每次有候选词跟新的时候统一刷新界面，因为影响到的因素比较多，统一使用状态机解决
     *
     */
    public void refreshDisplay(boolean special) {
        boolean isNK = Global.currentKeyboard == Global.KEYBOARD_T9 || Global.currentKeyboard == Global.KEYBOARD_SYM;
        boolean hideCandidate = Kernel.getWordsNumber()==0 && !special;
        Kernel.setKernelType(isNK?Kernel.NINE_KEY:Kernel.QWERT);

        if (mInputViewGG.isShown()) mInputViewGG.setVisibility(View.VISIBLE);
        functionViewGroup.refreshState(hideCandidate);
        functionsC.refreshStateForSecondLayout();
        functionsC.refreshStateForLargeCandidate(hideCandidate);
        if (lastHideState!=hideCandidate && !hideCandidate) {
            viewSizeUpdate.UpdatePreEditSize();
            viewSizeUpdate.UpdateCandidateSize();
            viewSizeUpdate.UpdateLargeCandidateSize();
        }

        candidatesViewGroup.refreshState(hideCandidate, isNK ? Global.EMOJI : Global.QUANPIN);
        specialSymbolChooseViewGroup.refreshState(hideCandidate);
        prefixViewGroup.refreshState();
        t9InputViewGroup.refreshState();
        qkInputViewGroup.refreshState();
        quickSymbolViewGroup.refreshState();
        viewSizeUpdate.UpdateQuickSymbolSize();
        bottomBarViewGroup.refreshState();
        pinyinProc.computeCursorPosition(getCurrentInputConnection());
        preEditPopup.refreshState();
    }

    /**
     * 但是貌似因为太好用了而导致滥用，使得可能一个过程中多次重复刷新界面，所以还是要 todo：增加状态表示使得如果状态未变则不刷新
     * 同时：避免滥用，能不用的情况就不用
     * */
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
                t9InputViewGroup.updateFirstKeyText();
                editPinyin("", true);
            } else if (Global.currentKeyboard == Global.KEYBOARD_QK) {
                if (mQKOrEmoji.equals(Global.QUANPIN)) {
                    String pinyin = Kernel.getWordsShowPinyin();
                    Global.addToRedo(pinyin.substring(pinyin.length()-1));
                    editPinyin("", true);
                } else if (mQKOrEmoji.equals(Global.EMOJI)) {
                    Kernel.cleanKernel();
                    refreshDisplay(true);
                }
            } else if (Global.currentKeyboard == Global.KEYBOARD_EN) {
                Kernel.cleanKernel();
                refreshDisplay(true);
            }
        }
    }

    private static final int TEXT_MAX_LENGTH = 100;
    public void deleteAll() {
        if (Kernel.getWordsNumber()==0 || Global.currentKeyboard == Global.KEYBOARD_SYM) {
            InputConnection ic = SoftKeyboard.this.getCurrentInputConnection();
            if (ic != null) {
                Global.redoTextForDeleteAll = ic.getTextBeforeCursor(TEXT_MAX_LENGTH,0);
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
        } else if (Global.currentKeyboard == Global.KEYBOARD_QK) {
            refreshDisplay(!mQKOrEmoji.equals(Global.QUANPIN));
        } else if (Global.currentKeyboard == Global.KEYBOARD_EN) {
            refreshDisplay();
        }
        qkInputViewGroup.refreshQKKeyboardPredict();//刷新点滑预测
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
        qkInputViewGroup.refreshQKKeyboardPredict();//刷新点滑预测
    }

    /**
     * 上屏,从这里传输的文字会直接填写到文本框里
     * @param text 上传文字
     */
    public void commitText(CharSequence text) {
        InputConnection ic = getCurrentInputConnection();
        if (text != null && ic != null) {
            Global.addToRedo(text);
            ic.commitText(text, 1);
        }
        qkInputViewGroup.refreshQKKeyboardPredict();//刷新点滑预测
    }

    /**
     * 如其名，向九键内核传递拼音串
     * @param msg pinyin
    * */
    public void sendMsgToKernel(String msg) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SEND_TO_KERNEL, msg));
    }

    /**
     * 向全键内核传递拼音串
     * @param msg pinyin
     * */
    public void sendMsgToQKKernel(String msg) {
        mHandler.sendMessage(mHandler.obtainMessage(QK_MSG_SEND_TO_KERNEL, msg));
    }

    @SuppressWarnings("deprecation")
    public void updateWindowManager() {
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
        wm.updateViewLayout(keyboardLayout, keyboardParams);
    }

    public void switchKeyboardTo(int keyboard, boolean showAnim) {
        keyBoardSwitcher.switchKeyboard(keyboard, showAnim);
    }

    public void clearAnimation(){
        quickSymbolViewGroup.clearAnimation();
        t9InputViewGroup.clearAnimation();
        qkInputViewGroup.clearAnimation();
        functionViewGroup.clearAnimation();
        bottomBarViewGroup.clearAnimation();
        prefixViewGroup.clearAnimation();
        largeCandidateButton.clearAnimation();
        candidatesViewGroup.clearAnimation();
        lightViewManager.invisibleLightView();
        specialSymbolChooseViewGroup.clearAnimation();
    }

    public OnChangeListener mOnSizeChangeListener = new OnChangeListener() {

        public void onSizeChange(Rect keyboardRect) {
            keyboardWidth = keyboardRect.width();
            keyboardHeight = keyboardRect.height();

            keyboardParams.x = keyboardRect.left;
            keyboardParams.y = keyboardRect.top;
            keyboardParams.width = keyboardWidth;
            keyboardParams.height = keyboardHeight;

            standardHorizontalGapDistance = keyboardWidth * 2 / 100;
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
            edit.apply();
            screenInfoC.LoadKeyboardSizeInfoFromSharedPreference();

            viewSizeUpdate.updateViewSizeAndPosition();

            updateSetKeyboardSizeViewPos();
            mSetKeyboardSizeView.invalidate();
        }
    };

    /**
     * 键盘切换类，实际运用基本只调用其中的SwitchKeyBoard
     */
    private class KeyBoardSwitcherC {

        private void hideKeyboard(int keyboard, boolean showAnim) {
            if (Global.isQK(Global.currentKeyboard)) {
                qkInputViewGroup.hide(showAnim);
            } else {
                if (keyboard == Global.KEYBOARD_NUM || keyboard == Global.KEYBOARD_SYM  || keyboard == Global.KEYBOARD_T9) {
                    t9InputViewGroup.T9ToNum(showAnim);
                } else {
                    t9InputViewGroup.hideT9(showAnim);
                }
            }
        }

        private void showKeyboard(int keyboard, boolean showAnim) {
            if(Global.isQK(keyboard)){
                qkInputViewGroup.reloadPredictText(keyboard);
                qkInputViewGroup.show(showAnim);
            } else {
                t9InputViewGroup.show(showAnim);
            }
        }

        public void switchKeyboard(int keyboard) {
            switchKeyboard(keyboard, false);
        }

        /**
         * 功能：切换键盘
         * 调用时机：首次弹出键盘或者按下键盘上相应的切换键时调用
         *
         * @param keyboard 要切换到的键盘
         * @param showAnim 是否播放动画
         */
        public void switchKeyboard(int keyboard, boolean showAnim) {
            Kernel.cleanKernel();

            if(Global.currentKeyboard != keyboard){
                hideKeyboard(keyboard, showAnim);
                showKeyboard(keyboard, showAnim);
                quickSymbolViewGroup.updateCurrentSymbolsAndSetTheContent(keyboard);
                Global.currentKeyboard = keyboard;
            }

            viewSizeUpdate.updateViewSizeAndPosition();
            refreshDisplay();
        }
    }

    /**
     * 一些工具函数集中存放的地方
     */
    public class  FunctionsC {

        /**
         * 功能：判断当前输入框是否要输入网址，为了显示相应的字符
         * 调用时机：切换到符号键盘时调用
         *
         * @param ei 输入框信息
         * @return 是否要输入网址
         */
        private boolean isToShowUrl(EditorInfo ei) {
            return ei!=null && ((ei.inputType & EditorInfo.TYPE_MASK_CLASS) == EditorInfo.TYPE_CLASS_TEXT
                  && (EditorInfo.TYPE_MASK_VARIATION & ei.inputType) == EditorInfo.TYPE_TEXT_VARIATION_URI
                  || (ei.imeOptions & (EditorInfo.IME_MASK_ACTION | EditorInfo.IME_FLAG_NO_ENTER_ACTION)) == EditorInfo.IME_ACTION_GO);
        }

        /**
         * 功能：判断输入框是否要输入邮箱
         * 调用时机：切换为符号键盘
         * @param ei 编辑框信息
         * @return 是否要输入邮箱的boolean
        */
        private boolean isToShowEmail(EditorInfo ei) {
            return ei!=null && (ei.inputType & EditorInfo.TYPE_MASK_CLASS) == EditorInfo.TYPE_CLASS_TEXT &&
                        (EditorInfo.TYPE_MASK_VARIATION & ei.inputType) == EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
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

        public void refreshStateForSecondLayout() {
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
            largeCandidateButton.getBackground().setAlpha(Global.getCurrentAlpha());
        }

        public void updateSkin(TextView v, int textColor, int backgroundColor) {
            v.setTextColor(textColor);
            v.setBackgroundColor(backgroundColor);
            v.getBackground().setAlpha(Global.getCurrentAlpha());
        }

        /**
         * 判断输入域类型，返回确定键盘类型
         */
        private int getKeyboardType(EditorInfo pEditorInfo) {
            int keyboardType = Global.KEYBOARD_QK;
            // 判断输入框类型,选择对应的键盘
            if(pEditorInfo != null){
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
            }
            return keyboardType;
        }
    }

    /**
     * 存放listener
     */
    private class Listeners {
        //todo: largeCandidate button should belonged to candidateViewGroups, needs to refactor
        OnTouchListener largeCandidateOnTouchListener = new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                transparencyHandle.handleAlpha(motionEvent.getAction());
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    candidatesViewGroup.displayCandidates();
                    candidatesViewGroup.largeTheCandidate();
                    Global.inLarge = true;
                    bottomBarViewGroup.intoReturnState();
                }
                keyboardTouchEffect.onTouchEffectWithAnim(view, motionEvent.getAction(),
                        skinInfoManager.skinData.backcolor_touchdown,
                        skinInfoManager.skinData.backcolor_quickSymbol
                );
                return false;
            }
        };
    }

    /**
     * 各种view的创建函数，不初始化位置,只初始一些设置以及分配相应内存
     */
    private class KeyBoardCreate {

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
            candidatesViewGroup.setSoftKeyboard(SoftKeyboard.this);
            candidatesViewGroup.create(SoftKeyboard.this);
            candidatesViewGroup.addThisToView(keyboardLayout);
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
            prefixViewGroup.setSoftKeyboard(SoftKeyboard.this);
            prefixViewGroup.create(SoftKeyboard.this);
            prefixViewGroup.addThisToView(secondLayerLayout);
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
            largeCandidateButton.setTextColor(skinInfoManager.skinData.textcolor_quickSymbol);
            largeCandidateButton.setBackgroundColor(skinInfoManager.skinData.backcolor_quickSymbol);
            largeCandidateButton.getBackground().setAlpha(Global.getCurrentAlpha());
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
            qkInputViewGroup.setSoftKeyboard(SoftKeyboard.this);
            qkInputViewGroup.create(SoftKeyboard.this);
            qkInputViewGroup.setTypeface(mTypeface);
            qkInputViewGroup.addThisToView(mInputViewGG);
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
                mInputViewGG.setGravity(Gravity.CENTER_HORIZONTAL);
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
        private final float TEXTSIZE_RATE_CANDIDATE = (float) 0.8;
        private final float TEXTSIZE_RATE_BOTTOM = (float) 0.8;
        private final float TEXTSIZE_RATE_FUNCTION = (float) 0.8;
        private final float TEXTSIZE_RATE_T9 = (float) 0.8;
        private final float TEXTSIZE_RATE_QUICKSYMBOL = (float) 0.8;
        private final int PREFIX_WIDTH_RATE = 44;
        private final double PREEDIT_HEIGHT_RATE = 0.5;
        private final int HOR_GAP_NUM = 2;
        private final int BUTTON_SHOW_NUM = 6;

        private int[] layerHeightRate;

        private void UpdatePrefixSize() {
            prefixViewGroup.setPosition(0, 0);
            if (Global.currentKeyboard == Global.KEYBOARD_NUM || Global.currentKeyboard == Global.KEYBOARD_SYM || Global.currentKeyboard == Global.KEYBOARD_T9) {
                prefixViewGroup.setSize(keyboardWidth * PREFIX_WIDTH_RATE / 100, keyboardHeight * layerHeightRate[1] / 100);
            } else {
                prefixViewGroup.setSize(keyboardWidth - standardHorizontalGapDistance, keyboardHeight * layerHeightRate[1] / 100);
            }
            prefixViewGroup.setButtonPadding(standardHorizontalGapDistance);
            prefixViewGroup.updateViewLayout();
        }

        private void UpdatePreEditSize() {
            preEditPopup.upadteSize(keyboardParams.width-HOR_GAP_NUM* standardHorizontalGapDistance,
                    (int) (PREEDIT_HEIGHT_RATE*layerHeightRate[0]*keyboardParams.height/100),
                    standardHorizontalGapDistance);
        }

        private void UpdateQKSize() {
            qkInputViewGroup.setPosition(0, 0);
            qkInputViewGroup.setSize(keyboardWidth, keyboardHeight * layerHeightRate[2] / 100,standardHorizontalGapDistance);
            qkInputViewGroup.updateViewLayout();
        }

        private void UpdateQuickSymbolSize() {
            int height = keyboardHeight * layerHeightRate[1] / 100;
            int width = keyboardWidth - HOR_GAP_NUM * standardHorizontalGapDistance;
            int buttonWidth = width / BUTTON_SHOW_NUM;
            quickSymbolViewGroup.setPosition(0, 0);
            quickSymbolViewGroup.setButtonPadding(standardHorizontalGapDistance);
            quickSymbolViewGroup.setButtonWidth(buttonWidth);
            float textSize = DisplayUtil.px2sp(SoftKeyboard.this,(float) (Math.min(buttonWidth, height) * TEXTSIZE_RATE_QUICKSYMBOL));
            quickSymbolViewGroup.setTextSize(textSize);
            if (t9InputViewGroup.deleteButton.isShown() && largeCandidateButton.isShown()) {
                quickSymbolViewGroup.setSize(keyboardWidth * PREFIX_WIDTH_RATE / 100, height);
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
            specialSymbolChooseViewGroup.setTextSize(DisplayUtil.px2sp(SoftKeyboard.this,(float) (TEXTSIZE_RATE_QUICKSYMBOL * height)));
        }

        private void UpdateT9Size() {
            t9InputViewGroup.setSize(keyboardWidth, mGGParams.height, standardHorizontalGapDistance);
            t9InputViewGroup.deleteButton.getPaint().setTextSize(DisplayUtil.px2sp(SoftKeyboard.this,
                     Math.min(t9InputViewGroup.deleteButton.itsLayoutParams.width, layerHeightRate[1] * keyboardHeight / 100) * TEXTSIZE_RATE_T9
            ));
            keyboardLayout.updateViewLayout(mInputViewGG, mGGParams);
        }

        private void UpdateLightSize() {
            lightViewManager.setSize(keyboardWidth, keyboardHeight * layerHeightRate[2] / 100,
                    keyboardParams.x,
                    keyboardParams.y + keyboardHeight * (layerHeightRate[0] + layerHeightRate[1]) / 100 +2*standardVerticalGapDistance //计算y位置
            );
        }

        private void UpdateFunctionsSize() {
            int height = keyboardHeight * layerHeightRate[0] / 100;
            int buttonwidth = keyboardWidth / 5 - standardHorizontalGapDistance * 6 / 5;
            functionViewGroup.updatesize(keyboardWidth, height);
            functionViewGroup.setButtonPadding(standardHorizontalGapDistance);
            functionViewGroup.setButtonWidth(buttonwidth);
            functionViewGroup.setTextSize(DisplayUtil.px2sp(SoftKeyboard.this,Math.min(buttonwidth, height) * TEXTSIZE_RATE_FUNCTION));
        }

        public void UpdateCandidateSize() {
            candidatesViewGroup.setPosition(standardHorizontalGapDistance, 0);
            candidatesViewGroup.setSize(keyboardWidth - HOR_GAP_NUM * standardHorizontalGapDistance, keyboardHeight * layerHeightRate[0] / 100);
            candidatesViewGroup.updateViewLayout();
        }

        private void UpdateBottomBarSize() {
            bottomBarViewGroup.setPosition(0, standardVerticalGapDistance);
            bottomBarViewGroup.setSize(keyboardWidth - standardHorizontalGapDistance, ViewGroup.LayoutParams.MATCH_PARENT);
            bottomBarViewGroup.setButtonPadding(standardHorizontalGapDistance);
            if (Global.currentKeyboard == Global.KEYBOARD_NUM) {
                bottomBarViewGroup.setButtonWidthByRate(res.getIntArray(R.array.BOTTOMBAR_NUM_KEY_WIDTH));
            } else {
                bottomBarViewGroup.setButtonWidthByRate(res.getIntArray(R.array.BOTTOMBAR_KEY_WIDTH));
            }
            bottomBarViewGroup.setTextSize(DisplayUtil.px2sp(SoftKeyboard.this,((keyboardHeight*layerHeightRate[3]) / 100) * TEXTSIZE_RATE_BOTTOM));
            bottomBarViewGroup.updateViewLayout();
        }

        private void UpdateLargeCandidateSize() {
            largeCandidateButton.itsLayoutParams.height = LayoutParams.MATCH_PARENT;
            largeCandidateButton.itsLayoutParams.width = keyboardWidth - 3 * standardHorizontalGapDistance - res.getInteger(R.integer.PREEDIT_WIDTH) * keyboardWidth / 100;
            ((LinearLayout.LayoutParams) largeCandidateButton.itsLayoutParams).leftMargin = standardHorizontalGapDistance;

            largeCandidateButton.getPaint().setTextSize(DisplayUtil.px2sp(SoftKeyboard.this,
                    Math.min(secondParams.height, (100 - res.getInteger(R.integer.PREEDIT_WIDTH)) * keyboardWidth / 100) * TEXTSIZE_RATE_CANDIDATE
            ));
        }

        void updateViewSizeAndPosition() {
            layerHeightRate = res.getIntArray(R.array.LAYER_HEIGHT);

            mGGParams.topMargin = standardVerticalGapDistance;
            mGGParams.height = keyboardHeight * layerHeightRate[2] / 100;
            mGGParams.width = keyboardWidth;

            secondParams.height = keyboardHeight * layerHeightRate[1] / 100;
            secondParams.topMargin = standardVerticalGapDistance;
            secondParams.leftMargin = standardHorizontalGapDistance;

            UpdateT9Size();
            UpdateCandidateSize();
            UpdatePreEditSize();
            UpdateQKSize();
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
            if (isDiy) {
                skinInfoManager.loadConfigurationFromDIY(SoftKeyboard.this);
            } else if (themeType != Global.currentSkinType) {
                skinInfoManager.loadConfigurationFromXML(themeType, res);
                Global.currentSkinType=themeType;
            }

            candidatesViewGroup.updateSkin();
            t9InputViewGroup.updateSkin();
            preEditPopup.updateSkin();
            prefixViewGroup.updateSkin();
            specialSymbolChooseViewGroup.updateSkin();
            quickSymbolViewGroup.updateSkin();
            qkInputViewGroup.updateSkin();
            functionViewGroup.updateSkin();
            bottomBarViewGroup.updateSkin();
            functionsC.updateSkin(largeCandidateButton, skinInfoManager.skinData.textcolor_quickSymbol, skinInfoManager.skinData.backcolor_quickSymbol);
            if (keyboardLayout.getBackground() != null)
                keyboardLayout.getBackground().setAlpha((int) (Global.keyboardViewBackgroundAlpha * 255));
            else {
                keyboardLayout.setBackgroundResource(R.drawable.blank);

                keyboardLayout.setBackgroundColor(skinInfoManager.skinData.backcolor_touchdown);
                keyboardLayout.getBackground().setAlpha((int) (Global.keyboardViewBackgroundAlpha * 255));
            }
        }

        void updateShadowLayer() {
            candidatesViewGroup.setShadowLayer(Global.shadowRadius, skinInfoManager.skinData.shadow);
            prefixViewGroup.setShadowLayer(Global.shadowRadius, skinInfoManager.skinData.shadow);
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

        private final float KEYBOARD_Y_RATE = (float) 0.33;
        private final float KEYBOARD_WIDTH_RATE = (float) 0.66;
        private final float KEYBOARD_HEIGHT_RATE = (float) 0.5;

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

            DEFAULT_KEYBOARD_Y = (int) (mScreenHeight * KEYBOARD_Y_RATE);
            DEFAULT_KEYBOARD_WIDTH = (int) (mScreenWidth * KEYBOARD_WIDTH_RATE);
            DEFAULT_KEYBOARD_HEIGHT = (int) (mScreenHeight * KEYBOARD_HEIGHT_RATE);

            DEFAULT_FULL_WIDTH = mScreenWidth;
            DEFAULT_FULL_WIDTH_X = 0;
            DEFAULT_KEYBOARD_X = DEFAULT_KEYBOARD_WIDTH;
        }

        int getStatusBarHeight() {
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

            keyboardParams.x = sp.getInt(FULL_WIDTH_X_S + orientation, DEFAULT_FULL_WIDTH_X);
            keyboardParams.y = sp.getInt(KEYBOARD_Y_S + orientation, DEFAULT_KEYBOARD_Y);
            keyboardWidth = sp.getInt(FULL_WIDTH_S + orientation, DEFAULT_FULL_WIDTH);
            keyboardHeight = sp.getInt(KEYBOARD_HEIGHT_S + orientation, DEFAULT_KEYBOARD_HEIGHT);

            keyboardParams.width = keyboardWidth;
            keyboardParams.height = keyboardHeight;

            standardVerticalGapDistance = keyboardHeight * 2 / 100;
            standardHorizontalGapDistance = keyboardWidth * 2 / 100;
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

            editor.putInt(FULL_WIDTH_X_S + orientation, keyboardParams.x);
            editor.putInt(FULL_WIDTH_S + orientation, keyboardWidth);
            editor.apply();
        }
    }

    /**
     * 键盘透明度处理，包括一些动画
     */
    public class TransparencyHandle {
        boolean isUpAlpha = false;
        private final float autoDownAlpha = (float) 0.1;
        private final float autoDownAlphaTop = (float) 1.0;

        private void startAutoDownAlpha() {
            mHandler.sendEmptyMessageDelayed(MSG_HIDE, 1000);
        }

        /**
         * author:purebluesong
         * @param alpha 一个0到1的单精度浮点型表示透明度
         */
        public void setKeyBoardAlpha(int alpha) {
            t9InputViewGroup.setBackgroundAlpha(alpha);
            functionViewGroup.setBackgroundAlpha(alpha);
            bottomBarViewGroup.setBackgroundAlpha(alpha);
            candidatesViewGroup.setBackgroundAlpha(alpha);
            specialSymbolChooseViewGroup.setBackgroundAlpha(alpha);
            prefixViewGroup.setBackgroundAlpha(alpha);
            qkInputViewGroup.setBackgroundAlpha(alpha);
            quickSymbolViewGroup.setBackgroundAlpha(alpha);
            largeCandidateButton.getBackground().setAlpha(alpha);
        }

        private void UpAlpha() {
            if (!mWindowShown)return;
            Animation anim = AnimationUtils.loadAnimation(SoftKeyboard.this, R.anim.hide);
            if (!Global.isQK(Global.currentKeyboard)) {
                if(t9InputViewGroup.isShown())t9InputViewGroup.startAnimation(anim);
            } else {
                if(qkInputViewGroup.isShown()) qkInputViewGroup.startAnimation(anim);
            }
            bottomBarViewGroup.setButtonAlpha(autoDownAlpha);
//            if (bottomBarViewGroup.isShown())bottomBarViewGroup.show(anim);
            if (functionViewGroup.isShown()) functionViewGroup.startAnimation(anim);
            if (specialSymbolChooseViewGroup.isShown()) specialSymbolChooseViewGroup.startAnimation(anim);
            if (quickSymbolViewGroup.isShown()) quickSymbolViewGroup.startAnimation(anim);
//            if (candidatesViewGroup.isShown())candidatesViewGroup.show(anim);
            candidatesViewGroup.setButtonAlpha(autoDownAlpha);
            largeCandidateButton.setAlpha(autoDownAlpha);
//            preEditPopup.setButtonAlpha(autoDownAlpha);
            isUpAlpha = true;
        }

        /**
         * 功能：减小透明度
         * 调用时机：键盘上的任意touch事件
         */
        public void DownAlpha() {
            if (!mWindowShown) return;
            Animation anim = AnimationUtils.loadAnimation(SoftKeyboard.this, R.anim.show);
            if (!Global.isQK(Global.currentKeyboard)) {
                if (t9InputViewGroup.isShown()){
                    t9InputViewGroup.clearAnimation();
                    t9InputViewGroup.startAnimation(anim);
                }
            } else {
                if (qkInputViewGroup.isShown()) qkInputViewGroup.startAnimation(anim);
            }
            bottomBarViewGroup.setButtonAlpha(autoDownAlphaTop);
            if (functionViewGroup.isShown()) functionViewGroup.startAnimation(anim);
            if (specialSymbolChooseViewGroup.isShown()) specialSymbolChooseViewGroup.startAnimation(anim);
            if (quickSymbolViewGroup.isShown()) quickSymbolViewGroup.startAnimation(anim);
            if (prefixViewGroup.isShown()) prefixViewGroup.startAnimation(anim);
//            preEditPopup.setButtonAlpha(autoDownAlphaTop);
            candidatesViewGroup.setButtonAlpha(autoDownAlphaTop);
            largeCandidateButton.setAlpha(autoDownAlphaTop);
            isUpAlpha =false;
        }

        public void handleAlpha(int eventAction) {
            Global.keyboardRestTimeCount = 0;
            if (eventAction == MotionEvent.ACTION_DOWN) {
                if (isUpAlpha){
                    DownAlpha();
                }
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
    private void startShowAnimation() {
        if (!Global.isQK(Global.currentKeyboard)) {
            qkInputViewGroup.setVisibility(View.GONE);
            t9InputViewGroup.startShowAnimation();
        } else {
            t9InputViewGroup.setVisibility(View.GONE);
            qkInputViewGroup.show();
        }
        prefixViewGroup.setVisibility(View.GONE);
        quickSymbolViewGroup.setVisibility(View.VISIBLE);

        functionViewGroup.setVisibility(View.VISIBLE);
        functionViewGroup.startShowAnimation();

        bottomBarViewGroup.setVisibility(View.VISIBLE);
        bottomBarViewGroup.startShowAnimation();

        if (keyboardLayout.getBackground() != null) {
            keyboardLayout.getBackground().setAlpha((int) (Global.keyboardViewBackgroundAlpha*255));
        }
    }

    /**
     * 键盘的退出动画
     * */
    private void startOutAnimation() {
        if (!Global.isQK(Global.currentKeyboard)) {
            qkInputViewGroup.setVisibility(View.GONE);
            t9InputViewGroup.startHideAnimation();
        } else {
            t9InputViewGroup.setVisibility(View.GONE);
            qkInputViewGroup.hide();
        }
        if (keyboardLayout.getBackground() != null)
            keyboardLayout.getBackground().setAlpha(0);

        if (largeCandidateButton.isShown()) {
            largeCandidateButton.setVisibility(View.GONE);
        }
         if (quickSymbolViewGroup.isShown()) {
            quickSymbolViewGroup.hide();
        }
        if (specialSymbolChooseViewGroup.isShown()) {
            specialSymbolChooseViewGroup.setVisibility(View.GONE);
        }
        prefixViewGroup.hide();
        functionViewGroup.startHideAnimation();
        bottomBarViewGroup.startHideAnimation();
        candidatesViewGroup.setVisibility(View.GONE);
        largeCandidateButton.setVisibility(View.GONE);

        lightViewManager.invisibleLightView();

    }

    @Override
    public View onCreateInputView() {
        return super.onCreateInputView();
    }

    /**
     * 启动输入法，在输入法未获得输入法焦点的时候调用
     * */
    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
//        Log.d("WIVE","onStartInputView");
        initInputParam.initKernal(this.getApplicationContext());

        Global.inLarge = false;
        //设置enter_text
        bottomBarViewGroup.setEnterText(info, Global.currentKeyboard);
        /*
         * 获取中文键盘类型
         */
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        zhKeyboard = sharedPreferences.getString("KEYBOARD_SELECTOR", "2").equals("1") ? Global.KEYBOARD_T9 : Global.KEYBOARD_QK;
        if (mWindowShown) {
            int keyboard = functionsC.getKeyboardType(info);
            keyBoardSwitcher.switchKeyboard(keyboard, true);
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
//        Log.d("WIVE","onWindowShown");
        MobclickAgent.onResume(this);
        mWindowShown = true;
        mHandler.removeMessages(MSG_HIDE);
        mHandler.removeMessages(MSG_CLEAR_ANIMATION);
        mHandler.removeMessages(MSG_REMOVE_INPUT);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        keyboardTouchEffect.loadSetting(sp);
        Global.mCurrentAlpha = sp.getFloat("CURRENT_ALPHA", 1f);
        Global.shadowRadius = Integer.parseInt(sp.getString("SHADOW_TEXT_RADIUS", "5"));
        Global.slideDeleteSwitch = sp.getBoolean("SLIDE_DELETE_CHECK", true);

        viewManagerC.addInputView();
        lightViewManager.addToWindow();
        mInputViewGG.setVisibility(View.VISIBLE);

        try {
            quickSymbolViewGroup.updateSymbolsFromFile();
        } catch (IOException e) {
            CommonFuncs.showToast(this, "Sorry,There is an error when program load symbols from file");
        }

        EditorInfo info = this.getCurrentInputEditorInfo();
        keyBoardSwitcher.switchKeyboard(functionsC.getKeyboardType(info));

        keyboardParams.flags = ABLE_LAYOUTPARAMS_FLAG;
        if (keyboardLayout.isShown()) {
            WindowManager wm = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
            wm.updateViewLayout(keyboardLayout, keyboardParams);
        }

        bottomBarViewGroup.spaceButton.setText(InputMode.halfToFull(sp.getString("ZH_SPACE_TEXT", "空格")));
        if(sp.getBoolean("AUTO_DOWN_ALPHA_CHECK",true))transparencyHandle.startAutoDownAlpha();

        keyboard_animation_switch = sp.getBoolean("KEYBOARD_ANIMATION",true);
        if (keyboard_animation_switch) {
            startShowAnimation();
        } else {
            keyBoardSwitcher.showKeyboard(functionsC.getKeyboardType(info), false);
        }
        skinUpdateC.updateSkin();
        skinUpdateC.updateShadowLayer();
        super.onWindowShown();
    }

    /**
     * 回收输入框
     * */
    @Override
    public void onWindowHidden() {
//        Log.d("WIVE","onWindowHidden");
        MobclickAgent.onPause(this);
        mWindowShown = false;
        Kernel.cleanKernel();
        refreshDisplay();
        clearAnimation();
        t9InputViewGroup.updateFirstKeyText();
        if(keyboard_animation_switch){
            startOutAnimation();
            mHandler.sendEmptyMessageDelayed(MSG_CLEAR_ANIMATION,400);
            mHandler.sendEmptyMessageDelayed(MSG_REMOVE_INPUT,400);
        } else {
            viewManagerC.removeInputView();
        }

        if (mSetKeyboardSizeViewOn) {
            mOnSizeChangeListener.onFinishSetting();
        }

        keyboardParams.flags = DISABLE_LAYOUTPARAMS_FLAG;
        if (keyboardLayout.isShown()) {
            WindowManager wm = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
            wm.updateViewLayout(keyboardLayout, keyboardParams);
        }
        lightViewManager.removeView();
        mHandler.removeMessages(MSG_HIDE);
        super.onWindowHidden();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
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
