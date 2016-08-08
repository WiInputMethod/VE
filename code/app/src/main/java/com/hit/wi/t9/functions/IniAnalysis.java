package com.hit.wi.t9.functions;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2015/10/28.
 */
public class IniAnalysis {
    private String path = "symbols";
    private Context context;


    public IniAnalysis(Context context){
        this.context = context;
    }

    public List<String> splitIni(List<String> rawlist) {
        List<String> values = new ArrayList<>();
        for (String value:rawlist) {
            String[] tmpStrings = value.split("\n");
            for (String str :tmpStrings){
                values.add(str);
            }
        }
        return values;
    }

    public List<String> getValuesFromFile(String path) throws IOException {
        InputStream inputStream = context.getAssets().open(path);
        List<String> values = new ArrayList<>();
        int count = inputStream.available();
        byte[] b = new byte[count];
        while (inputStream.read(b)!=-1){
            values.add(new String(b));
        }
        values.add(new String(b));
        values = splitIni(values);
        inputStream.close();
        return values;
    }
}
