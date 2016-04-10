package com.hit.wi.util;

/**
 * Created by purebleusong on 2016/4/8.
 */
public class CharUtil {
    public static boolean isEnglish(char c) {
        int codePoint = (int) c;
        return ('A' <= codePoint && codePoint <= 'Z') || ('a' <= codePoint && codePoint <= 'z');
    }
}
