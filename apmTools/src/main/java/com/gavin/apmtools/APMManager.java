package com.gavin.apmtools;

import android.app.Activity;

/**
 * Created by gavin
 * date 2018/5/30
 */
public class APMManager {
    private static APMManagerBuilder sManagerBuilder;

    public static APMManagerBuilder getInstance(Activity context) {
        if (sManagerBuilder == null) {
            sManagerBuilder = new APMManagerBuilder(context);
        }
        return sManagerBuilder;
    }
}
