package com.hit.wi.util;

/**
 * Created by daofa on 2016/4/10.
 */
public class InputMode {
    public static String halfToFull(String halfCharacterString) {
        if (null == halfCharacterString) {
            return "";
        }
        char[] c = halfCharacterString.toCharArray();
        for (int i = 0; i < c.length; i++) {
            //判断是否是半角空格
            if (c[i] == 32) {
                c[i] = (char) 12288;
            } else if (c[i] > 32 && c[i] < 127) {
                // 对于其他半角字符，加65248转换为全角字符
                c[i] = (char) (c[i] + 65248);
            }
        }
        return new String(c);
    }

    public static String fullToHalf(String fullCharacterString) {
        if (null == fullCharacterString) {
            return "";
        }
        char[] c = fullCharacterString.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
            } else if (c[i] > 65280 && c[i] < 65375) {
                c[i] = (char) (c[i] - 65248);
            }
        }
        return new String(c);
    }
}
