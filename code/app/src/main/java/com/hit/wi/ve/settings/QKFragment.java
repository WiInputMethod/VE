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

public class QKFragment extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Display display;
    private int enKeyWidth;

    /**
     * 字体
     */
    private Typeface mTypeface;
    private Resources res;
    private LinearLayout.LayoutParams layoutParamsFunc;
    private LinearLayout.LayoutParams layoutParamsEnkeys;
    private LinearLayout.LayoutParams layoutParamsSymbol;
    private LinearLayout.LayoutParams layoutParamsBottom;
    private LinearLayout.LayoutParams paramsEnkey;
    //functionView
    private LinearLayout function_layout;
    String[] funcKeyTexts;
    private List<TextView> functionKeyList = new ArrayList<TextView>();
    private int qkFragmentWidth;
    private int qkFragmentHeight;
    private LinearLayout.LayoutParams qkKeysLayoutParams;


    //symbols
    private List<TextView> symbolKeyList = new ArrayList<TextView>();
    private LinearLayout qk_symbol_layout;

    //enkeys
    private LinearLayout qk_enkeys_layout;
    private LinearLayout[] qkEnKeysLinears = new LinearLayout[3];
    private LinearLayout.LayoutParams[] qkLinearsParams = new LinearLayout.LayoutParams[3];
    private int[] qk_key_num_list = {10, 9, 7};
    //最后两个是Shift键和删除键
    private List<TextView> qkKeyList = new ArrayList<TextView>();
    private String[] qkKeyText;

    //bottomBar
    private List<TextView> qkBottomKeyList = new ArrayList<TextView>();
    private LinearLayout qk_bottom_layout;

    private int textColorFunc;
    private int backgroundColorFunc;

    private int textColorSymbol;
    private int backgroundColorSymbol;

    private int textColorQk;
    private int backgroundColorQk;

    private int textColorBottom;
    private int backgroundColorBottom;


    public enum viewType {FUNC, SYMBOL, QK, BOTTOM}

    private viewType myViewType = viewType.FUNC;

    private int[] selectedColors;

    public QKFragment() {
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
        // Inflate the layout for this fragment

        res = getResources();
        final View v = inflater.inflate(R.layout.fragment_qk, container, false);
        mTypeface = Typeface.createFromAsset(getActivity().getAssets(), res.getString(R.string.font_file_path));// 加载自定义字体

        layoutParamsFunc = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 2.0f);
        layoutParamsSymbol = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        layoutParamsEnkeys = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 6.0f);
        layoutParamsBottom = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);

        display = getActivity().getWindowManager().getDefaultDisplay();
        qkFragmentWidth = display.getWidth();
        qkFragmentHeight = display.getHeight() / 2;

        int rightMargin = qkFragmentWidth * 2 / 100;

        layoutParamsFunc.rightMargin = rightMargin;
        layoutParamsSymbol.rightMargin = rightMargin;
        layoutParamsEnkeys.rightMargin = rightMargin + rightMargin;
        layoutParamsBottom.rightMargin = rightMargin;

        layoutParamsEnkeys.leftMargin = rightMargin;


        qkKeysLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
        qkKeysLayoutParams.leftMargin = qkFragmentWidth * 2 / 100;
        qkKeysLayoutParams.topMargin = qkFragmentHeight / 64;
        qkKeysLayoutParams.bottomMargin = qkFragmentHeight / 64;
        enKeyWidth = qkFragmentWidth * 37 / 500;


        paramsEnkey = new LinearLayout.LayoutParams(enKeyWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        paramsEnkey.leftMargin = qkFragmentWidth * 2 / 100;
        paramsEnkey.topMargin = qkFragmentHeight / 64;
        paramsEnkey.bottomMargin = qkFragmentHeight / 64;
        initView(v);
        return v;
    }

    public void initView(View v) {

        selectedColors = ((SkinDiyActivity) getActivity()).getOldQkColors();
        textColorFunc = selectedColors[1];
        backgroundColorFunc = selectedColors[0];

        textColorSymbol = selectedColors[3];
        backgroundColorSymbol = selectedColors[2];

        textColorQk = selectedColors[5];
        backgroundColorQk = selectedColors[4];

        textColorBottom = selectedColors[7];
        backgroundColorBottom = selectedColors[6];

        initFuncView(v);
        initSymbolView(v);
        initQkView(v);
        initBottomView(v);

    }

    public void initFuncView(View v) {

        myViewType = viewType.FUNC;
        funcKeyTexts = res.getStringArray(R.array.FUNC_KEY_TEXT);
        function_layout = (LinearLayout) v.findViewById(R.id.function_layout);
        function_layout.setLayoutParams(layoutParamsFunc);
        for (int i = 0; i < 5; i++) {
            TextView textView = new TextView(v.getContext());
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(qkKeysLayoutParams);
            textView.setText(funcKeyTexts[i]);
            textView.setTextColor(textColorFunc);
            textView.setBackgroundColor(backgroundColorFunc);
            textView.setTypeface(mTypeface);
            textView.setOnClickListener(listener);
            functionKeyList.add(textView);
            function_layout.addView(textView);
        }
    }

    public void initSymbolView(View v) {
        myViewType = viewType.SYMBOL;
        String[] symbolText = {"符", ",", "。", "!", "?"};
        qk_symbol_layout = (LinearLayout) v.findViewById(R.id.symbol_layout);
        qk_symbol_layout.setLayoutParams(layoutParamsSymbol);
        for (int i = 0; i < 5; i++) {
            TextView textView = new TextView(v.getContext());
            textView.setText(symbolText[i]);
            textView.setTextColor(textColorSymbol);
            textView.setBackgroundColor(backgroundColorSymbol);
            textView.setTypeface(mTypeface);
            textView.setOnClickListener(listener);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(qkKeysLayoutParams);

            qk_symbol_layout.addView(textView);
            symbolKeyList.add(textView);

        }
    }


    public void initQkView(View v) {
        qk_enkeys_layout = (LinearLayout) v.findViewById(R.id.enkeys_layout);
        qk_enkeys_layout.setLayoutParams(layoutParamsEnkeys);
        qkKeyText = res.getStringArray(R.array.EN_KEY_TEXT);
        int count = 0;
        for (int i = 0; i < 3; i++) {
            qkEnKeysLinears[i] = new LinearLayout(v.getContext());
            qkLinearsParams[i] = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f);
            qkEnKeysLinears[i].setOrientation(LinearLayout.HORIZONTAL);
            qkEnKeysLinears[i].setGravity(Gravity.CENTER_HORIZONTAL);
            for (int j = 0; j < qk_key_num_list[i]; j++) {
                TextView textView = new TextView(v.getContext());
                textView.setText(qkKeyText[count]);
                textView.setOnClickListener(listener);
                textView.setTextColor(textColorQk);
                textView.setBackgroundColor(backgroundColorQk);
                textView.setGravity(Gravity.CENTER);
                textView.setLayoutParams(paramsEnkey);
                qkKeyList.add(textView);
                qkEnKeysLinears[i].addView(textView);
                qkEnKeysLinears[i].setLayoutParams(qkLinearsParams[i]);
                count++;
            }
            qk_enkeys_layout.addView(qkEnKeysLinears[i]);
        }

        addShiftKey(v);
        addDeleteKey(v);
    }


    public void initBottomView(View v) {
        String[] bottomBarText = res.getStringArray(R.array.BOTTOMBAR_TEXT);
        qk_bottom_layout = (LinearLayout) v.findViewById(R.id.qk_bottom_layout);
        qk_bottom_layout.setLayoutParams(layoutParamsBottom);

        for (int i = 0; i < 4; i++) {
            TextView textView = new TextView(v.getContext());
            textView.setText(bottomBarText[i]);
            textView.setTypeface(mTypeface);
            textView.setOnClickListener(listener);
            textView.setTextColor(textColorBottom);
            textView.setBackgroundColor(backgroundColorBottom);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(qkKeysLayoutParams);
            qk_bottom_layout.addView(textView);
            qkBottomKeyList.add(textView);
        }
    }

    public void addShiftKey(View v) {
        String[] shiftText = res.getStringArray(R.array.EN_SHIFT_AND_SYM_KEY_TEXT);
        TextView tv = new TextView(v.getContext());
        tv.setText(shiftText[0]);
        tv.setTypeface(mTypeface);
        tv.setTextColor(textColorQk);
        tv.setBackgroundColor(backgroundColorQk);
        tv.setGravity(Gravity.CENTER);
        tv.setOnClickListener(listener);
        tv.setLayoutParams(qkKeysLayoutParams);
        qkKeyList.add(tv);
        qkEnKeysLinears[2].addView(tv, 0);
    }

    public void addDeleteKey(View v) {
        TextView tv = new TextView(v.getContext());
        String deleteText = res.getString(R.string.delete_text);
        tv.setLayoutParams(qkKeysLayoutParams);
        tv.setGravity(Gravity.CENTER);
        tv.setText(deleteText);
        tv.setOnClickListener(listener);
        tv.setTypeface(mTypeface);
        tv.setTextColor(textColorQk);
        tv.setBackgroundColor(backgroundColorQk);
        qkEnKeysLinears[2].addView(tv);
        qkKeyList.add(tv);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            chooseColor(v);
        }
    };

    public void chooseColor(final View v) {

        myViewType = getCurrentViewType((TextView) v);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.color_picker_dialog, null);
        builder.setView(view);
        builder.setTitle("自定义皮肤");


        final ColorPicker picker = (ColorPicker) view.findViewById(R.id.color_picker);
        SVBar svBar = (SVBar) view.findViewById(R.id.color_picker_svbar);
        OpacityBar opacityBar = (OpacityBar) view.findViewById(R.id.color_picker_opacitybar);

        RadioButton rbPickAsQk = (RadioButton) view.findViewById(R.id.rb_pick_asqk);
        rbPickAsQk.setVisibility(View.GONE);

        final RadioButton rbIsText = (RadioButton) view.findViewById(R.id.rb_picker_text);
        final RadioButton rbIsBackground = (RadioButton) view.findViewById(R.id.rb_picker_background);


        picker.addSVBar(svBar);
        picker.addOpacityBar(opacityBar);
        picker.setOldCenterColor(v.getDrawingCacheBackgroundColor());

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (myViewType) {
                    case FUNC:
                        if (rbIsBackground.isChecked()) {
                            backgroundColorFunc = picker.getColor();
                            refreshFuncBackground();
                        }
                        if (rbIsText.isChecked()) {
                            textColorFunc = picker.getColor();
                            refreshFuncText();
                        }
                        break;
                    case SYMBOL:
                        if (rbIsBackground.isChecked()) {
                            backgroundColorSymbol = picker.getColor();
                            refreshSymbolBackground();
                        }
                        if (rbIsText.isChecked()) {
                            textColorSymbol = picker.getColor();
                            refreshSymbolText();
                        }
                        break;
                    case QK:
                        if (rbIsBackground.isChecked()) {
                            backgroundColorQk = picker.getColor();
                            refreshQkBackground();
                        }
                        if (rbIsText.isChecked()) {
                            textColorQk = picker.getColor();
                            refreshQkText();
                        }
                        break;
                    case BOTTOM:
                        if (rbIsBackground.isChecked()) {
                            backgroundColorBottom = picker.getColor();
                            refreshBottomBackground();
                        }
                        if (rbIsText.isChecked()) {
                            textColorBottom = picker.getColor();
                            refreshBottomText();
                        }
                        break;
                }

                ((SkinDiyActivity) getActivity()).setSelectedColorsQK(getCurrentColors());
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

        for (TextView textView : functionKeyList) {
            textView.setBackgroundColor(backgroundColorFunc);
        }
    }

    public void refreshSymbolBackground() {

        for (TextView textView : symbolKeyList) {
            textView.setBackgroundColor(backgroundColorSymbol);
        }
    }

    public void refreshQkBackground() {

        for (TextView textView : qkKeyList) {
            textView.setBackgroundColor(backgroundColorQk);
        }
    }

    public void refreshBottomBackground() {

        for (TextView textView : qkBottomKeyList) {
            textView.setBackgroundColor(backgroundColorBottom);
        }
    }

    public void refreshFuncText() {
        for (TextView textView : functionKeyList) {
            textView.setTextColor(textColorFunc);
        }
    }

    public void refreshSymbolText() {
        for (TextView textView : symbolKeyList) {
            textView.setTextColor(textColorSymbol);
        }
    }

    public void refreshQkText() {
        for (TextView textView : qkKeyList) {
            textView.setTextColor(textColorQk);
        }
    }

    public void refreshBottomText() {
        for (TextView textView : qkBottomKeyList) {
            textView.setTextColor(textColorBottom);
        }
    }

    public viewType getCurrentViewType(TextView v) {

        for (TextView textView : functionKeyList) {
            if (textView.equals(v)) {
                return viewType.FUNC;
            }
        }

        for (TextView textView : symbolKeyList) {
            if (textView.equals(v)) {
                return viewType.SYMBOL;
            }
        }

        for (TextView textView : qkKeyList) {
            if (textView.equals(v)) {
                return viewType.QK;
            }
        }

        for (TextView textView : qkBottomKeyList) {
            if (textView.equals(v)) {
                return viewType.BOTTOM;
            }
        }
        return viewType.FUNC;
    }

    public int[] getCurrentColors() {


        int[] colors = {backgroundColorFunc, textColorFunc,
                backgroundColorSymbol, textColorSymbol,
                backgroundColorQk, textColorQk,
                backgroundColorBottom, textColorBottom
        };
        return colors;
    }
}
