package com.hit.wi.t9.functions;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;

import com.hit.wi.t9.R;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.inputmethodservice.InputMethodService;
import android.os.Build;
import android.os.Handler;
import android.view.inputmethod.InputConnection;

public final class EmojiUtil {
    HashMap<String, String> mSoftBank2UnifiedMap;
    Handler mHandler = new Handler();

    @SuppressWarnings("unchecked")
    public EmojiUtil(InputMethodService context) {
        try {
            InputStream file = context.getAssets().open(context.getResources().getString(R.string.emoji_unified2softbank_path_name));
            ObjectInputStream ois = new ObjectInputStream(file);
            mSoftBank2UnifiedMap = (HashMap<String, String>) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isEmojiString(String s) {
        return s != null && s.startsWith("``");
    }

    public String getShowString(String s) {
        if (isEmojiString(s)) {
            String[] ts = s.replaceAll("``", "").split(" ");
            String res = "";
            for (int i = 0; i < ts.length; ++i) {
                if (ts[i].length() > 0) {
                    res += (char) Integer.parseInt(ts[i], 16);
                }
            }
            return res;
        }
        return s;
    }

    public boolean hasSoftbank(String s) {
        return mSoftBank2UnifiedMap != null && s != null && mSoftBank2UnifiedMap.containsKey(s);
    }

    public String getSoftbank(String s) {
        if (hasSoftbank(s)) {
            return mSoftBank2UnifiedMap.get(s);
        }
        return s;
    }

    @SuppressLint("NewApi")
    public void commitEmoji(String s, InputMethodService context) {
        InputConnection ic = context.getCurrentInputConnection();
        if (ic == null) return;
        s = getShowString(s);
        if (hasSoftbank(s) && context.getCurrentInputEditorInfo().packageName.contains("com.tencent.mm")) {
            s = getSoftbank(s);
            if (Build.VERSION.SDK_INT >= 11) {
                final ClipboardManager localClipboardManager = (ClipboardManager) context.getSystemService("clipboard");
                final ClipData localClipData = localClipboardManager.getPrimaryClip();
                ic.commitText(s, 1);
                localClipboardManager.setText("");
                ic.performContextMenuAction(android.R.id.copy);
                ic.performContextMenuAction(android.R.id.paste);
                mHandler.postDelayed(new Runnable() {

                    public void run() {
                        localClipboardManager.setPrimaryClip(localClipData);
                    }
                }, 100);
            } else {
                final ClipboardManager clip = (ClipboardManager) context.getSystemService("clipboard");
                final CharSequence clipData = clip.getText();
                clip.setText("");
                ic.commitText(s, 1);
                ic.performContextMenuAction(android.R.id.copy);
                ic.performContextMenuAction(android.R.id.paste);
                mHandler.postDelayed(new Runnable() {

                    public void run() {
                        clip.setText(clipData);
                    }
                }, 100);
            }
        } else {
            ic.commitText(s, 1);
        }
    }
}
