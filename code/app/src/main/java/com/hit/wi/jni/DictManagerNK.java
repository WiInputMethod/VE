package com.hit.wi.jni;


public final class DictManagerNK {
    /*
     * 导入新词
     */
    public static native int ImportNewWords(String jword, String jsyl);

    /*
     * 导出新词
     */
    public static native String ExportUserWords(int index);
}
