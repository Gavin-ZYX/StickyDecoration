package com.gavin.apmtools;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by gavin
 * date 2018/5/30
 */
public class APMTimer {

    /**
     * 轮询周期
     */
    private final int CYCLE_TIME = 1000;

    private List<TimerCallback> mTimerCallbackList = new ArrayList<>();

    private final Timer timer = new Timer();

    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            for (TimerCallback callback : mTimerCallbackList) {
                callback.onCallBack();
            }
        }
    };

    public APMTimer() {
        timer.schedule(timerTask, 0, CYCLE_TIME);
    }

    public void addCallback(TimerCallback timerCallback) {
        if (timerCallback != null) {
            mTimerCallbackList.add(timerCallback);
        }
    }
}
