package com.hit.wi.jni;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.hit.wi.define.FilePath;

import java.util.Arrays;

public final class InitInputParam {
    public final static int RESET = 0; // reset input method，复位参数
    // 模糊音设置
    final static int FUZZY_SYLLABLE_AN_ANG = 1;
    final static int FUZZY_SYLLABLE_EN_ENG = 2;
    final static int FUZZY_SYLLABLE_IN_ING = 3;
    final static int FUZZY_SYLLABLE_IAN_IANG = 4;
    final static int FUZZY_SYLLABLE_UAN_UANG = 5;
    final static int FUZZY_SYLLABLE_Z_ZH = 6;
    final static int FUZZY_SYLLABLE_C_CH = 7;
    final static int FUZZY_SYLLABLE_S_SH = 8;
    final static int FUZZY_SYLLABLE_L_N = 9;
    final static int FUZZY_SYLLABLE_F_H = 10;
    final static int FUZZY_SYLLABLE_R_L = 11;

    public final static int QUAN_PIN = 12; // 全拼
    final static int HUN_PIN = 13;
    final static int WI_SHUANG_PIN = 14;
    final static int ABC_SHUANG_PIN = 15;
    final static int SOGOU_SHUANG_PIN1 = 16;
    final static int JIAJIA_SHUANG_PIN = 17;
    final static int MS2003_SHUANG_PIN = 18;
    final static int XIAOHE_SHUANG_PIN = 19;
    final static int ZIGUANG_SHUANG_PIN = 20;
    final static int ZIRANMA_SHUANG_PIN = 21;
    final static int ZWZX_SHUANG_PIN = 22;
    final static int ERROR_CORRECT = 23;// 拼写纠错设置

    // 以下五项为默认设置
    final static int SENTENCE_INPUT = 24;// 以整条拼音输入
    final static int MACHINE_LEARNING = 25;// 机器学习
    final static int UNCOMMON_WORD = 26;// 内部默认设置

    final static int NO_ASSOCIATION = 27;// 关闭联想
    final static int SINGLE_ASSOCIATION = 28;// 开启单联想
    final static int MULTI_ASSOCIATION = 29;// 双联想

    final static int JIANPIN_SWITCH = 30;// 简拼设置
    final static int SHUANGPIN_EDIT_SWITCH = 31;// 双拼句内编辑
    final static int SHUANGPIN_ERROR_CORRECT = 32;// 双拼的拼写纠错
    final static int CHSTOCHT_SWITCH = 33;
    final static int TERM_DICT_WORD = 34;// 系统文件加载设置
    final static int EMOJI_SWITCH = 35;//EMOJI表情

    final static String[] SWITCH_NAME = {
            "RESET",
            "FUZZY_SYLLABLE_AN_ANG",
            "FUZZY_SYLLABLE_EN_ENG",
            "FUZZY_SYLLABLE_IN_ING",
            "FUZZY_SYLLABLE_IAN_IANG",
            "FUZZY_SYLLABLE_UAN_UANG",
            "FUZZY_SYLLABLE_Z_ZH",
            "FUZZY_SYLLABLE_C_CH",
            "FUZZY_SYLLABLE_S_SH",
            "FUZZY_SYLLABLE_L_N",
            "FUZZY_SYLLABLE_F_H",
            "FUZZY_SYLLABLE_R_L",
            "QUAN_PIN",
            "HUN_PIN",
            "WI_SHUANG_PIN",
            "ABC_SHUANG_PIN",
            "SOGOU_SHUANG_PIN1",
            "JIAJIA_SHUANG_PIN",
            "MS2003_SHUANG_PIN",
            "XIAOHE_SHUANG_PIN",
            "ZIGUANG_SHUANG_PIN",
            "ZIRANMA_SHUANG_PIN",
            "ZWZX_SHUANG_PIN",
            "ERROR_CORRECT",
            "SENTENCE_INPUT",
            "MACHINE_LEARNING",
            "UNCOMMON_WORD",
            "NO_ASSOCIATION",
            "SINGLE_ASSOCIATION",
            "MULTI_ASSOCIATION",
            "JIANPIN_SWITCH",
            "SHUANGPIN_EDIT_SWITCH",
            "SHUANGPIN_ERROR_CORRECT",
            "CHSTOCHT_SWITCH",
            "TERM_DICT_WORD",
            "EMOJI_SWITCH"
    };
    final static boolean[] SWITCH_DEFAULT = {
            false,//RESET
            false,//FUZZY_SYLLABLE_AN_ANG
            false,//FUZZY_SYLLABLE_EN_ENG
            false,//FUZZY_SYLLABLE_IN_ING
            false,//FUZZY_SYLLABLE_IAN_IANG
            false,//FUZZY_SYLLABLE_UAN_UANG
            false,//FUZZY_SYLLABLE_Z_ZH
            false,//FUZZY_SYLLABLE_C_CH
            false,//FUZZY_SYLLABLE_S_SH
            false,//FUZZY_SYLLABLE_L_N
            false,//FUZZY_SYLLABLE_F_H
            false,//FUZZY_SYLLABLE_R_L
            false,//QUAN_PIN
            false,//HUN_PIN
            false,//WI_SHUANG_PIN
            false,//ABC_SHUANG_PIN
            false,//SOGOU_SHUANG_PIN1
            false,//JIAJIA_SHUANG_PIN
            false,//MS2003_SHUANG_PIN
            false,//XIAOHE_SHUANG_PIN
            false,//ZIGUANG_SHUANG_PIN
            false,//ZIRANMA_SHUANG_PIN
            false,//ZWZX_SHUANG_PIN
            true,//ERROR_CORRECT
            true,//SENTENCE_INPUT
            false,//MACHINE_LEARNING
            false,//UNCOMMON_WORD
            false,//NO_ASSOCIATION
            false,//SINGLE_ASSOCIATION
            true,//MULTI_ASSOCIATION
            true,//JIANPIN_SWITCH
            true,//SHUANGPIN_EDIT_SWITCH
            false,//SHUANGPIN_ERROR_CORRECT
            false,//CHSTOCHT_SWITCH
            false,//TERM_DICT_WORD
            false,//EMOJI_SWITCH
    };
    final static String[] NK_SWITCH_NAME = {"RESET", "CANGJIE_IME",
            "ZHUYIN_IME", "BIHUA_IME", "JIANCANG_IME", "PINYIN_IME",
            "ERROR_CORRECT", "JIANPIN_SWITCH", "QUANJIANPAN_MODE",
            "JIUJIAN_MODE", "SHISIJIAN_MODE", "PREFIX_FILTER",
            "NEXT_WORD_PREDICTION", "MULTI_PREDICTION", "EMOJI_SWITCH"

    };
    final static int NK_RESET = 0;
    final static int NK_CANGJIE_IME = 1;
    final static int NK_ZHUYIN_IME = 2;
    final static int NK_BIHUA_IME = 3;
    final static int NK_JIANCANG_IME = 4;
    final static int NK_PINYIN_IME = 5;
    final static int NK_ERROR_CORRECT = 6;
    final static int NK_JIANPIN_SWITCH = 7;
    final static int NK_QUANJIANPAN_MODE = 8;
    final static int NK_JIUJIAN_MODE = 9;
    final static int NK_SHISIJIAN_MODE = 10;
    final static int NK_PREFIX_FILTER = 11;
    final static int NK_NO_PREDICTION = 12;
    final static int NK_NEXT_WORD_PREDICTION = 13;
    final static int NK_MULTI_PREDICTION = 14;
    final static int NK_EMOJI_SWITCH = 15;
    final static int NK_ENGLISH_DICT_SWITCH = 16;

