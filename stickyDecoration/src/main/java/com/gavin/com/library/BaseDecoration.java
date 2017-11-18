package com.gavin.com.library;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.gavin.com.library.listener.OnGroupClickListener;

/**
 * Created by gavin
 * Created date 17/7/1
 * Created log
 */

public abstract class BaseDecoration extends RecyclerView.ItemDecoration {
    public static final int LINEAR = 0x01;
    public static final int GRID = 0x02;

    @ColorInt
    int mGroupBackground = Color.parseColor("#00000000");//group背景色，默认透明
    int mGroupHeight = 80;  //悬浮栏高度
    boolean isAlignLeft = true; //是否靠左边
    @ColorInt
    int mDivideColor = Color.parseColor("#CCCCCC");//分割线颜色，默认灰色
    int mDivideHeight = 0;      //分割线高度
    private int mLayoutManager = LINEAR; //排列方式 默认线性布局

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

    /**
     * 设置布局类型
     *
     * @param layoutManager
     */
    public void setLayoutManager(int layoutManager) {
        this.mLayoutManager = layoutManager;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int pos = parent.getChildAdapterPosition(view);
        String groupId = getGroupName(pos);
        switch (mLayoutManager) {
            case LINEAR:
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
                break;
            case GRID:
                //网格布局
                RecyclerView.LayoutManager manager = parent.getLayoutManager();
                if (manager instanceof GridLayoutManager) {
                    int spanCount = ((GridLayoutManager) manager).getSpanCount();
                    if (isFirstLineInGroup(pos, spanCount)) {
                        //新group的第一行都需要留出空间
                        outRect.top = mGroupHeight; //为悬浮view预留空间
                    }
                    setSpan(parent, spanCount);
                } else {
                    //报错
                    Log.e("StickyDecoration", "布局类型错误");
                }
                break;

            default:
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

    private GridLayoutManager.SpanSizeLookup lookup;

    private void setSpan(RecyclerView parent, final int spanCount) {
        if (lookup == null) {
            lookup = new GridLayoutManager.SpanSizeLookup() {//相当于weight
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
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) parent.getLayoutManager();
            gridLayoutManager.setSpanSizeLookup(lookup);
        }
    }

    /**
     * 得到当前分组第一个item的position
     *
     * @param position position
     */
    private int getFirstInGroupWithCash(int position) {
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
