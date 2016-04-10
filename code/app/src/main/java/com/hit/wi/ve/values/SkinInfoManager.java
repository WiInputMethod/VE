package com.hit.wi.ve.values;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;
import com.hit.wi.ve.R;
import com.hit.wi.ve.datastruct.SkinInfoDataStruct;
import com.hit.wi.ve.functions.FileManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 皮肤的背景信息
 * Created by Administrator on 2015/5/23.
 */
public class SkinInfoManager {
    public SkinInfoDataStruct skinData;
    private FileManager fileManager;
    private List<Integer> dataList;
    private String filenameSuffix;
    private SharedPreferences sp;
    private static SkinInfoManager skinInfoManagerInstance = new SkinInfoManager();

    private SkinInfoManager() {
        skinData = new SkinInfoDataStruct();
        fileManager = FileManager.getInstance();
        dataList = new ArrayList<Integer>();
        filenameSuffix = ".skin";
    }

    public static SkinInfoManager getSkinInfoManagerInstance() {
        return skinInfoManagerInstance;
    }

    public void loadConfigurationFromFile(String skinName) throws IOException {
        dataList = fileManager.readIntegerListFromFile(skinName);
        skinData.skinName = skinName;
        convertTheDataListToTheSkinData();
    }

    private int[] textThemes = {
            R.array.THEME_WP_TEXT_COLORS,
            R.array.THEME_CLASSIC_TEXT_COLORS,
            R.array.THEME_AZURE_TEXT_COLORS,
            R.array.THEME_ORANGE_TEXT_COLORS,
            R.array.THEME_PURPLE_TEXT_COLORS,
            R.array.THEME_PINK_TEXT_COLORS,
            R.array.THEME_GRAY_TEXT_COLORS,
            R.array.THEME_GREEN_TEXT_COLORS,
            R.array.THEME_LIGHT_TEXT_COLORS,
            R.array.THEME_DARK_TEXT_COLORS,
    };

    private int[] backThemes = {
            R.array.THEME_WP_BUTTON_BACK_COLORS,
            R.array.THEME_CLASSIC_BUTTON_BACK_COLORS,
            R.array.THEME_AZURE_BUTTON_BACK_COLORS,
            R.array.THEME_ORANGE_BUTTON_BACK_COLORS,
            R.array.THEME_PURPLE_BUTTON_BACK_COLORS,
            R.array.THEME_PINK_BUTTON_BACK_COLORS,
            R.array.THEME_GRAY_BUTTON_BACK_COLORS,
            R.array.THEME_GREEN_BUTTON_BACK_COLORS,
            R.array.THEME_LIGHT_BUTTON_BACK_COLORS,
            R.array.THEME_DARK_BUTTON_BACK_COLORS,
    };

    private int[] shadowThemes = {
            R.integer.THEME_WP_SHADOW,
            R.integer.THEME_CLASSIC_SHADOW,
            R.integer.THEME_AZURE_SHADOW,
            R.integer.THEME_ORANGE_SHADOW,
            R.integer.THEME_PURPLE_SHADOW,
            R.integer.THEME_PINK_SHADOW,
            R.integer.THEME_GRAY_SHADOW,
            R.integer.THEME_GREEN_SHADOW,
            R.integer.THEME_LIGHT_SHADOW,
            R.integer.THEME_DARK_SHADOW,
    };

