package com.hit.wi.jni;

import com.hit.wi.util.CharUtil;

import java.util.HashSet;

/**
 * Created by purebleusong on 2016/4/8.
 */
public class Kernel {
    private static final int LETTER_TO_NUMBER[]
            = {2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6, 7, 7, 7, 7, 8, 8, 8, 9, 9, 9, 9};
    public static final int QWERT = 0;
    public static final int NINE_KEY = 1;

    private static int sKernelType = QWERT;
    private static boolean sIsBatch = false;
    private static HashSet<OnWordsChangedListener> sListeners = new HashSet<OnWordsChangedListener>();
    private static HashSet<OnWordsChangedListener> sTmpListeners = new HashSet<OnWordsChangedListener>();

    private static final StringBuilder sTmpBuilder = new StringBuilder();

    static {
        System.loadLibrary("WIIM_NK");
        System.loadLibrary("WIIM");
    }

    /**
     * @param type : 这个类中的常量字段
     */
    public static void setKernelType(int type) {
        sKernelType = type;
    }
    public static int getKernelType() {
        return sKernelType;
    }

    /**
     * 初始化输入法（词库读取等）
     */
    public static int initWiIme(final String kerneldictpath,
                                final String userdictpath) {
        return sKernelType == QWERT ?
                WIInputMethod.InitWiIme(kerneldictpath, userdictpath) :
                WIInputMethodNK.InitWiIme(kerneldictpath, userdictpath);
    }

    /**
     * 音字转换 一个字符一个字符输入，或者是一个串输入
     */
    public static int inputPinyin(final String words) {
        int result = sKernelType == QWERT ?
                WIInputMethod.GetAllWords(words) :
                WIInputMethodNK.GetAllWords(words);
        changed();
        return result;

    }

    /**
     * 获取候选项的数目
     */
    public static int getWordsNumber() {
        int wordsNumber = 0;
        switch (sKernelType) {
            case QWERT:
                wordsNumber = WIInputMethod.GetWordsNumber();
                break;
            case NINE_KEY:
                wordsNumber = WIInputMethodNK.GetWordsNumber();
                break;
            default:
                break;
        }
        return wordsNumber;
    }

    /**
     * 得到index对应的词语
     */
    public static String getWordByIndex(final int index) {
        switch (sKernelType) {
            case QWERT:
                return WIInputMethod.GetWordByIndex(index);
            case NINE_KEY:
                return WIInputMethodNK.GetWordByIndex(index);
            default:
                return null;
        }
    }

    public static String getPrefix(int index) {
        if (sKernelType == QWERT) {
            return null;
        } else if (sKernelType == NINE_KEY) {
            return WIInputMethodNK.GetPrefixByIndex(index);
        } else {
            return null;
        }
    }

    public static void selectPrefix(int index) {
        if (sKernelType == NINE_KEY) {
            WIInputMethodNK.GetPrefixSelectedPrefix(index);
            changed();
        }
    }

    public static int getPrefixNumber() {
        if (sKernelType == NINE_KEY) {
            return WIInputMethodNK.GetPrefixNumber();
        } else {
            return 0;
        }
    }

    /**
     * 获取预测拼音韵母
     */
    public static String getPredictA() {
        return WIInputMethod.GetPredictA();
    }

    public static String getPredictE() {
        return WIInputMethod.GetPredictE();
    }

    public static String getPredictI() {
        return WIInputMethod.GetPredictI();
    }

    public static String getPredictO() {
        return WIInputMethod.GetPredictO();
    }

    public static String getPredictU() {
        return WIInputMethod.GetPredictU();
    }

    public static String getPredictH() {
        return WIInputMethod.GetPredictH();
    }

    /**
     * 执行选择动作，选择了index指向的词语
     */
    public static String getWordSelectedWord(final int index) {
        String result;
        switch (sKernelType) {
            case QWERT:
                result = WIInputMethod.GetWordSelectedWord(index);
                changed();
                return result;
            case NINE_KEY:
                result = WIInputMethodNK.GetWordSelectedWord(index);
                changed();
                return result;
            default:
                return null;
        }
    }

