package com.hit.wi.jni;


public final class DictManager {
//	public static final int USERDEF_SUCCESS = 0;
//	public static final int USERDEF_WORD_FOUND = 1;
//	public static final int USERDEF_KEY_TOO_LONG = 2;
//	public static final int USERDEF_KEY_TOO_SHORT = 3;
//	public static final int USERDEF_VALUE_TOO_LONG = 4;
//	public static final int USERDEF_VALUE_TOO_SHORT = 5;
//	public static final int USERDEF_INVALID_FORMAT = 6;
//	public static final int USERDEF_DICT_FULL = 7;
//	public static final int USERDEF_CANNOT_FOUND = 8;

    /*
     * 插入联系人
     */
    public static native int InsertContactList(String js);

    /*
     * 清空联系人
     */
    public static native int CleanUserlessContactList();

    /*
     * 备份词库，给一个路径 *
     */
    public static native int BackupData(String path);

    /*
     * 还原词库，给定路径
     */
    public static native int RestoreData(final String path);

    /*
     * 导入新词
     */
    public static native int ImportNewWords(String jword, String jsyl);

    /*
     * 导出新词
     */
    public static native int ExportUserWords(int index);

    /*
     * 添加用户自定义词，例如：关毅 guanyi
     */
    public static native int AddUserDefWord(final String word,
                                            final String value);

    /*
     * 删除用户自定义词，例如：关毅 guanyi
     */
    public static native int DeleteUserdefWord(final String word,
                                               final String value);

    /*
     * 修改用户自定义词，例如：关毅 guanyi 广义 guangyi
     */
    public static native int EditUserdefWord(final String fpinyin,
                                             final String fword, final String tpinyin, final String tword);

    /**
     * 获取用户自定义词的个数
     *
     * @return
     */
    public static native int GetUserdefWordNum();

    /**
     * 获取第index个用户自定义词的拼音
     *
     * @param index
     * @return
     */
    public static native String GetUserdefWordKeyByIndex(final int index);

    /**
     * 获取第index个用户自定义词的值
     *
     * @param index
     * @return
     */
    public static native String GetUserdefWordValueByIndex(final int index);

    public static native int GetUserWordNum();

    public static native String GetUserWordByIndex(final int index);

    public static native int DeleteUserWord(final int index);
}
