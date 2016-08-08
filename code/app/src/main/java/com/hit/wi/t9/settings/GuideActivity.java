package com.hit.wi.t9.settings;

import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.RelativeLayout.LayoutParams;

import com.hit.wi.t9.R;
import com.hit.wi.t9.values.Global;
import com.umeng.analytics.MobclickAgent;

/**
 * @author winlandiano
 */
public final class GuideActivity extends Activity implements OnTouchListener {
    //常量
    private static final int PAGE1_SCROLL_ANIMATION = 1;
    private static final String WI_VE_IM = "com.hit.wi.t9/.SoftKeyboard";

    //个页面的控件
    private RelativeLayout screenLayout;
    private ImageView page1AppIcon;
    private TextView page1AppTitle;
    private TextView page1AppSummary;

    private TextView page6Summary;
    private ImageView page6FirstImageView;
    private ImageView page6SecondImageView;
    private TextView page6Success;
    private Button page6Number;

    private float mScreenWidth, mScreenHeight;

    private float downX;
    private float upX;
    private long downTime;
    private long upTime;

    /**
     * 当前页面编号
     */
    private int currentPage = 1;

    /**
     * 当前已经显示的最大页面编号
     */
    private int maxPageShown = 0;
    /**
     * 针对勾选输入法、勾选默认而言。<br/>
     * 未开始设置=0<br/>
     * 开始设置激活输入法=1<br/>
     * 开始设置默认输入法=2<br/>
     * 已成为默认输入法=3
     */
    private int currentPrepareStage = 0;

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

    @Override
    public void onDestroy() {
        releaseImageViewResource(page1AppIcon);
        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.guide_page);

        screenLayout = (RelativeLayout) findViewById(R.id.guide_page);
        screenLayout.setFocusable(true);
        screenLayout.setOnTouchListener(this);
        mScreenWidth = (float) ((WindowManager) (getSystemService(WINDOW_SERVICE))).getDefaultDisplay().getWidth();
        mScreenHeight = (float) ((WindowManager) (getSystemService(WINDOW_SERVICE))).getDefaultDisplay().getHeight();

        if (IsFirstVisit()) {
            //第一页
            if (page1AppIcon == null) {
                page1AppIcon = new ImageView(this);
                page1AppIcon.setImageDrawable(getResources().getDrawable(R.drawable.wi_t9_icon_white));
                scaleAndBindImgToView(page1AppIcon, R.drawable.wi_t9_icon_white, (int) (mScreenWidth * 0.4), (int) (mScreenHeight * 0.4));
                LayoutParams lp = new LayoutParams((int) (mScreenWidth * 0.4), (int) (mScreenHeight * 0.4));
                lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                lp.setMargins((int) (mScreenWidth * (0.5 - 0.2)), (int) (mScreenHeight * 0.38 - mScreenWidth * 0.2), (int) (mScreenWidth * 0.3), (int) (mScreenHeight * 0.62 - mScreenWidth * 0.2));
                screenLayout.addView(page1AppIcon, lp);
            }


            if (page1AppTitle == null) {
                page1AppTitle = new TextView(this);
                page1AppTitle.setText(getString(R.string.guide_page1_appname));
                page1AppTitle.setTextSize(25);
                page1AppTitle.setTextColor(getResources().getColor(R.color.snow));
                FontMetrics fm = page1AppTitle.getPaint().getFontMetrics();
                int width = (int) (page1AppTitle.getPaint().measureText(page1AppTitle.getText().toString()));
                int height = (int) (fm.descent - fm.top);
                LayoutParams lp = new LayoutParams(width, height);
                lp.setMargins((int) (mScreenWidth / 2 - width / 2), (int) (mScreenHeight * 0.65), 0, 0);
                screenLayout.addView(page1AppTitle, lp);
            }

            if (page1AppSummary == null) {
                page1AppSummary = new TextView(this);
                page1AppSummary.setText(getString(R.string.guide_page1_welcome));
                page1AppSummary.setTextSize(25);
                page1AppSummary.setTextColor(getResources().getColor(R.color.snow));
                FontMetrics fm = page1AppTitle.getPaint().getFontMetrics();
                int width = (int) (page1AppTitle.getPaint().measureText(page1AppSummary.getText().toString()));
                int height = (int) (fm.descent - fm.top);
                LayoutParams lp = new LayoutParams(width, height);
                lp.setMargins((int) (mScreenWidth / 2 - width / 2), (int) (mScreenHeight * 0.7), 0, 0);
                screenLayout.addView(page1AppSummary, lp);
            }
            currentPage = 1;
        } else {
            currentPage = 6;//是的你没看错，至少曾经有6页
        }

