package com.hit.wi.t9.functions;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by zrp on 2016/2/26.
 */
public class PredictManager {
    public String[] qkPredict;
    private final String alphabet = "QWERTYUIOPASDFGHJKLZXCVBNM";

    public PredictManager(){}

    public void init(Context context){
        refresh(context);
    }

    public void refresh(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        qkPredict = new String[alphabet.length()];
        for (int i = 0; i < alphabet.length(); i++) {
            qkPredict[i] = sp.getString("SLIDE_PIN_" + alphabet.substring(i,i+1), "");
        }
    }

}
