package com.hit.wi.t9.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hit.wi.t9.R;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

/**
 * 自定义皮肤
 */
public class SkinDiyActivity extends Activity {

    private Button bt11;
    private Button bt12;
    private Button bt21;
    private Button bt22;
    private Button bt31;
    private Button bt32;
    private Button bt41;
    private Button bt42;
    private Button bt51;
    private Button bt52;
    private Button bt61;
    private Button bt62;
    private Button bt71;
    private Button bt72;
    private Button bt8;
    private Button bt_sure;


    //如果用这些变量，以后修改自定义皮肤只需要修改SkinDefineActivity里边的内容
    public int textcolors_functionKeys;//最上排五个功能键：移动、缩放、设置、隐藏、退出
    public int textcolors_candidate_qk;//第二行candidate候选拼音
    public int textcolors_quickSymbol;//第三排左边五个字符选择键
    public int textcolors_delete;//第三排删除键
    public int textcolors_t9keys;//九宫格九个按键
    public int textcolors_26keys;//二十六个英文字母按键
    public int textcolors_zero;//数字零按键
    public int textcolors_space;//空格键8
    public int textcolors_enter;//换行键9
    public int textcolors_candidate_t9;//左侧候选词
    public int textcolors_preEdit;//编辑框11
    public int textcolors_shift; //Shift键12

    public int backcolor_functionKeys;//
    public int backcolor_candidate_qk;
    public int backcolor_quickSymbol;
    public int backcolor_delete;
    public int backcolor_t9keys;
    public int backcolor_26keys;
    public int backcolor_zero;
    public int backcolor_space;
    public int backcolor_enter;
    public int backcolor_candidate_t9;
    public int backcolor_preEdit;
    public int backcolor_shift;
    public int backcolor_smile;
    public int backcolor_prefix;
    public int backcolor_editText;//i dont know what it is

    public int backcolor_touchdown;
    public int shadow;

    public String skinName;