        //第六页
        if (page6Summary == null) {
            page6Summary = (TextView) findViewById(R.id.page6_summary);
            page6Summary.setText(getString(R.string.guide_page6_text1));
            page6Summary.getBackground().setAlpha(0);
        }

        if (page6FirstImageView == null) {
            page6FirstImageView = new ImageView(this);
            page6FirstImageView.setBackgroundResource(R.drawable.button_snow_round);
            page6FirstImageView.getBackground().setAlpha(0);
            LayoutParams lp = new LayoutParams((int) (mScreenWidth * 0.25), (int) (mScreenWidth * 0.25));
            lp.setMargins((int) (mScreenWidth * (0.5 - 0.125)), (int) (mScreenHeight * 0.4 - mScreenWidth * 0.125), 0, 0);
            screenLayout.addView(page6FirstImageView, lp);
        }


        if (page6SecondImageView == null) {
            page6SecondImageView = new ImageView(this);
            page6SecondImageView.setBackgroundResource(R.drawable.button_snow_round);
            page6SecondImageView.getBackground().setAlpha(0);
            LayoutParams lp = new LayoutParams((int) (mScreenWidth * 0.4), (int) (mScreenWidth * 0.4));
            lp.setMargins((int) (mScreenWidth * (0.5 - 0.2)), (int) (mScreenHeight * 0.4 - mScreenWidth * 0.2), 0, 0);
            screenLayout.addView(page6SecondImageView, lp);
        }

        if (page6Number == null) {
            page6Number = new Button(this);
            page6Number.setBackgroundResource(R.drawable.blank);
            page6Summary.getPaint().setAlpha(0);
            page6Number.setText("1");
            page6Number.setTextColor(getResources().getColor(R.color.light_sea_green));
            page6Number.setTextSize(25);
            page6Summary.getPaint().setAlpha(0);
            LayoutParams lp = new LayoutParams((int) (mScreenWidth * 0.25), (int) (mScreenWidth * 0.25));
            lp.setMargins((int) (mScreenWidth * (0.5 - 0.125)), (int) (mScreenHeight * 0.4 - mScreenWidth * 0.125), 0, 0);
            screenLayout.addView(page6Number, lp);
            page6Number.setClickable(false);
        }

        if (page6Success == null) {
            page6Success = (TextView) findViewById(R.id.page6_success);
            page6Success.setText(getString(R.string.guide_page6_success));
            page6Success.setBackgroundColor(Color.argb(0, 0, 0, 0));
            page6Success.setClickable(false);
        }

