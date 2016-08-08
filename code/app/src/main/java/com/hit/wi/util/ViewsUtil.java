package com.hit.wi.util;

import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.View;

/**
 * Created by daofa on 2016/4/10.
 */
public class ViewsUtil {

    public static final int UP = 0;
    private static final int DOWM = 1;
    public static final int LEFT = 2;
    private static final int RIGHT = 3;
    public static final int ERR = -1;

    public static int computeDirection(float x, float y, int height, int width){
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


    /**
     * 设置View的背景颜色而不改变View的shape
     * todo 这里用强转的时候会报错，暂时没有更好的解决方法所以用了try-catch
     * @param v
     * @param color
     */
    public static void setBackgroundWithGradientDrawable(View v,int color){
        if(v!=null){
            try {
                ((GradientDrawable)v.getBackground()).setColor(color);
            }catch (ClassCastException e){
                v.setDrawingCacheBackgroundColor(color);
                Log.d("WIVE","set bcakground "+e.getMessage());
            }
        }
    }
}
