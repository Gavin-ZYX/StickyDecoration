package com.gavin.com.library;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.gavin.com.library.listener.OnGroupClickListener;

/**
 * Created by gavin
 * Created date 17/7/1
 * Created log
 */

abstract class BaseDecoration extends RecyclerView.ItemDecoration {

    @ColorInt
    int mGroupBackground = Color.parseColor("#00000000");//group背景色，默认透明
    int mGroupHeight = 80;  //悬浮栏高度
    boolean isAlignLeft = true; //是否靠左边
    @ColorInt
    int mDivideColor = Color.parseColor("#CCCCCC");//分割线颜色，默认灰色
    int mDivideHeight = 0;      //分割线高度

    Paint mDividePaint;

    private OnGroupClickListener mOnGroupClickListener;

    public BaseDecoration() {
        mDividePaint = new Paint();
        mDividePaint.setColor(mDivideColor);
    }

    /**
     * 设置点击事件
     * @param listener
     */
    protected void setOnGroupClickListener(OnGroupClickListener listener) {
        this.mOnGroupClickListener = listener;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int pos = parent.getChildAdapterPosition(view);
        String groupId = getGroupName(pos);
        if (groupId == null) {
            return;
        }
        //只有是同一组的第一个才显示悬浮栏
        if (pos == 0 || isFirstInGroup(pos)) {
            outRect.top = mGroupHeight; //为悬浮view预留空间
        } else {
            outRect.top = mDivideHeight; //为分割线预留空间
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        //点击事件处理
        if (gestureDetector == null) {
            gestureDetector = new GestureDetector(parent.getContext(), gestureListener);
            parent.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetector.onTouchEvent(event);
                }
            });
        }
        stickyHeaderPosArray.clear();
    }

    /**
     * 判断是不是组中的第一个位置
     * 根据前一个组名，判断当前是否为新的组
     */
    private boolean isFirstInGroup(int pos) {
        if (pos == 0) {
            return true;
        } else {
            String prevGroupId = getGroupName(pos - 1);
            String groupId = getGroupName(pos);
            return !TextUtils.equals(prevGroupId, groupId);
        }
    }

    /**
     * 获取分组名
     * @param position position
     * @return
     */
    abstract String getGroupName(int position);

    /**
     * 点击事件调用
     * @param position  position
     */
    private void onGroupClick(int position) {
        if (mOnGroupClickListener != null) {
            mOnGroupClickListener.onClick(position);
        }
    }

    /**
     * 记录每个头部和悬浮头部的坐标信息【用于点击事件】
     */
    protected SparseArray<Integer> stickyHeaderPosArray = new SparseArray<>();
    private GestureDetector gestureDetector;
    private GestureDetector.OnGestureListener gestureListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            for (int i = 0; i < stickyHeaderPosArray.size(); i++) {
                int value = stickyHeaderPosArray.valueAt(i);
                float y = e.getY();
                if (value - mGroupHeight <= y && y <= value) {
                    //如果点击到分组头
                    onGroupClick(stickyHeaderPosArray.keyAt(i));
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    };

    void log(String msg) {
        Log.i("TAG", msg);
    }
}