    public void loadConfigurationFromDIY(Context context){

        //从SharedPreference中获得DIY颜色值
        sp = PreferenceManager.getDefaultSharedPreferences(context);

        Log.i("颜色值",""+sp.getInt("THEME_TYPE", 0));
        skinData.skinId = sp.getInt("THEME_TYPE", 0);
        skinData.textcolors_functionKeys = sp.getInt("textcolors_functionKeys",0);
        skinData.textcolors_quickSymbol = sp.getInt("textcolors_quickSymbol",0);

        skinData.textcolors_delete = sp.getInt("textcolors_26keys",0);
        skinData.textcolors_t9keys = sp.getInt("textcolors_t9keys",0);
        skinData.textcolors_26keys = sp.getInt("textcolors_26keys",0);
        skinData.textcolors_candidate_qk = sp.getInt("textcolors_candidate",0);

        skinData.textcolors_zero = sp.getInt("textcolors_bottom",0);
        skinData.textcolors_space = sp.getInt("textcolors_bottom",0);
        skinData.textcolors_enter = sp.getInt("textcolors_bottom",0);

        skinData.textcolors_candidate_t9 = sp.getInt("textcolors_candidate",0);//中文九键第二栏候选词中文字体颜色
        skinData.textcolors_preEdit = sp.getInt("textcolors_preEdit",0);//中文输入最上方的拼音栏
        skinData.textcolors_shift = sp.getInt("textcolors_26keys",0);


        skinData.backcolor_functionKeys = sp.getInt("backcolor_functionKeys",0);
        skinData.backcolor_candidate_qk =sp.getInt("backcolor_candidate",0);
        skinData.backcolor_quickSymbol = sp.getInt("backcolor_quickSymbol",0);

        skinData.backcolor_delete = sp.getInt("backcolor_26keys",0);
        skinData.backcolor_t9keys = sp.getInt("backcolor_t9keys",0);
        skinData.backcolor_26keys = sp.getInt("backcolor_26keys",0);

        skinData.backcolor_zero = sp.getInt("backcolor_bottom",0);
        skinData.backcolor_space = sp.getInt("backcolor_bottom",0);
        skinData.backcolor_enter = sp.getInt("backcolor_bottom",0);

        skinData.backcolor_candidate_t9 = sp.getInt("backcolor_candidate",0);//中文九键的候选词背景色
        skinData.backcolor_shift = sp.getInt("backcolor_26keys",0);
        skinData.backcolor_smile = sp.getInt("backcolor_26keys",0);

        skinData.backcolor_prefix =sp.getInt("backcolor_touchdown",0);//长按Shift键之后的颜色
        skinData.backcolor_touchdown = sp.getInt("backcolor_touchdown",0);
        skinData.backcolor_preEdit = sp.getInt("backcolor_preEdit",0);

        skinData.backcolor_editText = sp.getInt("backcolor_26keys",0);//不知道这是干什么的,暂时与全键的颜色一致
        skinData.shadow = sp.getInt("shadow",0);

    }
    public void loadConfigurationFromXML(int skinId, Resources res) {
        int[] textColors;
        int[] backgroundColors;
        int shadowColor;
        try {
            textColors = res.getIntArray(textThemes[skinId]);
            backgroundColors = res.getIntArray(backThemes[skinId]);
            shadowColor = res.getInteger(shadowThemes[skinId]);
        } catch (Exception e) {
            textColors = res.getIntArray(R.array.THEME_CLASSIC_TEXT_COLORS);
            backgroundColors = res.getIntArray(R.array.THEME_CLASSIC_BUTTON_BACK_COLORS);
            shadowColor = res.getInteger(R.integer.THEME_CLASSIC_SHADOW);
        }
        if (skinData == null) {
            Log.d("WIVE", "skindata is null");
            skinData = new SkinInfoDataStruct();
        }
        skinData.skinId = skinId;
        int i = 0;
        skinData.textcolors_functionKeys = textColors[i++];
        skinData.textcolors_candidate_qk = textColors[i++];
        skinData.textcolors_quickSymbol = textColors[i++];

        skinData.textcolors_delete = textColors[i++];
        skinData.textcolors_t9keys = textColors[i++];
        skinData.textcolors_26keys = textColors[i++];

        skinData.textcolors_zero = textColors[i++];
        skinData.textcolors_space = textColors[i++];
        skinData.textcolors_enter = textColors[i++];

        skinData.textcolors_candidate_t9 = textColors[i++];
        skinData.textcolors_preEdit = textColors[i++];
        skinData.textcolors_shift = textColors[i++];

        i = 0;
        skinData.backcolor_functionKeys = backgroundColors[i++];
        skinData.backcolor_candidate_qk = backgroundColors[i++];
        skinData.backcolor_quickSymbol = backgroundColors[i++];

        skinData.backcolor_delete = backgroundColors[i++];
        skinData.backcolor_t9keys = backgroundColors[i++];
        skinData.backcolor_26keys = backgroundColors[i++];

        skinData.backcolor_zero = backgroundColors[i++];
        skinData.backcolor_space = backgroundColors[i++];
        skinData.backcolor_enter = backgroundColors[i++];

        skinData.backcolor_candidate_t9 = backgroundColors[i++];
        skinData.backcolor_shift = backgroundColors[i++];
        skinData.backcolor_smile = backgroundColors[i++];

        skinData.backcolor_prefix = backgroundColors[i++];
        skinData.backcolor_touchdown = backgroundColors[i++];
        skinData.backcolor_preEdit = backgroundColors[i++];

        skinData.shadow = shadowColor;
    }

