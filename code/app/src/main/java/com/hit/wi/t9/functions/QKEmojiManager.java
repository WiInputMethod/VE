package com.hit.wi.t9.functions;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.inputmethodservice.InputMethodService;
import android.os.Build;
import android.os.Handler;
import android.view.inputmethod.InputConnection;

public class QKEmojiManager {
    HashMap<String, String> mSoftBank2UnifiedMap;
    Handler mHandler = new Handler();

    public QKEmojiManager(InputMethodService context) {
        try {
            InputStream file = context.getAssets().open("dict_qk/emoji/emoji_softbank2unified.ser");
            ObjectInputStream ois = new ObjectInputStream(file);
            mSoftBank2UnifiedMap = (HashMap<String, String>) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isEmojiString(String s) {
        return mSoftBank2UnifiedMap != null && mSoftBank2UnifiedMap.containsKey(s);
    }

    public String getShowString(String s) {
        if (mSoftBank2UnifiedMap == null) {
            return s;
        }
        String t = mSoftBank2UnifiedMap.get(s);
        if (t == null) {
            t = s;
        }
        return t;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void commitEmoji(InputMethodService context, String s) {
        InputConnection ic = context.getCurrentInputConnection();
        if (ic == null) return;
        if (context.getCurrentInputEditorInfo().packageName.contains("com.tencent.mm")) {
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
            s = getShowString(s);
            ic.commitText(s, 1);
        }
    }
}