    /**
     * 得到index指向的拼音,九键显示为英文
     */
    public static String getWordsShowPinyin() {
        if (sKernelType == QWERT) {
            return WIInputMethod.GetWordsPinyin(0);
        } else if (sKernelType == NINE_KEY) {
            return WIInputMethodNK.GetWordsPinyin(0);
        } else return null;
    }

    /**
     * 删除操作
     */
    public static int deleteAction() {
        int number = 0;
        if (sKernelType == QWERT) {
            number = WIInputMethod.DeleteAction();
        } else if (sKernelType == NINE_KEY) {
            number = WIInputMethodNK.DeleteAction();
        }
        changed();
        return number;
    }

    /**
     * enter键操作
     */
    public static String returnAction() {
        String toReturn = "";
        if (sKernelType == QWERT) {
            toReturn = WIInputMethod.ReturnAction();
        } else if (sKernelType == NINE_KEY) {
            toReturn = WIInputMethodNK.ReturnAction();
        }
        changed();
        return toReturn;
    }

    /**
     * 清空内核
     */
    public static int cleanKernel() {
        int count = 0;
        if (sKernelType == QWERT) {
            count = WIInputMethod.CLeanKernel();
        } else if (sKernelType == NINE_KEY) {
            count = WIInputMethodNK.CLeanKernel();
        }
        changed();
        return count;
    }

    /**
     * 传入一个串，直接进行翻译
     */
    public static int setInputString(final String input) {
        int count = 0;
        if (sKernelType == QWERT) {
            WIInputMethod.CLeanKernel();
            count = WIInputMethod.SetInputString(input);
        } else if (sKernelType == NINE_KEY) {
            WIInputMethodNK.CLeanKernel();
            sTmpBuilder.delete(0, sTmpBuilder.length());
            for (int i = 0; i < input.length(); i++) {
                if (CharUtil.isEnglish(input.charAt(i)))
                    sTmpBuilder.append(
                            LETTER_TO_NUMBER[(int) input.charAt(i) - (int) 'a']);
                else {
                    sTmpBuilder.append(input.charAt(i));
                }
            }
            String inputString = sTmpBuilder.toString();
            count = WIInputMethodNK.SetInputString(inputString);
        }
        changed();
        return count;
    } // set the inputstring

    /**
     * 释放内存
     */
    public static void freeIme() {
        WIInputMethod.FreeIme();
        WIInputMethodNK.FreeIme();
    } // /free ime

    /**
     * 设置输入法
     */
    public static int setWiIme(final int model) {
        if (sKernelType == QWERT) {
            return WIInputMethod.SetWiIme(model);
        } else if (sKernelType == NINE_KEY) {
            return WIInputMethodNK.SetWiIme(model);
        } else return 0;
    } // set ime

    /**
     * 重设置输入法
     */
    public static int resetWiIme(final int model) {
        if (sKernelType == QWERT) {
            return WIInputMethod.ResetWiIme(model);
        } else if (sKernelType == NINE_KEY) {
            return WIInputMethodNK.ResetWiIme(model);
        } else return 0;
    }

    public static void startBatch() {
        sIsBatch = true;
    }

    public static void endBatch() {
        if (!sIsBatch) {
            return;
        }
        sIsBatch = false;
        changed();
    }

    private static void changed() {
        if (sIsBatch) {
            return;
        }

        sTmpListeners.clear();
        sTmpListeners.addAll(sListeners);
        for (OnWordsChangedListener listener : sTmpListeners) {
            listener.OnWordsChanged();
        }
    }

    public static void addWordsChangedListener(OnWordsChangedListener listener) {
        sListeners.add(listener);
    }

    public static void removeListener(OnWordsChangedListener listener) {
        sListeners.remove(listener);
    }

    public static void removeAllListener() {
        sListeners.clear();
    }

    public interface OnWordsChangedListener {
        void OnWordsChanged();
    }

    public static boolean hasNoListener() {
        return sListeners.isEmpty();
    }
}
