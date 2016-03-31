package com.hit.wi.t9.datastruct;

/**
 * Created by admin on 2016/3/20.
 */
public class InputAction {
    public static final int TEXT_TO_KERNEL = 0x00;
    public static final int TEXT_TO_INPUTCONNECTION = 0x01;

    public CharSequence text;
    public int Type;

    public InputAction(CharSequence text,int Type){
        this.text = text;
        this.Type = Type;
    }
}
