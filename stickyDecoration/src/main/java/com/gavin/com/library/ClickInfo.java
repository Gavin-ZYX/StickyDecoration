package com.gavin.com.library;

import android.view.View;

import java.util.List;

/**
 * Created by gavin
 * date 2018/3/31
 * 点击模型 保存id 以及坐标信息
 * 用于处理整个Item中的点击事件分发
 */

public class ClickInfo {

    public int mBottom;
    public int mGroupId = View.NO_ID;

    /**
     * 详细信息
     * 对应的子view坐标信息
     */
    public List<DetailInfo> mDetailInfoList;

    public ClickInfo(int bottom) {
        this.mBottom = bottom;
    }

    public ClickInfo(int bottom, List<DetailInfo> detailInfoList) {
        mBottom = bottom;
        mDetailInfoList = detailInfoList;
    }

    public static class DetailInfo {

        public int id;
        public int left;
        public int right;
        public int top;
        public int bottom;

        public DetailInfo(int id, int left, int right, int top, int bottom) {
            this.id = id;
            this.left = left;
            this.right = right;
            this.top = top;
            this.bottom = bottom;
        }
    }

}
