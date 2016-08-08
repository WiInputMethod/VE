package com.hit.wi.t9.functions;

import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import com.hit.wi.jni.Kernel;
import com.hit.wi.t9.SoftKeyboard;
import com.hit.wi.t9.values.Global;

/**
 * Created by purebluesong on 2016/4/25.
 */
public class PinyinEditProcess {
    private final char QUOTATION_ASC = 39;
    public int mSelStart;
    public int mSelEnd;
    public int mCandidateStart;
    public int mCandidateEnd;
    private SoftKeyboard softKeyboard;

    public PinyinEditProcess(SoftKeyboard softKeyboard) {
        this.softKeyboard = softKeyboard;
    }

    public void computeCursorPosition(InputConnection ic) {
        //上屏
        if (ic != null) {
            //计算光标位置
            ic.beginBatchEdit();
            ic.setComposingText(Kernel.getWordsShowPinyin(), 1);
            ic.endBatchEdit();
        } else {
            Log.d("WIVE", "ic is null");
        }
    }

    private void commitChangesInPreEdit(int start, int end) {
        softKeyboard.preEditPopup.setCursor(start, end);
        softKeyboard.preEditPopup.refreshState();
    }

    /**
     * 后来发现出问题的原因是这里与小米联系人输入框对于candidateEnd与Start的设置冲突了
     * 但是要改的话就是整个候选词显示方式的改动
    * @author purebluesong
    * */
    private boolean isXiaomiContact() {
        EditorInfo info = softKeyboard.getCurrentInputEditorInfo();
        return info.packageName.equals("com.android.contacts") && Global.XIAOMI.equals(Build.MANUFACTURER);
    }

    private void specialOperateForXiaomiContact() {
        if (isXiaomiContact()) {
            InputConnection ic = softKeyboard.getCurrentInputConnection();
            ic.setComposingRegion(0,mSelEnd);
            ic.setComposingText("",1);
        }
    }

    /**
     * 句子边界编辑处理，也就是游标在句首或者句尾的时候的处理，处理完全就不必向下传递
     * author： purebluesong
     *
     * @return 是否向下传递
     */
    public boolean borderEditProcess(String s, boolean delete) {
        if (delete && (mSelStart <= mCandidateStart || mSelStart > mCandidateEnd) && mCandidateEnd!=-1) {
            softKeyboard.sendDownUpKeyEvents(KeyEvent.KEYCODE_DEL);
            softKeyboard.preEditPopup.setCursor(mSelStart - 1, mSelEnd - 1);
            return true;
        }
        if (mCandidateEnd <= mSelStart) {
            if (delete) {
                Kernel.deleteAction();
            } else {
                Kernel.inputPinyin(s);
            }
            int cursor = Kernel.getWordsShowPinyin().length();
            if (mSelEnd != 0) {
                softKeyboard.preEditPopup.setCursor(cursor);
            }
            specialOperateForXiaomiContact();
            computeCursorPosition(softKeyboard.getCurrentInputConnection());
            softKeyboard.refreshDisplay();
            return true;
        }
        return false;
    }

    private int countQuotation(int readLength, String str) {
        int count = 0;
        for (int i = 0; i < readLength && i < str.length(); i++) {
            if (str.charAt(i) == QUOTATION_ASC) {
                count++;
                readLength++;
            }
        }
        return count;
    }

    private void innerPinyinProcess(InputConnection ic, String sBegin, String sEnd) {
        Log.d("WIVE", "pinyin process");

        Kernel.cleanKernel();
        Kernel.inputPinyin(sBegin + sEnd);
        String showPinyin = Kernel.getWordsShowPinyin();

        int position = sBegin.length() + countQuotation(sBegin.length(), showPinyin);
        commitChangesInPreEdit(position,position);
        ic.setComposingText(showPinyin, 1);
        ic.setSelection(mCandidateStart + position, mCandidateStart + position);
    }

    private void innerChineseInsert(String s) {
        Log.d("WIVE", "chinese insert");
        //因为内核的缺陷，选择了无视用户的请求=。=b
        Kernel.inputPinyin(s);
        commitChangesInPreEdit(mSelStart, mSelEnd);
    }

