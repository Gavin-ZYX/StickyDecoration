package com.gavin.apmtools.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Application;
import android.app.Service;
import android.graphics.PixelFormat;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.gavin.apmtools.R;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by gavin
 * date 2018/5/30
 */
public class MonitorView {
    private View mMonitorView;
    private View mFrameStatusView;
    private TextView mFrameValueView;
    private View mCPUStatusView;
    private TextView mCPUValueView;
    private View mMemStatusView;
    private TextView mMemValueView;
    private View mNetStatusView;
    private TextView mNetValueView;
    private TextView mNetTotalView;
    private final WindowManager windowManager;
    private int shortAnimationDuration = 200, longAnimationDuration = 700;

    private GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            hide(false);
            return super.onDoubleTap(e);
        }
    };

    public MonitorView(Application context) {
        mMonitorView = LayoutInflater.from(context).inflate(R.layout.monitor_view, null);
        mFrameStatusView = mMonitorView.findViewById(R.id.v_frame);
        mFrameValueView = (TextView) mMonitorView.findViewById(R.id.tv_frame);
        mCPUStatusView = mMonitorView.findViewById(R.id.v_cpu);
        mCPUValueView = (TextView) mMonitorView.findViewById(R.id.tv_cpu);
        mMemStatusView = mMonitorView.findViewById(R.id.v_mem);
        mMemValueView = (TextView) mMonitorView.findViewById(R.id.tv_mem);
        mNetStatusView = mMonitorView.findViewById(R.id.v_net);
        mNetValueView = (TextView) mMonitorView.findViewById(R.id.tv_net);
        mNetTotalView = (TextView) mMonitorView.findViewById(R.id.tv_net_total);
        // grab window manager and add view to the window
        windowManager = (WindowManager) mMonitorView.getContext().getSystemService(Service.WINDOW_SERVICE);
        addViewToWindow();
    }

    private void addViewToWindow() {
        int permissionFlag = PermissionCompat.getFlag();

        WindowManager.LayoutParams paramsF = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                permissionFlag,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        paramsF.x = 100;
        paramsF.y = 500;
        paramsF.gravity = Gravity.TOP | Gravity.START;

        windowManager.addView(mMonitorView, paramsF);

        GestureDetector gestureDetector = new GestureDetector(mMonitorView.getContext(), simpleOnGestureListener);

        mMonitorView.setOnTouchListener(new MonitorTouchListener(paramsF, windowManager, gestureDetector));
        mMonitorView.setHapticFeedbackEnabled(false);
        mMonitorView.setAlpha(0f);
    }

    /**
     * 设置帧数据
     *
     * @param dataSet
     */
    public void setFrameData(List<Long> dataSet) {
        if (dataSet == null) {
            return;
        }
        mFrameValueView.setText(String.valueOf(dataSet.size()));
    }

    /**
     * CPU
     *
     * @param percent
     */
    public void setCpuData(double percent) {
        mCPUValueView.setText(formatValue(percent)+ "%");
    }

    /**
     * Memory
     *
     * @param value
     */
    public void setMemData(double value) {
        // 0x400L  byte -> k
        String mem = formatValue(value / 0X400L) + "m";
        mMemValueView.setText(mem);
    }

    /**
     * Net
     *
     * @param value
     */
    public void setNetData(double value) {
        String mem = formatValue(value / 0X400L) + "k/s";
        mNetValueView.setText(mem);
    }

    /**
     * 流量总数
     * @param value
     */
    public void setNetTotalData(double value) {
        // 0x100000L  byte -> MebiByte
        String mem = formatValue(value / 0x100000L) + "m";
        mNetTotalView.setText(mem);
    }

    public void destroy() {
        mMonitorView.setOnTouchListener(null);
        hide(true);
    }

    public void show() {
        mMonitorView.setVisibility(View.VISIBLE);
        mMonitorView.animate()
                .alpha(1f)
                .setDuration(longAnimationDuration)
                .setListener(null);
    }

    public void hide(final boolean remove) {
        mMonitorView.animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mMonitorView.setVisibility(View.GONE);
                        if (remove) {
                            windowManager.removeView(mMonitorView);
                        }
                    }
                });

    }

    private String formatValue(double value) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        return decimalFormat.format(value);
    }
}