    public void writeConfigurationToFile(String skinName) throws IOException {
        skinName += filenameSuffix;
        convertTheSkinDataToDataList();
        fileManager.writeToFile(skinName, dataList);
    }

    private void convertTheDataListToTheSkinData() {
        try {
            int i = 0;
            skinData.backcolor_26keys = dataList.get(i++);
            skinData.backcolor_candidate_qk = dataList.get(i++);
            skinData.backcolor_candidate_t9 = dataList.get(i++);
            skinData.backcolor_delete = dataList.get(i++);
            skinData.backcolor_editText = dataList.get(i++);
            skinData.backcolor_enter = dataList.get(i++);
            skinData.backcolor_functionKeys = dataList.get(i++);
            skinData.backcolor_quickSymbol = dataList.get(i++);
            skinData.backcolor_preEdit = dataList.get(i++);
            skinData.backcolor_shift = dataList.get(i++);
            skinData.backcolor_smile = dataList.get(i++);
            skinData.backcolor_space = dataList.get(i++);
            skinData.backcolor_t9keys = dataList.get(i++);
            skinData.backcolor_touchdown = dataList.get(i++);
            skinData.backcolor_zero = dataList.get(i++);

            skinData.textcolors_26keys = dataList.get(i++);
            skinData.textcolors_candidate_qk = dataList.get(i++);
            skinData.textcolors_candidate_t9 = dataList.get(i++);
            skinData.textcolors_delete = dataList.get(i++);
            skinData.textcolors_enter = dataList.get(i++);
            skinData.textcolors_functionKeys = dataList.get(i++);
            skinData.textcolors_preEdit = dataList.get(i++);
            skinData.textcolors_quickSymbol = dataList.get(i++);
            skinData.textcolors_shift = dataList.get(i++);
            skinData.textcolors_space = dataList.get(i++);
            skinData.textcolors_t9keys = dataList.get(i++);
            skinData.textcolors_zero = dataList.get(i++);

            skinData.shadow = dataList.get(i++);

        } catch (Exception e) {
            Log.d("WIVE", "skindata get wrong:" + e.toString());
        }
    }

    private List<Integer> convertTheSkinDataToDataList() {
        dataList.clear();
        dataList.add(skinData.backcolor_26keys);
        dataList.add(skinData.backcolor_candidate_qk);
        dataList.add(skinData.backcolor_candidate_t9);
        dataList.add(skinData.backcolor_delete);
        dataList.add(skinData.backcolor_editText);
        dataList.add(skinData.backcolor_enter);
        dataList.add(skinData.backcolor_functionKeys);
        dataList.add(skinData.backcolor_quickSymbol);
        dataList.add(skinData.backcolor_preEdit);
        dataList.add(skinData.backcolor_shift);
        dataList.add(skinData.backcolor_smile);
        dataList.add(skinData.backcolor_space);
        dataList.add(skinData.backcolor_t9keys);
        dataList.add(skinData.backcolor_touchdown);
        dataList.add(skinData.backcolor_zero);

        dataList.add(skinData.textcolors_26keys);
        dataList.add(skinData.textcolors_candidate_qk);
        dataList.add(skinData.textcolors_candidate_t9);
        dataList.add(skinData.textcolors_delete);
        dataList.add(skinData.textcolors_enter);
        dataList.add(skinData.textcolors_functionKeys);
        dataList.add(skinData.textcolors_preEdit);
        dataList.add(skinData.textcolors_quickSymbol);
        dataList.add(skinData.textcolors_shift);
        dataList.add(skinData.textcolors_space);
        dataList.add(skinData.textcolors_t9keys);
        dataList.add(skinData.textcolors_zero);

        dataList.add(skinData.shadow);
        return dataList;
    }

}
