package com.hit.wi.t9.view;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hit.wi.t9.R;
import com.hit.wi.t9.effect.SoundManager;
import com.hit.wi.t9.effect.VibrateManager;

public final class SeekbarPreference extends DialogPreference implements
        SeekBar.OnSeekBarChangeListener {

    private TextView soundView;
    final private SoundManager mSoundManager;
    final private VibrateManager mVibrateManager;

    public SeekbarPreference(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mSoundManager = new SoundManager(context);
        mVibrateManager = new VibrateManager(context);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.preference.DialogPreference#onBindDialogView(android.view.View)
     */
    @Override
    protected final void onBindDialogView(final View view) {
        final SeekBar soundBar = (SeekBar) view.findViewById(R.id.soundBar);
        soundView = (TextView) view.findViewById(R.id.soundView);
        soundBar.setOnSeekBarChangeListener(this);
        soundBar.setProgress(getSharedPreferences().getInt(getKey(), 0));
        if (getTitle().equals(
                getContext().getResources().getString(R.string.sound))) {
            mSoundManager.setSoundType(Integer.parseInt(getSharedPreferences()
                    .getString("KEY_SOUND_EFFECT_SELECTOR", "1")));
        }
        super.onBindDialogView(view);
    }

    public final void onProgressChanged(final SeekBar seekBar,
                                        final int progress, final boolean fromUser) {
        final String state;
        if (progress == 0) {
            state = getContext().getString(R.string.silent);
        } else if (progress < 30) {
            state = getContext().getString(R.string.low);
        } else if (progress >= 30 && progress < 60) {
            state = getContext().getString(R.string.middle);
        } else if (progress > 60) {
            state = getContext().getString(R.string.high);
        } else {
            state = "";
        }
        soundView.setText(getContext().getString(R.string.rate) + state);
        getSharedPreferences().edit().putInt(getKey(), progress).commit();
        if (getTitle().equals(
                getContext().getResources().getString(R.string.sound))) {
            mSoundManager.setVolume(progress);
            mSoundManager.playSound();// 用户设置时给出声音反馈
        } else if (getTitle().equals(
                getContext().getResources().getString(R.string.vibrator))) {
            mVibrateManager.setStrength(progress);
            mVibrateManager.Vibrate();// 用户设置时给出震动反馈
        } else if (getTitle().equals(
                getContext().getResources().getString(
                        R.string.title_slide_pin_volume))) {
            mSoundManager.setSlideVolume(progress);
            mSoundManager.playSwipeSound();
        }
    }

    public final void onStartTrackingTouch(final SeekBar seekBar) {
    }

    public final void onStopTrackingTouch(final SeekBar seekBar) {
    }

}
