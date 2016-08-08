package com.hit.wi.t9.view;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import com.hit.wi.t9.R;
import com.hit.wi.t9.values.Global;

/**
 * Created by daofa on 2016/5/11.
 */
public class TextSizePreference extends DialogPreference implements
        SeekBar.OnSeekBarChangeListener {

    private TextView tv_sample;

    public TextSizePreference(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onBindDialogView(View view) {
        final SeekBar textsizeBar = (SeekBar) view.findViewById(R.id.textsizeBar);
        tv_sample = (TextView) view.findViewById(R.id.tv_textsize_sample);
        textsizeBar.setOnSeekBarChangeListener(this);
//        textsizeBar.setProgress(getSharedPreferences().getInt(getKey(),0)*100);
        super.onBindDialogView(view);
    }

    public final void onProgressChanged(final SeekBar seekBar,
                                        final int progress, final boolean fromUser) {
        tv_sample.setTextSize(progress);
        Global.textsizeFactor =  ((float) progress)/50;
        Global.textsizeFactor = Math.max(0f, Math.min(Global.textsizeFactor, 2f));
    }

    public final void onStartTrackingTouch(final SeekBar seekBar) {
    }

    public final void onStopTrackingTouch(final SeekBar seekBar) {
    }

}
