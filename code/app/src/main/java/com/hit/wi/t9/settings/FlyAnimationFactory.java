package com.hit.wi.t9.settings;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;

/**
 * 工厂方法,生成一个飞进飞出的动画
 *
 * @author dagger
 */
public class FlyAnimationFactory {
    public static final int FLYIN_ANIMATION = 0;
    public static final int FLYOUT_ANIMATION = 1;

    float mWidth, mHeight, mY, mFromX, mToX;
    long mDuration;
    int mInterpolatorType;
    TranslateAnimation animation;
    private static FlyAnimationFactory mFlyAnimationFactory = null;

    private FlyAnimationFactory() {
    }

    public static FlyAnimationFactory getInstance() {
        if (mFlyAnimationFactory == null) {
            mFlyAnimationFactory = new FlyAnimationFactory();
        }
        return mFlyAnimationFactory;
    }

    /**
     * @param fromX            ABSOLUTE
     * @param toX              ABSOLUTE
     * @param Y                ABSOLUTE
     * @param duration         速度变化区间从1~1000，动画持续时间
     * @param interpolatorType
     * @return
     */
    public TranslateAnimation getFlyAnimation(float fromX, float toX, float Y, long duration, int interpolatorType) {

        this.mFromX = fromX;
        this.mToX = toX;
        this.mY = Y;
        this.mDuration = duration;
        this.mInterpolatorType = interpolatorType;

        animation = new TranslateAnimation(Animation.ABSOLUTE, mFromX,
                Animation.ABSOLUTE, mToX,
                Animation.ABSOLUTE, mY,
                Animation.ABSOLUTE, mY);

        if (interpolatorType == FLYIN_ANIMATION) {
            animation.setInterpolator(new DecelerateInterpolator());
        } else {
            animation.setInterpolator(new AccelerateInterpolator());
        }
        animation.setFillAfter(true);
        animation.setDuration(mDuration);
        return animation;
    }

}