    final boolean[] SWITCH_STATE;

    final static int PREDICTION_SETTING_OFFSET = 15;

    public InitInputParam() {
        SWITCH_STATE = new boolean[SWITCH_NAME.length];
    }

    private final void reset() {
        Arrays.fill(SWITCH_STATE, false);
        SWITCH_STATE[SENTENCE_INPUT] = true;
        SWITCH_STATE[MACHINE_LEARNING] = true;
        SWITCH_STATE[UNCOMMON_WORD] = true;
        SWITCH_STATE[TERM_DICT_WORD] = true;
        SWITCH_STATE[QUAN_PIN] = true;
//		SWITCH_STATE[EMOJI_SWITCH] = true;
    }

    public final int initKernal(final Context context) {
        /*
         * 输入法初始化时进行输入法参数设置，首先读取xml配置文件，然后进行相关参数的设置
		 */
        //九键与全键盘共用
        reset();

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        WIInputMethodNK.SetWiIme(RESET);// 复位设置内核参数
        WIInputMethod.SetWiIme(RESET);// 复位设置内核参数

        for (int i = SWITCH_NAME.length - 1; i > 0; i--) {
            if (sp.getBoolean(SWITCH_NAME[i], SWITCH_DEFAULT[i])) {
                SWITCH_STATE[i] = true;
            }
        }

        if (sp.getBoolean(SWITCH_NAME[EMOJI_SWITCH], false)) {
            WIInputMethodNK.SetWiIme(NK_EMOJI_SWITCH);
        }
        WIInputMethodNK.SetWiIme(NK_JIANPIN_SWITCH);
        WIInputMethodNK.SetWiIme(NK_PINYIN_IME);
        WIInputMethodNK.SetWiIme(NK_PREFIX_FILTER);
        WIInputMethodNK.SetWiIme(NK_JIUJIAN_MODE);
        WIInputMethodNK.SetWiIme(NK_ENGLISH_DICT_SWITCH);

        for (int i = SWITCH_STATE.length - 1; i >= 0; i--) {
            if (SWITCH_STATE[i] && i != EMOJI_SWITCH) {//不让内核输出emoij了
                WIInputMethod.SetWiIme(i);
            }
        }

        final int shuangpinModel = Integer.parseInt(sp.getString(
                "shuangpin_selector", "0"));
        if (shuangpinModel != 0) {
            WIInputMethod.ResetWiIme(QUAN_PIN);
            WIInputMethod.SetWiIme(shuangpinModel);
        }

        final int lianxiangModel = Integer.parseInt(sp.getString("LIANXIANG_SELECTOR", "-1"));
        if (lianxiangModel != -1) {
            WIInputMethodNK.SetWiIme(lianxiangModel - PREDICTION_SETTING_OFFSET);
            WIInputMethod.SetWiIme(lianxiangModel);
        }

        WIInputMethodNK.InitWiIme(FilePath.ROOT_DIR, FilePath.ROOT_DIR);
        WIInputMethod.InitWiIme(FilePath.ROOT_DIR, FilePath.ROOT_DIR);
        return 0;
    }
}
