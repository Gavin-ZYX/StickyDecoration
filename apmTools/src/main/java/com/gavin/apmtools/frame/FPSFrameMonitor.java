package com.gavin.apmtools.frame;

import android.content.Context;
import android.view.Choreographer;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by gavin
 * date 2018/5/30
 */
public class FPSFrameMonitor {

    private static FPSConfig fpsConfig;

    private static FPSFrameCallback fpsFrameCallback;

    public FPSFrameMonitor() {
        fpsConfig = new FPSConfig();
    }

    public void init(Context context, FrameDataCallback callback) {
        // getInstance our choreographer callback and register it
        fpsFrameCallback = new FPSFrameCallback(fpsConfig, callback);
        Choreographer.getInstance().postFrameCallback(fpsFrameCallback);
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        fpsConfig.deviceRefreshRateInMs = 1000f / display.getRefreshRate();
        fpsConfig.refreshRate = display.getRefreshRate();
    }
}
