package com.hit.wi.t9.effect;

import android.content.Context;
import android.os.Vibrator;

/**
 * @author Liabiao
 * @description 该类负责按键震动大小强度控制功能
 */

public final class VibrateManager {
    private final Vibrator vibrator;
    private final static int milliseconds = 50;// 最大震动时长
    private int mMilliStrength;// 震动强度

    public VibrateManager(final Context context) {
        vibrator = (Vibrator) context
                .getSystemService(Context.VIBRATOR_SERVICE);
    }

    public final void setStrength(final int milliStrength) {
        mMilliStrength = milliseconds * milliStrength / 100;
    }

    public final void Vibrate() {
        if (mMilliStrength > 0) {
            vibrator.vibrate(mMilliStrength);
        }
    }
}

