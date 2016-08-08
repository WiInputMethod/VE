package com.hit.wi.t9.settings;


import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.*;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hit.wi.jni.DictManager;
import com.hit.wi.jni.NKInitDictFile;
import com.hit.wi.jni.WIInputMethodNK;
import com.hit.wi.util.CommonFuncs;
import com.hit.wi.t9.R;
import com.hit.wi.t9.values.Global;
import com.umeng.analytics.MobclickAgent;

/**
 * 输入法设置界面
 */
public final class WIT9Activity extends PreferenceActivity implements OnPreferenceClickListener, OnPreferenceChangeListener {
    private Preference mDataBackUp;
    private Preference mDataRecover;
    /* 自定义点滑 */
    private PreferenceScreen mUserdefSlidePin;
    /* 添加自定义点滑 */
    private Preference mAddUserdefSlidePin;

    private Builder mAddUserdefWordDialog;

    /**
     * 推荐点滑方案的Preference
     */
    private Preference mSlideRecommend0;
    private Preference mSlideRecommend1;
    private Preference mSlideRecommend2;
    private Preference mSlideRecommend3;


    private Preference mLianXiang;
//    private Preference mReverse;
//    private Preference mLeftHand;
    private ListPreference mKeyboardSelector;
    private PreferenceCategory mSlidePin;
    /**
     * 双拼的选项
     */
    private ListPreference mShuangPinOption;
    private PreferenceScreen mSuperShuangPinOption;
    private CheckBoxPreference mHunPin;
    private CheckBoxPreference mShuangPinEdit;
    private CheckBoxPreference mShuangPinErrorCorrect;
    private SharedPreferences mSpUserLog = null;
    /**
     * MIUI悬浮窗权限
     */
    private Preference mMIUIPermission;

    private AlertDialog.Builder permissionDialog;

    private Properties properties;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.setting);
        NKInitDictFile.NKInitWiDict(getApplicationContext());

        properties = new Properties();
        try {
            properties.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getBoolean(Global.PERMISSION_TAG, false)) {
            Intent intent = getPermissionIntent();
            if (intent != null) {
                showPermissionDialog(intent);
                Editor ed = sp.edit();
                ed.putBoolean(Global.PERMISSION_TAG, false);
                ed.commit();
            }
        }

        mDataBackUp = findPreference("DATA_BACK_UP");
        mDataRecover = findPreference("DATA_RECOVER");
        mUserdefSlidePin = (PreferenceScreen) findPreference("SELF_DEF_SLIDE_PIN");
        mAddUserdefSlidePin = findPreference("SELF_DEF_SLIDE_PIN_ADD");
        mLianXiang = findPreference("LIANXIANG_SELECTOR");
