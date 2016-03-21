package com.hit.wi.jni;

public final class WIInputMethodNK {
    public static final int SUCCESS = 0;
    public static final int ERROR = -1;

    public static final int RESET = 0;
    public static final int CANGJIE_IME = 1;
    public static final int ZHUYIN_IME = 2;
    public static final int BIHUA_IME = 3;
    public static final int JIANCANG_IME = 4;
    public static final int PINYIN_IME = 5;
    public static final int ERROR_CORRECT = 6;
    public static final int JIANPIN_SWITCH = 7;
    public static final int QUANJIANPAN_MODE = 8;
    public static final int JIUJIAN_MODE = 9;
    public static final int SHISIJIAN_MODE = 10;
    public static final int PREFIX_FILTER = 11;
    public static final int NEXT_WORD_PREDICTION = 12;
    public static final int EMOJI_SWITCH = 13;

    public static final int OUT_FILE_CAN_NOT_BUILD = -11;
    public static final int IN_FILE_CAN_NOT_OPEN = -12;
    public static final int SETTING_NOT_LOAD = -13;
    public static final int USR_DICT_NOT_FOUND = -1001;
    public static final int BACKUP_FILE_CAN_NOT_BUILD = -1002;
    public static final int EMOJI_DICT_NOT_FOUND = -1003;
    public static final int BACKUP_EMOJI_FILE_CAN_NOT_BUILD = -1004;
    public static final int INSERT_USR_WORD_FAILED = -1005;
    public static final int FLUSH_EMOJI_FAILED = -1006;

    /**
     * 初始化输入法（词库读取等）
     */
    public static native int InitWiIme(final String kerneldictpath,
                                       final String userdictpath);
//
//	/**
//	 * 初始化输入法
//	 */
//	public static final native int InitWIIM(final String dictpath,
//			final String usrdictpath);// /init WI inputmethod

    /**
     * 音字转换 一个字符一个字符输入法，或者是一个串输入
     */
    public static native int GetAllWords(final String words); // /get all
    // words

    /**
     * 获取候选项的数目
     */
    public static native int GetWordsNumber(); // /get the number of words

    /**
     * 获取前缀拼音数目
     */
    public static native int GetPrefixNumber();

    /**
     * 得到index对应的前缀音
     */
    public static native String GetPrefixByIndex(final int index);

    /**
     * 得到index对应的词语
     */
    public static native String GetWordByIndex(final int index); // get a word by
    // index

    /**
     * 执行选择动作，选择了index指向的词语
     */
    public static native String GetWordSelectedWord(final int index); // get the word
    // you selected

    /**
     * 执行选择动作, 选择了index指向的前缀音
     */
    public static native String GetPrefixSelectedPrefix(final int index);

    /**
     * 得到index指向的拼音
     */
    public static native String GetWordsPinyin(final int index); // get thi pinyin of
    // a word

    /**
     * 删除操作
     */
    public static native int DeleteAction();

    /**
     * enter键操作
     */
    public static native String ReturnAction(); // get word by retur action

    /**
     * 空格选词操作
     */
    public static native String SpaceAction(); // get word by space action

    /**
     * 清空内核
     */
    public static native int CLeanKernel(); // clean kernel stirng

    /**
     * 传入一个串，直接进行翻译
     */
    public static native int SetInputString(final String input); // /set the input
    // string

    /**
     * 释放内存
     */
    public static native int FreeIme(); // /free ime

    /**
     * 设置输入法
     */
    public static native int SetWiIme(final int model); // set ime

    /**
     * 重设置输入法
     */
    public static native int ResetWiIme(final int model);
    // //////////////////////////////////////////////////////////////
}