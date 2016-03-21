package com.hit.wi.jni;

public final class WIInputMethod {
    /**
     * 初始化输入法（词库读取等）
     */
    public static final native int InitWiIme(final String kerneldictpath,
                                             final String userdictpath);
//
//	/**
//	 * 初始化输入法
//	 */
//	public static final native int InitWIIM(final String dictpath,
//			final String usrdictpath);// /init WI inputmethod

    /**
     * 音字转换 一个字符一个字符输入，或者是一个串输入
     */
    public static final native int GetAllWords(final String words); // /get all
    // words

    /**
     * 获取候选项的数目
     */
    public static final native int GetWordsNumber(); // /get the number of words

    /**
     * 得到index对应的词语
     */
    public static final native String GetWordByIndex(final int index); // get a word by
    // index

    /**
     * 获取预测拼音韵母
     */
    public static final native String GetPredictA();

    public static final native String GetPredictE();

    public static final native String GetPredictI();

    public static final native String GetPredictO();

    public static final native String GetPredictU();

    public static final native String GetPredictH();

    /**
     * 执行选择动作，选择了index指向的词语
     */
    public static final native String GetWordSelectedWord(final int index); // get the word
    // you selected

    /**
     * 得到index指向的拼音
     */
    public static final native String GetWordsPinyin(final int index); // get thi pinyin of
    // a word

    /**
     * 删除操作
     */
    public static final native int DeleteAction();

    /**
     * enter键操作
     */
    public static final native String ReturnAction(); // get word by retur action

    /**
     * 空格选词操作
     */
    public static final native String SpaceAction(); // get word by space action

    /**
     * 清空内核
     */
    public static final native int CLeanKernel(); // clean kernel stirng

    /**
     * 传入一个串，直接进行翻译
     */
    public static final native int SetInputString(final String input); // /set the input
    // string

    /**
     * 释放内存
     */
    public static final native int FreeIme(); // /free ime

    /**
     * 设置输入法
     */
    public static final native int SetWiIme(final int model); // set ime

    /**
     * 重设置输入法
     */
    public static final native int ResetWiIme(final int model);

    // //////////////////////////////////////////////////////////////
}