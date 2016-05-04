package com.hit.wi.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by daofa on 2016/4/10.
 * 字符串的处理方法
 */
public class StringUtil {

    /**
     * @param strings
     * @return 字符串生成的列表
     */
    public static List<String> convertStringstoList(String[] strings) {
        List<String> arraylist = new ArrayList<String>();

        for (String string : strings) {
            arraylist.add(string);
        }
        if (arraylist.size() == 1 && arraylist.get(0) == "")
            arraylist = Collections.EMPTY_LIST;
        return arraylist;
    }

    public static String[] convertListToString(List<String> list) {
        String strings[] = list.toArray(new String[list.size()]);
        return strings;
    }
    /**
     * 检验字符串str中是否全为英文字母
     *
     * @param str
     * @return
     */
    public static boolean isAllLetter(String str) {
        char tmp;
        for (int i = 0; i < str.length(); i++) {
            tmp = str.charAt(i);
            if (!((tmp >= 'a' && tmp <= 'z') || (tmp >= 'A' && tmp <= 'Z'))) {
                return false;
            }
        }
        return true;
    }
}
