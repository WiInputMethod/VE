package com.hit.wi.ve.functions;

import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.InputConnection;
import com.hit.wi.jni.Kernel;
import com.hit.wi.ve.SoftKeyboard;

/**
 * Created by purebluesong on 2016/4/25.
 */
public class PinyinEditProcess {
    public int mSelStart;
    public int mSelEnd;
    public int mCandidateStart;
    public int mCandidateEnd;
    private SoftKeyboard softKeyboard;

    public PinyinEditProcess(SoftKeyboard softKeyboard){
        this.softKeyboard = softKeyboard;
    }

    private void commitChanges(int start, int end){
        softKeyboard.preEditPopup.setCursor(start, end);
        softKeyboard.refreshDisplay();
    }
    /**
     * 句子边界编辑处理，也就是游标在句首或者句尾的时候的处理，处理完全就不必向下传递
     * author： purebluesong
     * @return 是否向下传递
     * */
    public boolean borderEditProcess(String s,boolean delete){
        if (delete && (mSelStart <= mCandidateStart || mSelStart > mCandidateEnd)) {
            softKeyboard.sendDownUpKeyEvents(KeyEvent.KEYCODE_DEL);
            softKeyboard.preEditPopup.setCursor(mSelStart -1, mSelEnd -1);
            return true;
        }
        if (mCandidateEnd <= mSelStart) {
            if (delete) {
                Kernel.deleteAction();
            } else {
                Kernel.inputPinyin(s);
            }
            if(mSelEnd !=0)
                softKeyboard.preEditPopup.setCursor(Kernel.getWordsShowPinyin().length());
            softKeyboard.refreshDisplay();
            return true;
        }
        return false;
    }

    private void innerPinyinInsert(InputConnection ic, String sBegin, String sEnd) {
        Log.d("WIVE","pinyin insert"+sBegin+"!!!!!"+sEnd);
        int i,j;
        Kernel.cleanKernel();
        Kernel.inputPinyin(sBegin + sEnd);
        String r = Kernel.getWordsShowPinyin();
        for (i = 0,j = 0; j < sBegin.length() && j < r.length(); i++) {
            if (r.charAt(j) != '\'') j++;
        }
        commitChanges(mCandidateStart+i,mCandidateEnd+i);

        ic.setComposingText(r, 1);
        ic.setSelection(mCandidateStart + i, mCandidateStart + i);
    }

    private void deletePinyinToHead(InputConnection ic){
        Log.d("WIVE","pinyin delete");
        // Kernel.cleanKernel();
        // Kernel.inputPinyin(sEnd);
        commitChanges(0,0);
        ic.setSelection(0, 0);
    }

    private void innerChineseInsert(InputConnection ic,String s) {
        Log.d("WIVE","chinese insert");
        Kernel.inputPinyin(s);
        commitChanges(mSelStart,mSelEnd);
    }

    private void innerChineseDelete(InputConnection ic,String sBegin,String sEnd,int i,int j){
        Log.d("WIVE","chinese delete");
        ic.commitText(sBegin, 1);
        ic.commitText(sEnd.substring(0, j), 1);
        final String sylla = sEnd.substring(j);
        Kernel.cleanKernel();
        Kernel.inputPinyin(sylla);
        String r = Kernel.getWordsShowPinyin();
        ic.setComposingText(r, 1);
        ic.setSelection(mCandidateStart + i + j, mCandidateStart + i + j);
        commitChanges(mCandidateStart + i + j,mCandidateStart + i + j);
    }

    private void borderChinesePinyinInsert(InputConnection ic,String sBefore,String s){
        Log.d("WIVE","border chinese insert");
        ic.commitText(sBefore, 1);
        Kernel.cleanKernel();
        Kernel.inputPinyin(s);
        ic.setSelection(mCandidateStart + 1, mCandidateStart + 1);
        commitChanges(mCandidateStart+1, mCandidateStart+1);
    }

    private void withChineseInsert(InputConnection ic,String sBefore,String sAfter,int i) {
        Log.d("WIVE","with chinese insert");
        ic.commitText(sBefore.substring(0, i), 1);
        Kernel.cleanKernel();
        Kernel.inputPinyin(sBefore.substring(i) + sAfter);
        String r = Kernel.getWordsShowPinyin();
        int k,j;
        for (k = i, j = 0; k < sBefore.length() && j < r.length(); k++) {
            if (r.charAt(j) == '\'') j++;
            j++;
        }
        ic.setComposingText(r, 1);
        ic.setSelection(mCandidateStart + i + j, mCandidateStart + i + j);
        commitChanges(mCandidateStart+i+j, mCandidateEnd+i+j);
    }

    private void withChineseDelete(InputConnection ic, String sBefore, String sAfter, int i) {
        Log.d("WIVE","with chinese delete");

    }

    public void innerEditProcess(InputConnection ic, String sBefore,String s, String sAfter,Boolean delete) {
        int beforeLength = sBefore.length();
        int afterLength = sAfter.length();

        //Character.OTHER_LETTER means chinese character
        if (beforeLength > 0 && Character.getType(sBefore.charAt(0)) != Character.OTHER_LETTER) {
            innerPinyinInsert(ic,sBefore,sAfter);
        } else if ((beforeLength == 0) && (afterLength <= 0 || Character.getType(sAfter.charAt(0)) != Character.OTHER_LETTER)) {
            deletePinyinToHead(ic);
        } else {
            //未上屏字符中有中文
            int i, j;
            //move pointer i to the first english letter or symbol in begin string
            for (i = 0; i < beforeLength && Character.getType(sBefore.charAt(i)) == Character.OTHER_LETTER; i++);
            // 光标前是中文
            if (i == beforeLength || (i == beforeLength - 1 && !delete)) {
                //move the pointer j to the first english letter or symbol in end string
                for (j = 0; j < afterLength && Character.getType(sAfter.charAt(j)) == Character.OTHER_LETTER; j++);

                if (delete) {
                    innerChineseDelete(ic,sBefore,sAfter,i,j);
                } else if (j != 0 ) {// if current cursor is in chinese characters
                    innerChineseInsert(ic,s);
                } else if (beforeLength > 0) {//if before cursor is chinese and after cursor is pinyin
                    borderChinesePinyinInsert(ic,sBefore,s+sAfter);
                }
            } else {
                // 光标前不是汉字
                if(delete){
                    withChineseDelete(ic,sBefore,sAfter,i);
                } else {
                    withChineseInsert(ic,sBefore,sAfter,i);
                }
            }
        }
    }

}
