package com.gavin.apmtools.net;

import android.net.TrafficStats;

/**
 * Created by gavin
 * date 2018/5/30
 */
public class NetUtil {

    private long mBeginTotalTxBytes;
    private long mBeginTotalRxBytes;

    private long mLastTotalTxBytes;
    private long mLastTotalRxBytes;

    private int mUid;

    public NetUtil(int uid) {
        mUid = uid;
        mBeginTotalTxBytes = TrafficStats.getUidTxBytes(mUid);
        mBeginTotalRxBytes = TrafficStats.getUidRxBytes(mUid);
        mLastTotalTxBytes = TrafficStats.getUidTxBytes(mUid);
        mLastTotalRxBytes = TrafficStats.getUidRxBytes(mUid);
    }

    /**
     * 流量差(上传)(接收)
     * 和上一次获取的时候相比
     *
     * @return
     */
    public double getDiffBytes() {
        return getDiffTxBytes() + getDiffRxBytes();
    }

    /**
     * 流量差(上传)
     * 和上一次获取的时候相比
     *
     * @return
     */
    public double getDiffTxBytes() {
        long currentTx = TrafficStats.getUidTxBytes(mUid);
        long diff;
        diff = currentTx - mLastTotalTxBytes;
        mLastTotalTxBytes = currentTx;
        return diff;
    }

    /**
     * 流量差(接收)
     * 和上一次获取的时候相比
     *
     * @return
     */
    public double getDiffRxBytes() {
        long currentRx = TrafficStats.getUidRxBytes(mUid);
        long diff;
        diff = currentRx - mLastTotalRxBytes;
        mLastTotalRxBytes = currentRx;
        return diff;
    }

    /**
     * 本次所消耗的流量(上传)(接收)
     * @return
     */
    public double getTotalBytes() {
        return getTotalTxBytes() + getTotalRxBytes();
    }

    /**
     * 本次所消耗的流量(上传)
     *
     * @return
     */
    public double getTotalTxBytes() {
        long currentTx = TrafficStats.getUidTxBytes(mUid);
        return currentTx - mBeginTotalTxBytes;
    }

    /**
     * 本次所消耗的流量(接收)
     *
     * @return
     */
    public double getTotalRxBytes() {
        long currentRx = TrafficStats.getUidRxBytes(mUid);
        return currentRx - mBeginTotalRxBytes;
    }
}
