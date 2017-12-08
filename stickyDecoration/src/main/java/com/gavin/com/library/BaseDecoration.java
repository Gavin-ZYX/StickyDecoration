package com.gavin.com.library;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.gavin.com.library.listener.OnGroupClickListener;

/**
 * Created by gavin
 * Created date 17/7/1
 * Created log  BaseDecoration
 */

public abstract class BaseDecoration extends RecyclerView.ItemDecoration {

    @ColorInt
    int mGroupBackground = Color.parseColor("#48BDFF");//group背景色，默认透明
    int mGroupHeight = 120;  //悬浮栏高度
    boolean isAlignLeft = true; //是否靠左边
    @ColorInt
    int mDivideColor = Color.parseColor("#CCCCCC");//分割线颜色，默认灰色
    int mDivideHeight = 0;      //分割线宽度

    Paint mDividePaint;
    /**
     * 缓存分组第一个item的position
     */
    private SparseIntArray firstInGroupCash = new SparseIntArray(100);

    private OnGroupClickListener mOnGroupClickListener;

    public BaseDecoration() {
        mDividePaint = new Paint();
        mDividePaint.setColor(mDivideColor);
    }

    /**
     * 设置点击事件
     *
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
        RecyclerView.LayoutManager manager = parent.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            //网格布局
            int spanCount = ((GridLayoutManager) manager).getSpanCount();
            if (isFirstLineInGroup(pos, spanCount) && groupId != null) {
                //新group的第一行都需要留出空间
                outRect.top = mGroupHeight; //为悬浮view预留空间
            }
        } else {
            //非网格布局都默认的线性布局
            //线性布局
            if (groupId == null) {
                return;
            }
            //只有是同一组的第一个才显示悬浮栏
            if (isFirstInGroup(pos)) {
                outRect.top = mGroupHeight; //为悬浮view预留空间
            } else {
                outRect.top = mDivideHeight; //为分割线预留空间
            }
        }
    }

    /**
     * 判断是不是组中的第一个位置
     * 根据前一个组名，判断当前是否为新的组
     */
    private boolean isFirstInGroup(int pos) {
        if (pos == 0) {
            return true;
        } else {
            String preGroupId = getGroupName(pos - 1);
            String curGroupId = getGroupName(pos);
            return !TextUtils.equals(preGroupId, curGroupId);
        }
    }

    /**
     * 判断是不是新组的第一行（GridLayoutManager使用）
     * 利用当前行的第一个对比前一个组名，判断当前是否为新组的第一样
     */
    private boolean isFirstLineInGroup(int pos, int spanCount) {
        if (pos == 0) {
            return true;
        } else {
            int posFirstInGroup = getFirstInGroupWithCash(pos);
            if (pos - posFirstInGroup < spanCount) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 网格布局需要调用
     * @param recyclerView
     * @param gridLayoutManager
     */
    public void resetSpan(RecyclerView recyclerView, GridLayoutManager gridLayoutManager) {
        if (recyclerView == null) {
            throw new NullPointerException("recyclerView not allow null");
        }
        if (gridLayoutManager == null) {
            throw new NullPointerException("gridLayoutManager not allow null");
        }
        final int spanCount = gridLayoutManager.getSpanCount();
        GridLayoutManager.SpanSizeLookup lookup = new GridLayoutManager.SpanSizeLookup() {//相当于weight
            @Override
            public int getSpanSize(int position) {
                int span;
                String curGroupId = getGroupName(position);
                String nextGroupId;
                try {
                    //防止外面没判断，导致越界
                    nextGroupId = getGroupName(position + 1);
                } catch (Exception e) {
                    nextGroupId = curGroupId;
                }
                if (!TextUtils.equals(curGroupId, nextGroupId)) {
                    //为本行的最后一个
                    int posFirstInGroup = getFirstInGroupWithCash(position);
                    span = spanCount - (position - posFirstInGroup) % spanCount;
                } else {
                    span = 1;
                }
                return span;
            }
        };
        gridLayoutManager.setSpanSizeLookup(lookup);
    }

    /**
     * 得到当前分组第一个item的position
     *
     * @param position position
     */
    protected int getFirstInGroupWithCash(int position) {
        if (firstInGroupCash.get(position) == 0) {
            int firstPosition = getFirstInGroup(position);
            firstInGroupCash.put(position, firstPosition);
            return firstPosition;
        } else {
            return firstInGroupCash.get(position);
        }
    }

    /**
     * 得到当前分组第一个item的position
     *
     * @param position position
     */
    private int getFirstInGroup(int position) {
        if (position == 0) {
            return 0;
        } else {
            if (!TextUtils.equals(getGroupName(position), getGroupName(position - 1))) {
                return position;
            } else {
                return getFirstInGroup(position - 1);
            }
        }
    }


    /**
     * 判断自己是否为group的最后一行
     *
     * @param recyclerView recyclerView
     * @param position     position
     * @return
     */
    protected boolean isLastLineInGroup(RecyclerView recyclerView, int position) {
        String curGroupName = getGroupName(position);
        String nextGroupName;
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        //默认往下查找的数量
        int findCount = 1;
        if (manager instanceof GridLayoutManager) {
            int spanCount = ((GridLayoutManager) manager).getSpanCount();
            int firstPositioninGroup = getFirstInGroupWithCash(position);
            findCount = spanCount - (position - firstPositioninGroup) % spanCount;
        }
        try {
            nextGroupName = getGroupName(position + findCount);
        } catch (Exception e) {
            nextGroupName = curGroupName;
        }
        return !TextUtils.equals(curGroupName, nextGroupName);
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
     * 获取分组名
     *
     * @param position position
     * @return group
     */
    abstract String getGroupName(int position);

    /**
     * 点击事件调用
     *
     * @param position position
     */
    private void onGroupClick(int position) {
        if (mOnGroupClickListener != null) {
            mOnGroupClickListener.onClick(position);
        }
    }

    /**
     * 记录每个头部和悬浮头部的坐标信息【用于点击事件】
     * 位置由子类添加
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

}
