package com.gavin.apmtools.net;

import android.annotation.TargetApi;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.os.Build;

/**
 * Created by gavin
 * date 2018/5/30
 */
public class NetUtilAboveM {

    @TargetApi(Build.VERSION_CODES.M)
    public NetUtilAboveM(Context context) {
        NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(context.NETWORK_STATS_SERVICE);
    }
}