    private void innerChineseDelete(InputConnection ic, String sBegin, String sEnd, int sAfterEngPosition) {
        Log.d("WIVE", "chinese delete");
        ic.commitText(sBegin, 1);
        ic.commitText(sEnd.substring(0, sAfterEngPosition), 1);

        Kernel.cleanKernel();
        Kernel.inputPinyin(sEnd.substring(sAfterEngPosition));

        String showPinyin = Kernel.getWordsShowPinyin();
        ic.setComposingText(showPinyin, 1);
        ic.setSelection(mCandidateStart + showPinyin.length(), mCandidateStart + showPinyin.length());
        commitChangesInPreEdit(mCandidateStart + showPinyin.length(), mCandidateStart + showPinyin.length());
    }

    private void borderChinesePinyinInsert(InputConnection ic, String sBefore, String pinyin) {
        Log.d("WIVE", "border chinese insert");

        ic.commitText(sBefore.substring(0, sBefore.length() - 1), 1);
        Kernel.cleanKernel();
        Kernel.inputPinyin(pinyin);

        ic.setComposingText(Kernel.getWordsShowPinyin(), 0);
        ic.setSelection(mCandidateEnd, mCandidateEnd);
        commitChangesInPreEdit(mCandidateEnd, mCandidateEnd);
    }

    private void withChineseInsert(InputConnection ic, String sBefore, String sAfter, int i) {
        Log.d("WIVE", "with chinese insert");
        ic.commitText(sBefore.substring(0, i), 1);
        Kernel.cleanKernel();
        Kernel.inputPinyin(sBefore.substring(i) + sAfter);
        String r = Kernel.getWordsShowPinyin();
        int k, j;
        for (k = i, j = 0; k < sBefore.length() && j < r.length(); k++) {
            if (r.charAt(j) == '\'') j++;
            j++;
        }
        ic.setComposingText(r, 1);
        ic.setSelection(mCandidateStart + i + j, mCandidateStart + i + j);
        commitChangesInPreEdit(mCandidateStart + i + j, mCandidateEnd + i + j);
    }

    private void withChineseDelete() {
        Log.d("WIVE", "with chinese delete");
    }

    private int getFirstEnglishLetterPosition(int readLength, String sBefore) {
        int i = 0;
        while (i < readLength && Character.getType(sBefore.charAt(i)) == Character.OTHER_LETTER) {
            i++;
        }
        return i;
    }

    public void innerEditProcess(InputConnection ic, String sBefore, String s, String sAfter, Boolean delete) {
        Log.d("WIVE", "inner edit");
        int beforeLength = sBefore.length();
        int afterLength = sAfter.length();

        //Character.OTHER_LETTER means chinese character
        if ((beforeLength > 0 && Character.getType(sBefore.charAt(0)) != Character.OTHER_LETTER) ||
                (beforeLength == 0 && afterLength >= 0)) {
            innerPinyinProcess(ic, sBefore, sAfter);
        } else { //未上屏字符中有中文
            int sBeforeEngPosition = getFirstEnglishLetterPosition(beforeLength, sBefore);
            if (sBeforeEngPosition == beforeLength || (sBeforeEngPosition == beforeLength - 1 && !delete)) {
                // 光标前是中文
                int sAfterEngPosition = getFirstEnglishLetterPosition(afterLength, sAfter);
                if (delete) {
                    innerChineseDelete(ic, sBefore, sAfter, sAfterEngPosition);
                } else if (sAfterEngPosition != 0) {// if current cursor is in chinese characters
                    innerChineseInsert(s);
                } else if (beforeLength > 0) {//if before cursor is chinese and after cursor is pinyin
                    borderChinesePinyinInsert(ic, sBefore, s + sAfter);
                }
            } else {
                // 光标前不是中文
                if (delete) {
                    withChineseDelete();
                } else {
                    withChineseInsert(ic, sBefore, sAfter, sBeforeEngPosition);
                }
            }
        }
    }

    /**
     * 按句中位置比例设置游标位置
     *
     * @param rate 0< rate <1 表示游标位置
     */
    public void setCursorByRate(InputConnection ic, float rate) {
        if (softKeyboard.preEditPopup.getTextLength() > 0) {
            int realPosition = (int) (rate * softKeyboard.preEditPopup.getTextLength());
            commitChangesInPreEdit(realPosition, realPosition);
            if (ic != null && (realPosition + mCandidateStart) >= 0) {
                ic.setSelection(mCandidateStart + realPosition, mCandidateStart + realPosition);
            }
        } else if (ic != null) {
            int textLength = ic.getTextAfterCursor(100, 0).length() + ic.getTextBeforeCursor(100, 0).length();
            int realPosition = (int) (rate * textLength);
            ic.setSelection(realPosition, realPosition);
        }
    }


}