    private SharedPreferences sp;
    private static int[] colors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin_diy);

        init();

        bt_sure=(Button)findViewById(R.id.bt_color_picker_sure);
        bt_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeToFile();
            }
        });


    }

    public void choosColor(final View v){

        AlertDialog.Builder builder =new AlertDialog.Builder(SkinDiyActivity.this);
        LayoutInflater inflater=this.getLayoutInflater();
        View view =inflater.inflate(R.layout.color_picker_dialog,null);
            builder.setView(view);
        builder.setTitle("自定义皮肤");


        final ColorPicker picker = (ColorPicker)view.findViewById(R.id.color_picker);
        SVBar svBar = (SVBar)view.findViewById(R.id.color_picker_svbar);
        OpacityBar opacityBar = (OpacityBar)view.findViewById(R.id.color_picker_opacitybar);
        SaturationBar saturationBar = (SaturationBar)view.findViewById(R.id.color_picker_saturationbar);
        ValueBar valueBar = (ValueBar)view.findViewById(R.id.color_picker_valuebar);

        picker.addSVBar(svBar);
        picker.addOpacityBar(opacityBar);
        picker.addSaturationBar(saturationBar);
        picker.addValueBar(valueBar);
        picker.setOldCenterColor(v.getDrawingCacheBackgroundColor());

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //
                //v.setBackgroundColor(picker.getColor());
                v.setBackgroundColor(picker.getColor());
                switch (v.getId()){
                    case R.id.bt_color_picker_11:colors[0]=picker.getColor();

                        Log.i("Test","bt_color_picker_11");
                        break;
                    case R.id.bt_color_picker_12:colors[1]=picker.getColor();
                        Log.i("Test","bt_color_picker_11");
                        break;
                    case R.id.bt_color_picker_21:colors[2]=picker.getColor();
                        break;
                    case R.id.bt_color_picker_22:colors[3]=picker.getColor();
                        break;
                    case R.id.bt_color_picker_31:colors[4]=picker.getColor();
                        break;
                    case R.id.bt_color_picker_32:colors[5]=picker.getColor();
                        break;
                    case R.id.bt_color_picker_41:colors[6]=picker.getColor();
                        break;
                    case R.id.bt_color_picker_42:colors[7]=picker.getColor();
                        break;
                    case R.id.bt_color_picker_51:colors[8]=picker.getColor();
                        break;
                    case R.id.bt_color_picker_52:colors[9]=picker.getColor();
                        break;
                    case R.id.bt_color_picker_61:colors[10]=picker.getColor();
                        break;
                    case R.id.bt_color_picker_62:colors[11]=picker.getColor();
                        break;
                    case R.id.bt_color_picker_71:colors[12]=picker.getColor();
                        break;
                    case R.id.bt_color_picker_72:colors[13]=picker.getColor();
                        break;
                    case R.id.bt_color_picker_8:colors[14]=picker.getColor();
                        break;
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //

            }
        });
        builder.show();
    }

    public void init(){


        colors=new int[15];

        //选择颜色的按钮
        bt11=(Button)findViewById(R.id.bt_color_picker_11);
        bt12=(Button)findViewById(R.id.bt_color_picker_12);
        bt21=(Button)findViewById(R.id.bt_color_picker_21);
        bt22=(Button)findViewById(R.id.bt_color_picker_22);
        bt31=(Button)findViewById(R.id.bt_color_picker_31);
        bt32=(Button)findViewById(R.id.bt_color_picker_32);
        bt41=(Button)findViewById(R.id.bt_color_picker_41);
        bt42=(Button)findViewById(R.id.bt_color_picker_42);
        bt51=(Button)findViewById(R.id.bt_color_picker_51);
        bt52=(Button)findViewById(R.id.bt_color_picker_52);
        bt61=(Button)findViewById(R.id.bt_color_picker_61);
        bt62=(Button)findViewById(R.id.bt_color_picker_62);
        bt71=(Button)findViewById(R.id.bt_color_picker_71);
        bt72=(Button)findViewById(R.id.bt_color_picker_72);
        bt8=(Button)findViewById(R.id.bt_color_picker_8);

        //初始化Colors数组
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        colors[0]=sp.getInt("backcolor_functionKeys", 0xFF050505);
        colors[1]=sp.getInt("textcolors_functionKeys", 0xFFe8853b);
        colors[2]=sp.getInt("backcolor_delete", 0xFF0a0a0a);
        colors[3]=sp.getInt("textcolors_delete", 0xFF0a0a0a);
        colors[4]=sp.getInt("backcolor_quickSymbol",0xFF0a0a0a);
        colors[5]=sp.getInt("textcolors_quickSymbol",0xFF0a0a0a);
        colors[6]=sp.getInt("backcolor_candidate_t9",0xFF1a1a1a);
        colors[7]=sp.getInt("textcolors_candidate_t9",0xFFe8853b);
        colors[8]=sp.getInt("backcolor_preEdit",0xFF1a1a1a);
        colors[9]=sp.getInt("textcolors_preEdit",0xFFe8853b);
        colors[10]=sp.getInt("backcolor_t9keys",0xFF555555);
        colors[11]=sp.getInt("textcolors_t9keys",0xFF555555);
        colors[12]=sp.getInt("backcolor_26keys",0xFF555555);
        colors[13]=sp.getInt("textcolors_26keys",0xFF555555);
        colors[14]=sp.getInt("backcolor_touchdown",0xFF555555);


//初始化每个颜色值
        //上侧功能栏
        backcolor_functionKeys=colors[0];
        textcolors_functionKeys=colors[1];

        //底部操作栏
        backcolor_delete=colors[2];
        textcolors_delete=colors[3];

        backcolor_space=colors[2];
        textcolors_space=colors[3];

        backcolor_enter=colors[2];
        textcolors_enter=colors[3];

        backcolor_shift=colors[2];
        textcolors_shift=colors[3];

        backcolor_smile=colors[2];

        //符号栏
        backcolor_quickSymbol=colors[4];
        textcolors_quickSymbol=colors[5];
        backcolor_prefix=colors[4];//复制、删除、剪切、粘贴的功能键的背景颜色


        //候选框
        textcolors_candidate_t9=colors[7];//候选框字体颜色
        backcolor_candidate_t9=colors[6];//候选框背景颜色

        textcolors_candidate_qk=colors[7];//符号候选框字体颜色
        backcolor_candidate_qk=colors[6];//符号候选框背景颜色

        //编辑框
        textcolors_preEdit=colors[9];//编辑框字体颜色
        backcolor_preEdit=colors[8];//编辑框背景颜色

        //九键
        backcolor_t9keys=colors[10];
        textcolors_t9keys=colors[11];
        backcolor_zero=colors[10];
        textcolors_zero=colors[11];
        //全键
        backcolor_26keys=colors[12];
        textcolors_26keys=colors[13];
        //按下背景色
        backcolor_touchdown=colors[14];



        backcolor_editText=Color.RED;
        shadow=Color.YELLOW;



        //初始化Button背景
        bt11.setBackgroundColor(colors[0]);
        bt12.setBackgroundColor(colors[1]);
        bt21.setBackgroundColor(colors[2]);
        bt22.setBackgroundColor(colors[3]);
        bt31.setBackgroundColor(colors[4]);
        bt32.setBackgroundColor(colors[5]);
        bt41.setBackgroundColor(colors[6]);
        bt42.setBackgroundColor(colors[7]);
        bt51.setBackgroundColor(colors[8]);
        bt52.setBackgroundColor(colors[9]);
        bt61.setBackgroundColor(colors[10]);
        bt62.setBackgroundColor(colors[11]);
        bt71.setBackgroundColor(colors[12]);
        bt72.setBackgroundColor(colors[13]);
        bt8.setBackgroundColor(colors[14]);


        View.OnClickListener listener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosColor(v);
            }
        };

        bt11.setOnClickListener(listener);
        bt12.setOnClickListener(listener);
        bt21.setOnClickListener(listener);
        bt22.setOnClickListener(listener);
        bt31.setOnClickListener(listener);
        bt32.setOnClickListener(listener);
        bt41.setOnClickListener(listener);
        bt42.setOnClickListener(listener);
        bt51.setOnClickListener(listener);
        bt52.setOnClickListener(listener);
        bt61.setOnClickListener(listener);
        bt62.setOnClickListener(listener);
        bt71.setOnClickListener(listener);
        bt72.setOnClickListener(listener);
        bt8.setOnClickListener(listener);

    }

    public void writeToFile(){

        //上侧功能栏
        backcolor_functionKeys=colors[0];
        textcolors_functionKeys=colors[1];

        //底部操作栏
        backcolor_delete=colors[2];
        textcolors_delete=colors[3];

        backcolor_space=colors[2];
        textcolors_space=colors[3];

        backcolor_enter=colors[2];
        textcolors_enter=colors[3];

        backcolor_shift=colors[2];
        textcolors_shift=colors[3];

        backcolor_smile=colors[2];

        //符号栏
        backcolor_quickSymbol=colors[4];
        textcolors_quickSymbol=colors[5];
        backcolor_prefix=colors[4];//复制、删除、剪切、粘贴的功能键的背景颜色


        //候选框
        textcolors_candidate_t9=colors[7];//候选框字体颜色
        backcolor_candidate_t9=colors[6];//候选框背景颜色

        textcolors_candidate_qk=colors[7];//符号候选框字体颜色
        backcolor_candidate_qk=colors[6];//符号候选框背景颜色

        //编辑框
        textcolors_preEdit=colors[9];//编辑框字体颜色
        backcolor_preEdit=colors[8];//编辑框背景颜色

        //九键
        backcolor_t9keys=colors[10];
        textcolors_t9keys=colors[11];
        backcolor_zero=colors[10];
        textcolors_zero=colors[11];
        //全键
        backcolor_26keys=colors[12];
        textcolors_26keys=colors[13];
        //按下背景色
        backcolor_touchdown=colors[14];



        backcolor_editText=Color.RED;
        shadow=Color.YELLOW;


        sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("textcolors_functionKeys",textcolors_functionKeys);
        editor.putInt("textcolors_candidate_qk",textcolors_candidate_qk);
        editor.putInt("textcolors_quickSymbol",textcolors_quickSymbol);
        editor.putInt("textcolors_delete",textcolors_delete);

        editor.putInt("textcolors_t9keys",textcolors_t9keys);
        editor.putInt("textcolors_26keys",textcolors_26keys);
        editor.putInt("textcolors_zero",textcolors_zero);
        editor.putInt("textcolors_space",textcolors_space);

        editor.putInt("textcolors_enter",textcolors_enter);
        editor.putInt("textcolors_candidate_t9",textcolors_candidate_t9);
        editor.putInt("textcolors_preEdit",textcolors_preEdit);
        editor.putInt("textcolors_shift",textcolors_shift);


        editor.putInt("backcolor_functionKeys",backcolor_functionKeys);
        editor.putInt("backcolor_candidate_qk",backcolor_candidate_qk);
        editor.putInt("backcolor_quickSymbol",backcolor_quickSymbol);
        editor.putInt("backcolor_delete",backcolor_delete);

        editor.putInt("backcolor_t9keys",backcolor_t9keys);
        editor.putInt("backcolor_26keys",backcolor_26keys);
        editor.putInt("backcolor_zero",backcolor_zero);
        editor.putInt("backcolor_space",backcolor_space);

        editor.putInt("backcolor_enter",backcolor_enter);
        editor.putInt("backcolor_candidate_t9",backcolor_candidate_t9);
        editor.putInt("backcolor_preEdit",backcolor_preEdit);
        editor.putInt("backcolor_shift",backcolor_shift);

        editor.putInt("backcolor_smile",backcolor_smile);
        editor.putInt("backcolor_prefix",backcolor_prefix);
        editor.putInt("backcolor_touchdown",backcolor_touchdown);
        editor.putInt("backcolor_editText", backcolor_editText);

        editor.putInt("shadow", shadow);

        Intent intent=this.getIntent();
        int position=intent.getIntExtra("position",0);

        editor.putInt("THEME_TYPE",position);
        editor.putBoolean("IS_DIY",true);
        editor.putBoolean("THEME_CHANGED", true);
        editor.putBoolean("THEME_DEF_ON", false);
        editor.commit();
    }

}