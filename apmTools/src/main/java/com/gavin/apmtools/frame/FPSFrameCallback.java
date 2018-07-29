package com.gavin.apmtools.frame;

import android.view.Choreographer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gavin
 * date 2018/5/30
 */
public class FPSFrameCallback implements Choreographer.FrameCallback {
    private FPSConfig fpsConfig;
    private FrameDataCallback mCallback;
    private List<Long> dataSet; //holds the frame times of the sample set
    private boolean enabled = true;

    private long startSampleTimeInNs = 0;

    public FPSFrameCallback(FPSConfig fpsConfig, FrameDataCallback callback) {
        this.fpsConfig = fpsConfig;
        this.mCallback = callback;
        dataSet = new ArrayList<>();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void doFrame(long frameTimeNanos) {
        if (!enabled) {
            destroy();
            return;
        }
        if (startSampleTimeInNs == 0) {
            startSampleTimeInNs = frameTimeNanos;
        }
        //是否完成一个周期
        boolean aCycleComplete = frameTimeNanos - startSampleTimeInNs > fpsConfig.getSampleTimeInNs();
        if (aCycleComplete) {
            if (fpsConfig.frameDataCallback != null) {
                fpsConfig.frameDataCallback.getFPS(dataSet);
            }
            collectSampleAndSend(frameTimeNanos);
        }
        dataSet.add(frameTimeNanos);
        Choreographer.getInstance().postFrameCallback(this);
    }


    private void collectSampleAndSend(long frameTimeNanos) {
        List<Long> dataSetCopy = new ArrayList<>(dataSet);
        mCallback.getFPS(dataSetCopy);
        dataSet.clear();
        startSampleTimeInNs = frameTimeNanos;
    }

    private void destroy() {
        dataSet.clear();
        fpsConfig = null;
    }
}
