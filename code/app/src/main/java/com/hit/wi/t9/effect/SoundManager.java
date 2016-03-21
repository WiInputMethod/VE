package com.hit.wi.t9.effect;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.hit.wi.t9.R;

/**
 * @author Liabiao
 * @description 该类负责按键音效方案加载，音量大小控制功能
 */

public final class SoundManager {
    final private SoundPool mSoundPool;// 用来创建和播放声音
    final private HashMap<Integer, Integer> mSoundPoolMap;// 用来存储多声道
    final private AudioManager mAudioManager;// 服务句柄
    private final float maxVolume = 1;// 音量最大值
    private float mVolume;// 音量大小，范围为1-100
    private float mSlideVolume;
    private int mSoundScheme;// 声音方案

    // 声音方案
    private final static int SOUND_IPHONE = 1;// iphone声音
    private final static int SOUND_WP7 = 2;// wphone7声音
    private final static int SOUND_WI = 3;// wi声音
    private final static int SOUND_HEART_BEAT = 4;// 心跳声音
    private final static int SOUND_WALKING = 5;// 走路声音
    private final static int SOUND_WATER = 6;// 水滴声音
    private final static int SOUND_WOODEN_FISH = 7;// 木鱼声音
    private final static int SOUND_SWIPE = 8;// 点滑声音
    private final static int SOUND_ANDROID = 9;// android声音

    public SoundManager(final Context context) {
        mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);// 4个声道
        mSoundPoolMap = new HashMap<Integer, Integer>();
        mAudioManager = (AudioManager) context
                .getSystemService(Context.AUDIO_SERVICE);
        mSoundScheme = 1;
        this.addSound(context, SOUND_IPHONE, R.raw.iphonekeypress);// 1号为默认按键iphone声音
        this.addSound(context, SOUND_WP7, R.raw.w7keypress);// 2号为默认按键w7声音方案
        this.addSound(context, SOUND_WI, R.raw.wi);
        this.addSound(context, SOUND_HEART_BEAT, R.raw.xintiao);
        this.addSound(context, SOUND_WALKING, R.raw.zoulu);
        this.addSound(context, SOUND_WATER, R.raw.shuidi);
        this.addSound(context, SOUND_WOODEN_FISH, R.raw.wii);
        this.addSound(context, SOUND_SWIPE, R.raw.bling);
    }

    // 初始化函数,参数依次为：上下文句柄、音量强度、音效方案
    public final void setVolume(final int vol) {
        final float percent = vol / 100.0f;// 音量百分比
        mVolume = maxVolume * percent;
    }

    public final void setSlideVolume(final int vol) {
        final float percent = vol / 100.0f;// 音量百分比
        mSlideVolume = maxVolume * percent;
    }

    public final void setSoundType(final int soundType) {
        mSoundScheme = soundType;// 声音方案
    }

    // 该函数负责添加声道
    private final void addSound(final Context context, final int index,
                                final int SoundID) {
        mSoundPoolMap.put(index, mSoundPool.load(context, SoundID, 1));// 加载默认按键音效
    }

    // 播放声音函数
    public final void playSound() {
        if (mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL
                && mVolume > 0.001) {
            if (mSoundScheme != SOUND_ANDROID) {
                mSoundPool.play(mSoundPoolMap.get(mSoundScheme),
                        mVolume, mVolume, 1, 0, 1f);
            } else {
                mAudioManager.playSoundEffect(
                        AudioManager.FX_KEYPRESS_STANDARD, mVolume);
            }
        }
    }

    public final void playEnterSound() {
        if (mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL
                && mVolume > 0.001) {
            mAudioManager.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
        }
    }

    public final void playSpaceSound() {
        if (mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL
                && mVolume > 0.001) {
            mAudioManager.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
        }
    }

    public final void playBackspaceSound() {
        if (mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL
                && mVolume > 0.001) {
            mAudioManager.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
        }
    }

    public final void playClickSound() {
        if (mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL
                && mVolume > 0.001) {
            mAudioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
        }
    }

    public final void playSwipeSound() {
        if (mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL
                && mSlideVolume > 0.001) {
            mSoundPool.play(mSoundPoolMap.get(SOUND_SWIPE),
                    mSlideVolume, mSlideVolume, 1, 0, 1f);
        }
    }

}
