package com.hit.wi.ve.settings;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.hit.wi.ve.R;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

import java.util.ArrayList;
import java.util.List;


public class T9Fragment extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LinearLayout.LayoutParams layoutParamsFunc;
    private LinearLayout.LayoutParams layoutParamsSymbol;
    private LinearLayout.LayoutParams layoutParamsKeys;
    private LinearLayout.LayoutParams layoutParamsBottom;
    private LinearLayout.LayoutParams keyParams;
    private Display display;
    private Resources res;
    private int fragmentWidth;
    private int fragmentHeight;
    private int keyMargin;
    private Typeface mTypeface;
    //functions
    private LinearLayout t9_func_layout;
    private List<TextView> t9FuncKeyList=new ArrayList<TextView>();
    private String[] funcKeyText;
    private String[] symbolKeyText={"符",",","。","!","?"};
    private String[] keysText;
    private String[] bottomKeyText;
    //symbols
    private LinearLayout t9_symbol_layout;
    private List<TextView> t9SymbolKeyList=new ArrayList<TextView>();
    //keys
    private LinearLayout[] t9Linears=new LinearLayout[3];
    private LinearLayout t9_keys_layout;
    private List<TextView> t9KeysList=new ArrayList<TextView>();
    //bottom
    private LinearLayout t9_bottom_layout;
    private List<TextView> t9BottomKeyList=new ArrayList<TextView>();

    private int textColorFunc;
    private int backgroundColorFunc;

    private int textColorSymbol;
    private int backgroundColorSymbol;

    private int textColorT9;
    private int backgroundColorT9;

    private int textColorBottom;
    private int backgroundColorBottom;


    private int[] selectedQkColors={0,0,0,0,0,0,0,0};
    private int[] oldT9Colors=new int[2];

    public enum viewType{FUNC,SYMBOL,T9,BOTTOM}

    private viewType myViewType=viewType.FUNC;

    public T9Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragment_t9, container, false);
        display=getActivity().getWindowManager().getDefaultDisplay();
        fragmentWidth=display.getWidth();
        fragmentHeight=display.getHeight()/2;
        res=getResources();
        mTypeface = Typeface.createFromAsset(getActivity().getAssets(), res.getString(R.string.font_file_path));// 加载自定义字体

        t9_func_layout=(LinearLayout)v.findViewById(R.id.t9_function_layout);
        t9_symbol_layout=(LinearLayout)v.findViewById(R.id.t9_symbol_layout);
        t9_keys_layout=(LinearLayout)v.findViewById(R.id.t9_keys_layout);
        t9_bottom_layout=(LinearLayout)v.findViewById(R.id.t9_bottom_layout);

        layoutParamsFunc=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,2);
        layoutParamsSymbol=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,2);
        layoutParamsKeys=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,6);
        layoutParamsBottom=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,2);
        keyParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,1.0f);

        int rightMargin=fragmentWidth*2/100;
        layoutParamsFunc.rightMargin=rightMargin;
        layoutParamsSymbol.rightMargin=rightMargin;
        layoutParamsKeys.rightMargin=rightMargin;
        layoutParamsBottom.rightMargin=rightMargin;

        keyParams.leftMargin=fragmentWidth*2/100;
        keyParams.topMargin=fragmentHeight/64;
        keyParams.bottomMargin=fragmentHeight/64;


        initView(v);
        return v;
    }

    public void initView(View v){

        selectedQkColors=((SkinDiyActivity)getActivity()).getSelectedColorsQk();
        backgroundColorFunc=selectedQkColors[0];
        textColorFunc=selectedQkColors[1];
        backgroundColorSymbol=selectedQkColors[2];
        textColorSymbol=selectedQkColors[3];
        textColorBottom=selectedQkColors[7];
        backgroundColorBottom=selectedQkColors[6];

        oldT9Colors=((SkinDiyActivity)getActivity()).getOldT9Colors();

        backgroundColorT9=oldT9Colors[0];
        textColorT9=oldT9Colors[1];
        initFuncView(v);
        initSymbolView(v);
        initKeysView(v);
        initBottomView(v);
    }
    private void initFuncView(View v){

        funcKeyText=res.getStringArray(R.array.FUNC_KEY_TEXT);
        t9_func_layout.setLayoutParams(layoutParamsFunc);
        for(int i=0;i<5;i++){
            TextView textView=new TextView(v.getContext());
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(keyParams);
            textView.setText(funcKeyText[i]);
            textView.setTextColor(textColorFunc);
            textView.setTypeface(mTypeface);

            textView.setOnClickListener(listener);
            t9FuncKeyList.add(textView);
            t9_func_layout.addView(textView);
        }
    }

    private void initSymbolView(View v){
        t9_symbol_layout.setLayoutParams(layoutParamsSymbol);
        for(int i=0;i<5;i++){
            TextView textView=new TextView(v.getContext());
            textView.setText(symbolKeyText[i]);
            textView.setTypeface(mTypeface);
            textView.setBackgroundColor(backgroundColorSymbol);
            textView.setTextColor(textColorSymbol);
            textView.setGravity(Gravity.CENTER);
            textView.setOnClickListener(listener);
            textView.setLayoutParams(keyParams);
            t9_symbol_layout.addView(textView);
            t9SymbolKeyList.add(textView);
        }
    }

    private void initKeysView(View v) {

        keysText = res.getStringArray(R.array.KEY_TEXT);
        t9_keys_layout.setLayoutParams(layoutParamsKeys);
        int count = 0;
        for (int i = 0; i < 3; i++) {
            t9Linears[i] = new LinearLayout(v.getContext());
            t9Linears[i].setOrientation(LinearLayout.HORIZONTAL);
            t9Linears[i].setGravity(Gravity.CENTER_HORIZONTAL);
            t9Linears[i].setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
            for (int j = 0; j < 3; j++) {
                TextView textView = new TextView(v.getContext());
                textView.setText(keysText[count]);
                textView.setBackgroundColor(backgroundColorT9);
                textView.setTextColor(textColorT9);
                textView.setGravity(Gravity.CENTER);
                textView.setLayoutParams(keyParams);
                textView.setOnClickListener(t9Listener);
                t9KeysList.add(textView);
                t9Linears[i].addView(textView);
                count++;
            }
            t9_keys_layout.addView(t9Linears[i]);
        }

    }

    private void initBottomView(View v){

        bottomKeyText=res.getStringArray(R.array.BOTTOMBAR_TEXT);
        t9_bottom_layout.setLayoutParams(layoutParamsBottom);

        for(int i=0;i<4;i++){
            TextView textView=new TextView(v.getContext());
            textView.setText(bottomKeyText[i]);
            textView.setBackgroundColor(backgroundColorBottom);
            textView.setTextColor(textColorBottom);
            textView.setTypeface(mTypeface);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(keyParams);
            textView.setOnClickListener(listener);
            t9_bottom_layout.addView(textView);
            t9BottomKeyList.add(textView);
        }
    }

    private View.OnClickListener t9Listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            chooseColor(v);
        }
    };
    private View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            refreshView();
        }
    };
    public void chooseColor(final View v){

        selectedQkColors=getSelectedColorsQk();
        myViewType=getCurrentViewType((TextView)v);
        AlertDialog.Builder builder =new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view =inflater.inflate(R.layout.color_picker_dialog,null);
        builder.setView(view);
        builder.setTitle("自定义皮肤");


        final ColorPicker picker = (ColorPicker)view.findViewById(R.id.color_picker);
        SVBar svBar = (SVBar)view.findViewById(R.id.color_picker_svbar);
        OpacityBar opacityBar = (OpacityBar)view.findViewById(R.id.color_picker_opacitybar);

        final RadioButton rbPickAsQk=(RadioButton)view.findViewById(R.id.rb_pick_asqk);
        final RadioButton rbIsText=(RadioButton)view.findViewById(R.id.rb_picker_text);
        final RadioButton rbIsBackground=(RadioButton)view.findViewById(R.id.rb_picker_background);

        picker.addSVBar(svBar);
        picker.addOpacityBar(opacityBar);
        picker.setOldCenterColor(v.getDrawingCacheBackgroundColor());

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (myViewType) {
                    case FUNC:

                        if (rbIsBackground.isChecked()) {
                            if (rbPickAsQk.isChecked())
                                backgroundColorFunc=selectedQkColors[0];
                            else
                                backgroundColorFunc = picker.getColor();

                            refreshFuncBackground();
                        }
                        if (rbIsText.isChecked()) {
                            if(rbPickAsQk.isChecked())
                                textColorFunc=selectedQkColors[1];
                            else
                                textColorFunc = picker.getColor();
                            refreshFuncText();
                        }

                        break;
                    case SYMBOL:
                        if (rbIsBackground.isChecked()) {
                            if(rbPickAsQk.isChecked())
                                backgroundColorSymbol=selectedQkColors[2];
                            else
                                backgroundColorSymbol = picker.getColor();
                            refreshSymbolBackground();
                        }
                        if (rbIsText.isChecked()) {

                            if(rbPickAsQk.isChecked())
                                textColorSymbol=selectedQkColors[3];
                            else
                                textColorSymbol = picker.getColor();
                            refreshSymbolText();
                        }
                        break;
                    case T9:
                        if (rbIsBackground.isChecked()) {
                            if(rbPickAsQk.isChecked())
                                backgroundColorT9=selectedQkColors[4];
                            else
                                backgroundColorT9 = picker.getColor();
                            refreshT9Background();
                        }
                        if (rbIsText.isChecked()) {
                            if(rbPickAsQk.isChecked())
                                textColorT9=selectedQkColors[5];
                            else
                                textColorT9 = picker.getColor();
                            refreshT9Text();
                        }
                        break;
                    case BOTTOM:
                        if (rbIsBackground.isChecked()) {
                            if(rbPickAsQk.isChecked())
                                backgroundColorBottom=selectedQkColors[6];
                            else
                                backgroundColorBottom = picker.getColor();
                            refreshBottomBackground();
                        }
                        if (rbIsText.isChecked()) {
                            if(rbPickAsQk.isChecked())
                                textColorBottom=selectedQkColors[7];
                            else
                                textColorBottom = picker.getColor();
                            refreshBottomText();
                        }
                        break;
                }

                ((SkinDiyActivity)getActivity()).setSelectedColorsT9(getCurrentColors());
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

    public void refreshFuncBackground() {

        for(TextView textView:t9FuncKeyList){
            textView.setBackgroundColor(backgroundColorFunc);
        }
    }
    public void refreshSymbolBackground() {

        for(TextView textView:t9SymbolKeyList){
            textView.setBackgroundColor(backgroundColorSymbol);
        }
    }
    public void refreshT9Background() {

        for(TextView textView:t9KeysList){
            textView.setBackgroundColor(backgroundColorT9);
        }
    }
    public void refreshBottomBackground() {

        for(TextView textView:t9BottomKeyList){
            textView.setBackgroundColor(backgroundColorBottom);
        }
    }
    public void refreshFuncText(){
        for(TextView textView:t9FuncKeyList){
            textView.setTextColor(textColorFunc);
        }
    }
    public void refreshSymbolText(){
        for(TextView textView:t9SymbolKeyList){
            textView.setTextColor(textColorSymbol);
        }
    }
    public void refreshT9Text(){
        for(TextView textView:t9KeysList){
            textView.setTextColor(textColorT9);
        }
    }
    public void refreshBottomText(){
        for(TextView textView:t9BottomKeyList){
            textView.setTextColor(textColorBottom);
        }
    }

    public viewType getCurrentViewType(TextView v){

        for(TextView textView:t9FuncKeyList){
            if(textView.equals(v)){
                return viewType.FUNC;
            }
        }

        for(TextView textView:t9SymbolKeyList){
            if(textView.equals(v)){
                return viewType.SYMBOL;
            }
        }

        for(TextView textView:t9KeysList){
            if(textView.equals(v)){
                return viewType.T9;
            }
        }

        for(TextView textView:t9BottomKeyList){
            if(textView.equals(v)){
                return viewType.BOTTOM;
            }
        }

        return viewType.FUNC;
    }

    public int[] getSelectedColorsQk(){
        return ((SkinDiyActivity)getActivity()).getSelectedColorsQk();
    }
    public int[] getCurrentColors(){
        int [] colors={backgroundColorT9,textColorT9};
        return colors;
    }
    public void refreshView(){


        selectedQkColors=((SkinDiyActivity)getActivity()).getSelectedColorsQk();
        backgroundColorFunc=selectedQkColors[0];
        textColorFunc=selectedQkColors[1];
        backgroundColorSymbol=selectedQkColors[2];
        textColorSymbol=selectedQkColors[3];
        textColorBottom=selectedQkColors[7];
        backgroundColorBottom=selectedQkColors[6];

        refreshFuncBackground();
        refreshFuncText();
        refreshSymbolBackground();
        refreshSymbolText();
        refreshBottomBackground();
        refreshBottomText();
    }
}
