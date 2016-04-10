package com.hit.wi.util;

/**
 * Created by bahao on 2016/4/10.
 * 工具类，返回几个数的最大最小值
 */
public class WIMath {

    /**
     *
     * @param nums
     * @return 最大值
     */
    public final static int max(int... nums) {
        int maxint;
        maxint = nums[0];
        for (int i = 1; i < nums.length; i++) {
            if (maxint < nums[i]) {
                maxint = nums[i];
            }
        }
        return maxint;
    }

    /**
     *
     * @param nums
     * @return 最小值
     */
    public final static int min(int... nums) {
        int minint;
        minint = nums[0];
        for (int i = 1; i < nums.length; i++) {
            if (minint > nums[i]) {
                minint = nums[i];
            }
        }
        return minint;
    }
}
