package com.hit.wi.util;

import com.hit.wi.ve.values.Global;

/**
 * Created by daofa on 2016/4/10.
 */
public class ViewFuncs {

    public static final int UP = 0;
    private static final int DOWM = 1;
    public static final int LEFT = 2;
    private static final int RIGHT = 3;
    public static final int ERR = -1;

    public static int computePosition(float x, float y, int height, int width){
        if(x<0 && x<y && x+y<height){
            return LEFT;
        } else if(x>width && x+y>width && x>y-height+width) {
            return RIGHT;
        } else if(y<0){
            return UP;
        } else if(y>height){
            return DOWM;
        } else {
            return ERR;
        }
    }
    public static boolean isQK(int keyboard){
        return keyboard == Global.KEYBOARD_EN || keyboard==Global.KEYBOARD_QP;
    }
}