        if (currentPage == 6) {
            if (checkInList() && checkIsDefault()) {
                runToSettingActivity();
            } else {
                updateWindow();
            }
        }

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if ((currentPage == 6) && hasFocus) {
            updateWindow();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //把透明度调节放在这里，这样前景和后景的透明度可以一起调节

        AlphaAnimation aa = new AlphaAnimation(0f, 0f);
        aa.setDuration(0);
        aa.setFillAfter(true);
        page6Summary.startAnimation(aa);
        page6Success.startAnimation(aa);
        page6Number.startAnimation(aa);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downTime = System.currentTimeMillis();
                if (currentPage == 6) {
                    if (!checkInList()) {
                        popupIMChossingView();
                    } else if (!checkIsDefault()) {
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showInputMethodPicker();
                    } else if (checkInList() && checkIsDefault()) {
                        runToSettingActivity();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                upX = event.getX();
                upTime = System.currentTimeMillis();
                //判断滑动距离以及滑动时间
                if (((upTime - downTime) < 800) && ((upX - downX > mScreenWidth * 0.2) || (downX - upX > mScreenWidth * 0.2))) {
                    switch (currentPage) {
                        case 1:
                            scrollPage1(upX - downX, 1);
                            break;
                        default:
                            break;
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void popupIMChossingView() {
        Intent intent = new Intent("/");
        ComponentName cm = new ComponentName("com.android.settings", "com.android.settings.LanguageSettings");
        intent.setComponent(cm);
        intent.setAction(Intent.ACTION_VIEW);
        startActivityForResult(intent, 0);
    }

    public void runToSettingActivity() {
        Intent intent = new Intent(GuideActivity.this, WIT9Activity.class);
        startActivity(intent);
        finish();
    }

    //=====   动画函数   =======

    /**
     * 给page1写的滚动函数，分为两步，第一步使控件移出，第二步由handler调用使下一页控件飞入
     *
     * @param flag X的变化值，负代表向左滑动正向右滑动
     * @param step
     */
    private void scrollPage1(float flag, int step) {
        FlyAnimationFactory mFlyAnimationFactory = FlyAnimationFactory.getInstance();
        if (flag < 0) {
            if (step == 1) {
                Message msg = new Message();
                msg.arg1 = PAGE1_SCROLL_ANIMATION;
                msg.arg2 = (int) flag;
                handler.sendMessageDelayed(msg, 400);

                page1AppIcon.clearAnimation();
                page1AppIcon.startAnimation(mFlyAnimationFactory.getFlyAnimation(0f, -mScreenWidth, 0, 400, FlyAnimationFactory.FLYOUT_ANIMATION));
                page1AppTitle.clearAnimation();
                page1AppTitle.startAnimation(mFlyAnimationFactory.getFlyAnimation(0f, -mScreenWidth, 0, 300, FlyAnimationFactory.FLYOUT_ANIMATION));
                page1AppSummary.clearAnimation();
                page1AppSummary.startAnimation(mFlyAnimationFactory.getFlyAnimation(0f, -mScreenWidth, 0, 300, FlyAnimationFactory.FLYOUT_ANIMATION));
            } else {
                updateWindow();
                currentPage = 6;
            }
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case PAGE1_SCROLL_ANIMATION:
                    scrollPage1(msg.arg2, 2);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 这段用来纪念我未曾有机会熟识的**李阳**师兄  ^_^'
     *
     * @author leeon
     * @author purebluesong
     */
    public boolean checkInList() {
        boolean isInList = false;
        // Get list of input methods
        try {
            List<InputMethodInfo> InputMethods = ((InputMethodManager) this
                    .getSystemService(Context.INPUT_METHOD_SERVICE))
                    .getEnabledInputMethodList();

            /* 检查系统输入法列表 */
            for (int i = 0; i < InputMethods.size(); i++) {
                isInList = false;
                if (WI_VE_IM.equals(InputMethods.get(i).getId())) {
                    isInList = true;
                    break;
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG);
        }
        return isInList;
    }

    /**
     * @author leeon
     * @author purebluesong
     */
    public boolean checkIsDefault() {
        boolean isDefault = false;
        try {
            /* 检查系统的默认输入法 */
            String curInputMethod = Settings.Secure.getString(
                    this.getContentResolver(),
                    Settings.Secure.DEFAULT_INPUT_METHOD);
            if (WI_VE_IM.equals(curInputMethod)) {
                isDefault = true;
            }
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG);
        }
        return isDefault;
    }

    private void pageOneToThreeAnim() {
        //之前控件退场
        AlphaAnimation aAnimation = new AlphaAnimation(1f, 0f);
        aAnimation.setDuration(700);
        aAnimation.setFillAfter(true);
        page6Summary.startAnimation(aAnimation);

        ScaleAnimation sAnimation = new ScaleAnimation(1f, 0f, 1f, 0f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        sAnimation.setDuration(700);
        sAnimation.setFillAfter(true);
        sAnimation.setInterpolator(new AccelerateInterpolator());
        page6FirstImageView.startAnimation(sAnimation);
        page6SecondImageView.startAnimation(sAnimation);

        page6Number.startAnimation(aAnimation);

        //新控件入场
        AnimationSet as = new AnimationSet(false);
        TranslateAnimation tAnimationIcon = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF, 0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0.3f,
                TranslateAnimation.RELATIVE_TO_SELF, 0f);
        tAnimationIcon.setDuration(1000);
        tAnimationIcon.setFillAfter(true);
        tAnimationIcon.setInterpolator(new OvershootInterpolator());
        tAnimationIcon.setStartOffset(750);
        as.addAnimation(tAnimationIcon);

        AlphaAnimation aAnimationIcon = new AlphaAnimation(0f, 1f);
        aAnimationIcon.setDuration(1000);
        aAnimationIcon.setFillAfter(true);
        aAnimationIcon.setStartOffset(750);
        as.addAnimation(aAnimationIcon);
        if (page1AppIcon == null) {
            page1AppIcon = new ImageView(this);
            page1AppIcon.setImageDrawable(getResources().getDrawable(R.drawable.wi_t9_icon_white));
            scaleAndBindImgToView(page1AppIcon, R.drawable.wi_t9_icon_white, (int) (mScreenWidth * 0.4), (int) (mScreenHeight * 0.4));
            LayoutParams lp = new LayoutParams((int) (mScreenWidth * 0.4), (int) (mScreenHeight * 0.4));
            lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            lp.setMargins((int) (mScreenWidth * (0.5 - 0.2)), (int) (mScreenHeight * 0.38 - mScreenWidth * 0.2), (int) (mScreenWidth * 0.3), (int) (mScreenHeight * 0.62 - mScreenWidth * 0.2));
            screenLayout.addView(page1AppIcon, lp);
        }

        page1AppIcon.startAnimation(as);

        page1AppIcon.startAnimation(as);
        page6Success.getBackground().setAlpha(255);
        page6Success.startAnimation(as);
    }

    private void pageOtherToThreeAnim() {
        AnimationSet as = new AnimationSet(false);
        TranslateAnimation tAnimationIcon = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF, 0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0.3f,
                TranslateAnimation.RELATIVE_TO_SELF, 0f);
        tAnimationIcon.setDuration(700);
        tAnimationIcon.setFillAfter(true);
        tAnimationIcon.setInterpolator(new OvershootInterpolator());
        as.addAnimation(tAnimationIcon);

        AlphaAnimation aAnimationIcon = new AlphaAnimation(0f, 1f);
        aAnimationIcon.setDuration(700);
        aAnimationIcon.setFillAfter(true);
        as.addAnimation(aAnimationIcon);
        as.setStartOffset(300);
        page1AppIcon.startAnimation(as);

        page6Success.getBackground().setAlpha(255);
        page6Success.startAnimation(as);
    }

    private void pageOtherToTwoAnim() {
        if (currentPrepareStage != 1) {
            page6FirstImageView.getBackground().setAlpha(229);
            page6Summary.getBackground().setAlpha(255);
        }
        ScaleAnimation sAnimation = new ScaleAnimation(0.0f, 1f, 0.0f, 1f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        sAnimation.setDuration(700);
        sAnimation.setStartOffset(200);
        sAnimation.setFillAfter(true);
        sAnimation.setInterpolator(new OvershootInterpolator());
        AlphaAnimation aAnimation = new AlphaAnimation(0f, 0.6f);
        aAnimation.setDuration(700);
        aAnimation.setStartOffset(200);
        aAnimation.setFillAfter(true);
        page6SecondImageView.getBackground().setAlpha(153);
        page6SecondImageView.startAnimation(aAnimation);
        page6SecondImageView.startAnimation(sAnimation);


        AlphaAnimation aAnimation2 = new AlphaAnimation(0f, 1f);
        aAnimation2.setDuration(700);
        aAnimation2.setStartOffset(200);
        page6Number.setText("2");
        page6Number.getBackground().setAlpha(255);
        page6Number.clearAnimation();
        page6Number.startAnimation(aAnimation2);

        page6Summary.clearAnimation();
        page6Summary.startAnimation(aAnimation2);
        page6Summary.setText(getString(R.string.guide_page6_text2));
    }

    private void pageOtherToOneAnim() {
        AnimationSet as = new AnimationSet(false);
        ScaleAnimation sAnimation = new ScaleAnimation(0.0f, 1f, 0.0f, 1f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        sAnimation.setDuration(700);
        sAnimation.setFillAfter(true);
        sAnimation.setInterpolator(new OvershootInterpolator());
        as.addAnimation(sAnimation);
        AlphaAnimation aAnimation = new AlphaAnimation(0f, 0.9f);
        aAnimation.setDuration(700);
        aAnimation.setFillAfter(true);
        page6FirstImageView.getBackground().setAlpha(229);
        as.addAnimation(aAnimation);
        page6FirstImageView.startAnimation(as);

        AlphaAnimation aAnimation2 = new AlphaAnimation(0f, 1f);
        aAnimation2.setDuration(700);
        page6Number.setText("1");
        page6Number.getBackground().setAlpha(255);
        page6Number.startAnimation(aAnimation2);

        page6Summary.setText(getString(R.string.guide_page6_text1));
        page6Summary.clearAnimation();
        page6Summary.startAnimation(aAnimation);
    }

    /**
     * @author someone
     * @author purebluesong
     */
    private void updateWindow() {
        if (checkInList()) {
            if (checkIsDefault()) {
                if (currentPrepareStage == 2 || currentPrepareStage == 1) {
                    pageOneToThreeAnim();
                } else {
                    pageOtherToThreeAnim();
                }
                currentPrepareStage = 3;
            } else {
                pageOtherToTwoAnim();
                currentPrepareStage = 2;
            }
        } else {
            pageOtherToOneAnim();
            currentPrepareStage = 1;
        }
    }

    /**
     * @author leeon
     */
    public final static String getVersionName(final Context context) {
        final PackageManager packageManager = context.getPackageManager();
        String versionName = "";
        try {
            final PackageInfo packInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            versionName = packInfo.versionCode + " " + packInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    boolean IsFirstVisit() {
        final String versionName = getVersionName(this);
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        final String tag = "FIRST_SHOW_GUIDE" + versionName;

        if (sp.getBoolean(tag, true)) {
            Editor ed = sp.edit();
            ed.putBoolean(tag, false);
            ed.putBoolean(Global.PERMISSION_TAG, true);
            ed.commit();
            return true;
        }
        return false;
    }

    private void scaleAndBindImgToView(ImageView iv, int drawable, int width, int height) {
        BitmapFactory.Options opts = new Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), drawable, opts);
        int imageHeight = opts.outHeight;
        int imageWidth = opts.outWidth;
        int scale = 1;
        int scaleX = imageHeight / width;
        int scaleY = imageWidth / height;
        if (scaleX > scaleY && scaleY >= 1) {
            scale = scaleX;
        }
        if (scaleX < scaleY && scaleX >= 1) {
            scale = scaleY;
        }
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = scale;
        iv.setImageBitmap(BitmapFactory.decodeResource(getResources(), drawable, opts));

    }

    private void releaseImageViewResource(ImageView img) {
        if (img == null) {
            return;
        }
        Drawable drawable = img.getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    }
}