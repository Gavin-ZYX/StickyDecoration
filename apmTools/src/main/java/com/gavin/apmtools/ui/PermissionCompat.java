package com.gavin.apmtools.ui;

import android.view.WindowManager;

/**
 * Created by gavin
 * date 2018/5/30
 */
public final class PermissionCompat {

    private PermissionCompat() {
    }

    public static int getFlag() {
        int permissionFlag;
        permissionFlag = WindowManager.LayoutParams.TYPE_PHONE;
        return permissionFlag;
    }
}
