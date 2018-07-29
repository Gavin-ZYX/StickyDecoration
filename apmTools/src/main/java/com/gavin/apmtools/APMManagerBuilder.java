package com.gavin.apmtools;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Debug;
import android.os.Process;
import android.provider.Settings;

import com.gavin.apmtools.cpu.CpuUtil2;
import com.gavin.apmtools.frame.FPSFrameMonitor;
import com.gavin.apmtools.frame.Foreground;
import com.gavin.apmtools.frame.FrameDataCallback;
import com.gavin.apmtools.net.NetUtil;
import com.gavin.apmtools.ui.MonitorView;

import java.util.List;

/**
 * Created by gavin
 * date 2018/5/30
 * APM builder
 */
public class APMManagerBuilder {

    private APMTimer apmTimer =new APMTimer();
    private CpuUtil2 mCpuUtil2 = new CpuUtil2();
    private int mPid = Process.myPid();
    private int[] pids = {mPid};
    private int mUid = Process.myUid();
    private NetUtil mNetUtil = new NetUtil(mUid);


    private Activity mContext;
    private MonitorView sMonitorView;

    private FPSFrameMonitor mFPSFrameMonitorBuilder;

    private ActivityManager mActivityManager;

    public APMManagerBuilder(Activity context) {
        mContext = context;
        mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
    }

    /**
     * 所有数据
     *
     * @return
     */
    public APMManagerBuilder showAPM() {
        if (overlayPermRequest(mContext)) {
            return this;
        }
        if (!isInit){
            init();
        }
        sMonitorView.show();
        return this;
    }

    private boolean isInit = false;

    private void init() {
        initView();
        initFPS();
        initCPU();
        initMem();
        initNet();
        isInit = true;
    }

    public void hide() {
        sMonitorView.hide(false);
    }

    /**
     * 初始化view
     */
    public void initView() {
        sMonitorView = new MonitorView((Application) mContext.getApplicationContext());
        Foreground.init((Application) mContext.getApplicationContext()).addListener(foregroundListener);
    }

    private Foreground.Listener foregroundListener = new Foreground.Listener() {
        @Override
        public void onBecameForeground() {
            sMonitorView.show();
        }

        @Override
        public void onBecameBackground() {
            sMonitorView.hide(false);
        }
    };

    /**
     * 初始化 fps
     */
    private void initFPS() {
        if (mFPSFrameMonitorBuilder != null) {
            return;
        }
        mFPSFrameMonitorBuilder = new FPSFrameMonitor();
        mFPSFrameMonitorBuilder.init(mContext, new FrameDataCallback() {
            @Override
            public void getFPS(List<Long> pfs) {
                sMonitorView.setFrameData(pfs);
            }
        });
    }


    /**
     * init cpu
     */
    private void initCPU() {
        apmTimer.addCallback(new TimerCallback() {
            @Override
            public void onCallBack() {
                final double percent = mCpuUtil2.getCurrentCPUUsagePercent(mPid);
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sMonitorView.setCpuData(percent);
                    }
                });
            }
        });
    }

    /**
     * init memory
     */
    private void initMem() {
        apmTimer.addCallback(new TimerCallback() {
            @Override
            public void onCallBack() {
                Debug.MemoryInfo[] memoryInfoArray = mActivityManager.getProcessMemoryInfo(pids);
                Debug.MemoryInfo pidMemoryInfo = memoryInfoArray[0];
                final long mem = pidMemoryInfo.getTotalPrivateDirty();
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sMonitorView.setMemData(mem);
                    }
                });
            }
        });
    }

    /**
     * init memory
     */
    private void initNet() {
        apmTimer.addCallback(new TimerCallback() {
            @Override
            public void onCallBack() {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sMonitorView.setNetData(mNetUtil.getDiffBytes());
                        sMonitorView.setNetTotalData(mNetUtil.getTotalBytes());
                    }
                });
            }
        });
    }

    private boolean overlayPermRequest(Context mContext) {
        boolean permNeeded = false;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(mContext)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + mContext.getPackageName()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                permNeeded = true;
            }
        }
        return permNeeded;
    }
}