//        mReverse = findPreference("REVERSE_CANDIDATE");
//        mLeftHand = findPreference("LEFT_HAND");
        mKeyboardSelector = (ListPreference) findPreference("KEYBOARD_SELECTOR");
        mSlidePin = (PreferenceCategory) findPreference("slide_pin_mode");

        mSlideRecommend0 = findPreference("SLIDE_RECOMMEND0");
        mSlideRecommend1 = findPreference("SLIDE_RECOMMEND1");
        mSlideRecommend2 = findPreference("SLIDE_RECOMMEND2");
        mSlideRecommend3 = findPreference("SLIDE_RECOMMEND3");
        mShuangPinOption = (ListPreference) findPreference("shuangpin_selector");
        mSuperShuangPinOption = (PreferenceScreen) findPreference("advance_shuangpin_window");
        mHunPin = (CheckBoxPreference) findPreference("HUN_PIN");
        mShuangPinEdit = (CheckBoxPreference) findPreference("SHUANGPIN_EDIT_SWITCH");
        mShuangPinErrorCorrect = (CheckBoxPreference) findPreference("SHUANGPIN_ERROR_CORRECT");
        mMIUIPermission = findPreference("miui_permission");

        mDataBackUp.setOnPreferenceClickListener(this);
        mDataRecover.setOnPreferenceClickListener(this);
        mUserdefSlidePin.setOnPreferenceClickListener(this);
        mAddUserdefSlidePin.setOnPreferenceClickListener(this);
        mKeyboardSelector.setOnPreferenceChangeListener(this);
        mSlideRecommend0.setOnPreferenceClickListener(this);
        mSlideRecommend1.setOnPreferenceClickListener(this);
        mSlideRecommend2.setOnPreferenceClickListener(this);
        mSlideRecommend3.setOnPreferenceClickListener(this);
        mShuangPinOption.setOnPreferenceClickListener(this);
        mSuperShuangPinOption.setOnPreferenceClickListener(this);
        mHunPin.setOnPreferenceClickListener(this);
        mShuangPinEdit.setOnPreferenceClickListener(this);
        mShuangPinErrorCorrect.setOnPreferenceClickListener(this);
        mMIUIPermission.setOnPreferenceClickListener(this);

        mSpUserLog = PreferenceManager.getDefaultSharedPreferences(this);
        if (Integer.parseInt(mSpUserLog.getString("KEYBOARD_SELECTOR", "1")) == 2) {
            mShuangPinOption.setEnabled(true);
            mSuperShuangPinOption.setEnabled(true);
            mSlidePin.setEnabled(true);
        }


        /**
         * umeng发送策略
         */
        MobclickAgent.updateOnlineConfig(this);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * @param intent 打开MIUI的权限设置界面
     */
    public void showPermissionDialog(final Intent intent) {
        if (permissionDialog == null) {
            permissionDialog = new Builder(this);
        }
        permissionDialog.setMessage("检测到您的系统为MIUI系统，开启悬浮窗即可体验悬浮特效");
        permissionDialog.setTitle("MIUI悬浮窗权限");
        permissionDialog.setPositiveButton("去开启", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                showPermissionActivity(intent);
            }
        });
        permissionDialog.setNegativeButton("稍后开启", new OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        permissionDialog.show();
    }

    /**
     * 获得MIUI设置界面的Intent
     * @return 返回MIUI的权限设置界面Intent
     */
    public Intent getPermissionIntent() {
        Intent intent = null;
        if (Global.XIAOMI.equals(Build.MANUFACTURER)) {
            if ((Global.V5).equals(properties.getProperty(Global.KEY_MIUI_VERSION_NAME))) {
                intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.parse(Global.PACKAGE);
                intent.setData(uri);
                return intent;
            } else if (Global.V6.equals(properties.getProperty(Global.KEY_MIUI_VERSION_NAME )) ||
                    Global.V7.equals(properties.getProperty(Global.KEY_MIUI_VERSION_NAME)) ||
                    Global.V8.equals(properties.getProperty(Global.KEY_MIUI_VERSION_NAME))
                    ) {
                intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                intent.putExtra("extra_pkgname", getPackageName());
                return intent;
            }
        }
        return intent;
    }

    /**
     * 设置MIUI权限设置Activity
     * @param intent
     */
    public void showPermissionActivity(Intent intent) {
        if (intent!=null && intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "当前系统非MIUI，无法设置", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取设备信息
     *
     * @param context
     * @return
     */
    public static String getDeviceInfo(Context context) {
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            String device_id = tm.getDeviceId();

            android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            String mac = wifi.getConnectionInfo().getMacAddress();
            json.put("mac", mac);

            if (TextUtils.isEmpty(device_id)) {
                device_id = mac;
            }

            if (TextUtils.isEmpty(device_id)) {
                device_id = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            }

            json.put("device_id", device_id);

            return json.toString();
        } catch (Exception e) {
            Log.d("WIVE","get Device info"+e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param preference
     * @return
     */
    public boolean onPreferenceClick(Preference preference) {
        if (preference.equals(mDataBackUp)) {
            backUpData();
            return true;
        } else
        if (preference.equals(mDataRecover)) {
            recoverData();
            return true;
        } else
        if (preference.equals(mUserdefSlidePin)) {
            refreshSlidPinList();
        } else
        if (preference.equals(mAddUserdefSlidePin)) {
            addUserdefSlidePin();
        } else
        if (preference.getKey().equals("SLIDE_PIN")) {
            modifyUserdefSlidePin(preference);
        } else
        if (preference.getKey().equals("SLIDE_RECOMMEND0")) {
            loadRecommendSlideMode(0);
        } else
        if (preference.getKey().equals("SLIDE_RECOMMEND1")) {
            loadRecommendSlideMode(1);
        } else
        if (preference.getKey().equals("SLIDE_RECOMMEND2")) {
            loadRecommendSlideMode(2);
        } else
        if (preference.getKey().equals("SLIDE_RECOMMEND3")) {
            loadRecommendSlideMode(3);
        } else
        if (preference.equals(mMIUIPermission)) {
            Intent intent = getPermissionIntent();
            showPermissionActivity(intent);
        }
        return false;
    }

    /**
     *更新点滑列表
     */
    private final void refreshSlidPinList() {
        mUserdefSlidePin.removeAll();
        mUserdefSlidePin.addPreference(mAddUserdefSlidePin);
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        for (char i = 'A'; i <= 'Z'; i++) {
            final String pin = sp.getString("SLIDE_PIN_" + i, null);
            if (pin != null && pin.length() > 0) {
                final Preference p = new Preference(this);
                p.setTitle(i + "");
                p.setSummary(pin);
                p.setKey("SLIDE_PIN");
                p.setOnPreferenceClickListener(this);
                mUserdefSlidePin.addPreference(p);
            } else {
                sp.edit().remove("SLIDE_PIN_" + i).commit();
            }
        }
    }

    /**
     * 用户自定义点滑
     */
    final private void addUserdefSlidePin() {
        final LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(
                R.layout.user_def_slide_pin_add_layout,
                (ViewGroup) findViewById(R.id.dialog));
        final OnClickListener ocl = new OnClickListener() {
            public final void onClick(final DialogInterface dialog,
                                      final int which) {
                final TextView tvPinyin = (TextView) layout
                        .findViewById(R.id.key);
                final TextView tvWord = (TextView) layout
                        .findViewById(R.id.word);
                final String key = tvPinyin.getText().toString();
                final String word = tvWord.getText().toString();
                if (key.length() == 0) {
                    CommonFuncs.showToast(WIT9Activity.this, "添加失败\n按键不能为空");
                } else if (word.length() == 0) {
                    CommonFuncs.showToast(WIT9Activity.this, "添加失败\n文字不能为空");
                } else {
                    final SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(WIT9Activity.this);
                    final String tag = "SLIDE_PIN_" + Character.toUpperCase(key.charAt(0));
                    if (sp.getString(tag, null) != null) {
                        CommonFuncs.showToast(WIT9Activity.this, "添加失败\n'" + key + "'上已经设置了点滑文字\n请点击进行修改");
                    } else {
                        sp.edit().putString(tag, word).commit();
                        refreshSlidPinList();
                    }
                }
            }
        };
        mAddUserdefWordDialog = new AlertDialog.Builder(this)
                .setPositiveButton("保存", ocl).setNegativeButton("取消", null);
        mAddUserdefWordDialog.setView(layout);
        mAddUserdefWordDialog.setTitle("添加自定义点滑");
        mAddUserdefWordDialog.setMessage("(请在BCDFGJKLMNRSTVWXYZ键位上进行设置，AEIOUHPQ被系统所用，不可以进行自定义设置)");
        mAddUserdefWordDialog.show();
    }

    /**
     * 修改点滑设置
     * @param preference
     */
    private final void modifyUserdefSlidePin(final Preference preference) {
        final LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.user_def_slide_pin_add_layout,
                (ViewGroup) findViewById(R.id.dialog));
        final TextView tvPinyin = (TextView) layout.findViewById(R.id.key);
        final TextView tvWord = (TextView) layout.findViewById(R.id.word);
        tvPinyin.setEnabled(false);
        tvPinyin.setText(preference.getTitle());
        tvWord.setText(preference.getSummary());
        final OnClickListener ocl = new OnClickListener() {
            public final void onClick(final DialogInterface dialog,
                                      final int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    final String key = tvPinyin.getText().toString();
                    final String word = tvWord.getText().toString();
                    if (word.length() == 0) {
                        CommonFuncs.showToast(WIT9Activity.this, "修改失败\n文字不能为空");
                    } else {
                        final SharedPreferences sp = PreferenceManager
                                .getDefaultSharedPreferences(WIT9Activity.this);
                        final String tag = "SLIDE_PIN_" + Character.toUpperCase(key.charAt(0));
                        sp.edit().putString(tag, word).commit();
                        refreshSlidPinList();
                    }
                } else if (which == DialogInterface.BUTTON_NEUTRAL) {
                    final String key = tvPinyin.getText().toString();
                    final SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(WIT9Activity.this);
                    final String tag = "SLIDE_PIN_" + Character.toUpperCase(key.charAt(0));
                    sp.edit().remove(tag).commit();
                    refreshSlidPinList();
                }
            }
        };
        mAddUserdefWordDialog = new AlertDialog.Builder(this)
                .setPositiveButton("保存", ocl).setNegativeButton("取消", null)
                .setNeutralButton("删除", ocl);
        mAddUserdefWordDialog.setView(layout);
        mAddUserdefWordDialog.setTitle("修改自定义点滑");
        mAddUserdefWordDialog.show();
    }

    /**
     * 加载系统自带点滑
     * @param type
     */
    private final void loadRecommendSlideMode(int type) {
        String[][] recommendSlide = new String[4][];
        recommendSlide[0] = getResources().getStringArray(R.array.RECOMMEND_SLIDE_TEXT0);
        recommendSlide[1] = getResources().getStringArray(R.array.RECOMMEND_SLIDE_TEXT1);
        recommendSlide[2] = getResources().getStringArray(R.array.RECOMMEND_SLIDE_TEXT2);
        recommendSlide[3] = getResources().getStringArray(R.array.RECOMMEND_SLIDE_TEXT3);
        final SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(WIT9Activity.this);
        String tag;

        for (int i = 0; i < 26; i++) {
            tag = "SLIDE_PIN_" + (char) ('A' + i);
            sp.edit().putString(tag, recommendSlide[type][i]).commit();
        }
        refreshSlidPinList();
        CommonFuncs.showToast(this, "成功加载点滑方案");
    }

    /**
     * 备份点滑设置
     */
    private final void backUpData() {
        Resources res = getResources();
        String backUpSuccess = res.getString(R.string.back_up_success);
        String backUpFailed = res.getString(R.string.back_up_failed);
        String notFoundSDCard = res.getString(R.string.not_found_sd_card);
        String backUpPath = res.getString(R.string.back_up_folder_name);
        String kernelNotInit = res.getString(R.string.kernel_not_init);
        String noUserWord = res.getString(R.string.no_user_word);
        String canNotCreateFile = res.getString(R.string.cannot_create_file);
        String canNotCreateFolder = res.getString(R.string.cannot_create_folder);
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            final String path = android.os.Environment
                    .getExternalStorageDirectory().getAbsolutePath()
                    + File.separator
                    + backUpPath + File.separator;
            final File dictfile = new File(path);
            if (dictfile.exists() == false || !dictfile.isDirectory()) {
                if (!dictfile.mkdir()) {
                    Toast.makeText(this, backUpFailed + "\n" + canNotCreateFolder + path, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            int result = DictManager.BackupData(path);
            switch (result) {
                case WIInputMethodNK.EMOJI_DICT_NOT_FOUND:
                case WIInputMethodNK.SUCCESS:
                    Toast.makeText(this, backUpSuccess + "\n" + path, Toast.LENGTH_SHORT).show();
                    break;
                case WIInputMethodNK.SETTING_NOT_LOAD:
                    Toast.makeText(this, backUpFailed + "\n" + kernelNotInit, Toast.LENGTH_SHORT).show();
                    break;
                case WIInputMethodNK.USR_DICT_NOT_FOUND:
                    Toast.makeText(this, backUpFailed + "\n" + noUserWord, Toast.LENGTH_SHORT).show();
                    break;
                case WIInputMethodNK.BACKUP_EMOJI_FILE_CAN_NOT_BUILD:
                case WIInputMethodNK.BACKUP_FILE_CAN_NOT_BUILD:
                    Toast.makeText(this, backUpFailed + "\n" + canNotCreateFile + path, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(this, backUpFailed, Toast.LENGTH_SHORT).show();
                    break;
            }
        } else {
            Toast.makeText(this, backUpFailed + "\n" + notFoundSDCard, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 回复默认点滑设置
     */
    private final void recoverData() {
        Resources res = getResources();
        String backUpPath = res.getString(R.string.back_up_folder_name);
        String restore = res.getString(R.string.restore);
        String wordsNumber = res.getString(R.string.words_number);
        String restoreFailed = res.getString(R.string.restore_failed);
        String fileBrocken = res.getString(R.string.file_broken);
        String fileNotFound = res.getString(R.string.not_found_restore_file);
        String sdCardNotFound = res.getString(R.string.not_found_sd_card);
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            final String path = android.os.Environment
                    .getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + backUpPath + File.separator;
            final File dictpath = new File(path);
            if (dictpath.exists() && dictpath.isDirectory()) {
                int result = DictManager.RestoreData(path);
                if (result >= 0) {
                    Toast.makeText(this, restore + result + wordsNumber, Toast.LENGTH_SHORT).show();
                } else {
                    switch (result) {
                        case WIInputMethodNK.BACKUP_FILE_CAN_NOT_BUILD:
                            Toast.makeText(this, restoreFailed + "\n" + fileNotFound + dictpath, Toast.LENGTH_SHORT).show();
                            break;
                        case WIInputMethodNK.INSERT_USR_WORD_FAILED:
                            Toast.makeText(this, restoreFailed + "\n" + fileBrocken, Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(this, restoreFailed, Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            } else {
                Toast.makeText(this, restoreFailed + "\n" + fileNotFound + dictpath, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, restoreFailed + "\n" + sdCardNotFound, Toast.LENGTH_SHORT).show();
        }
    }

    public void setEnabled() {
        mLianXiang.setEnabled(true);
        mSlidePin.setEnabled(false);
        mShuangPinOption.setEnabled(false);
        mSuperShuangPinOption.setEnabled(false);
    }

    public void setDisabled() {
        mLianXiang.setEnabled(false);
        mSlidePin.setEnabled(true);
        mShuangPinOption.setEnabled(true);
        mSuperShuangPinOption.setEnabled(true);
    }

    /**
     *
     * @param preference
     * @param newValue
     * @return
     */
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.equals(mKeyboardSelector)) {
            if (newValue.equals("1")) {
                setEnabled();
            } else {
                setDisabled();
            }
        }
        return true;
    }
}