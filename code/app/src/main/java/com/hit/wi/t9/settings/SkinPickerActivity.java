package com.hit.wi.t9.settings;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.hit.wi.util.CommonFuncs;

import com.hit.wi.t9.R;
import com.umeng.analytics.MobclickAgent;


/**
 * 选择皮肤
 * @author dagger
 */
public class SkinPickerActivity extends Activity {

    private static int REQUEST_PICK_DIY = 0x02;
    private static int REQUEST_PICK_OTHER = 0x01;

    private GridView gridView;
    private Context context;
    private SharedPreferences sp;
    private int lastItem = 0;
    private SimpleAdapter adapter;
    final String[] themelist = {
            "WinPhone",
            "经典",
            "冰雪蓝",
            "热力橙",
            "魅惑紫",
            "萌萌粉",
            "格调灰",
            "典雅绿",
            "Light",
            "Dark",
            "自定义皮肤"
    };

    final int[] themepic = {
            R.drawable.theme_wp,
            R.drawable.theme_default,
            R.drawable.theme_iceblue,
            R.drawable.theme_orange,
            R.drawable.theme_purple,
            R.drawable.theme_pink,
            R.drawable.theme_gray,
            R.drawable.theme_green,
            R.drawable.theme_light,
            R.drawable.theme_dark,
            R.drawable.theme_diy
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.skin_picker);
        this.context = this;
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        lastItem = sp.getInt("THEME_TYPE", 0);
        gridView = (GridView) this.findViewById(R.id.gv_image);

        ArrayList<HashMap<String, Object>> imagelist = new ArrayList<HashMap<String, Object>>();
        /*
		 * dagger:升级皮肤要修改这里
		 */

        for (int i = 0; i < themelist.length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("Image", themepic[i]);
            if (lastItem == i) {
                map.put("Layer", R.drawable.theme_picked);
            } else {
                map.put("Layer", R.drawable.theme_unpick);
            }
            map.put("Text", themelist[i]);
            imagelist.add(map);
        }


        adapter = new SimpleAdapter(this,
                imagelist,
                R.layout.skin_grid_item,
                new String[]{"Image", "Layer", "Text"},
                new int[]{R.id.imageView, R.id.picklayer, R.id.tv_item});
        gridView.setAdapter(adapter);

        //处理事件
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position==adapter.getCount()-1){
                    Intent intent=new Intent(SkinPickerActivity.this,SkinDiyActivity.class);
                    intent.putExtra("position",position);
                    startActivity(intent);

                }else{

                    CommonFuncs.showToast(context, "您选择了主题:" + themelist[position]);

                    if (position != lastItem) {//发生了变化
                        HashMap<String, Object> map = (HashMap<String, Object>) adapter.getItem(lastItem);
                        map.put("Layer", R.drawable.theme_unpick);
                        adapter.notifyDataSetChanged();
                        lastItem = position;
                        map = (HashMap<String, Object>) adapter.getItem(lastItem);
                        map.put("Layer", R.drawable.theme_picked);
                        adapter.notifyDataSetChanged();
                    }

                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("THEME_TYPE", position);
                    editor.putBoolean("THEME_CHANGED", true);
                    editor.putBoolean("THEME_DEF_ON", false);
                    editor.putBoolean("IS_DIY",false);

                    editor.commit();

                }

            }
        });

    }

    @Override
    protected void onResume() {

        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        MobclickAgent.onPause(this);
        super.onPause();
    }
}