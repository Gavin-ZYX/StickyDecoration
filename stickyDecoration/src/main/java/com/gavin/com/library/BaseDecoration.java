package com.gavin.com.library;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.gavin.com.library.listener.OnGroupClickListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gavin
 * Created date 17/7/1
 * Created log  BaseDecoration
 */

public abstract class BaseDecoration extends RecyclerView.ItemDecoration {

    @ColorInt
    int mGroupBackground = Color.parseColor("#48BDFF");//group背景色，默认透明
    int mGroupHeight = 120;  //悬浮栏高度
    @ColorInt
    int mDivideColor = Color.parseColor("#CCCCCC");//分割线颜色，默认灰色
    int mDivideHeight = 0;      //分割线宽度

    Paint mDividePaint;
    /**
     * 缓存分组第一个item的position
     */
    private SparseIntArray firstInGroupCash = new SparseIntArray(100);

    protected OnGroupClickListener mOnGroupClickListener;

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
     * 获取分组名
     *
     * @param position position
     * @return group
     */
    abstract String getGroupName(int position);

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int pos = parent.getChildAdapterPosition(view);
        RecyclerView.LayoutManager manager = parent.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            //网格布局
            int spanCount = ((GridLayoutManager) manager).getSpanCount();
            if (isFirstLineInGroup(pos, spanCount)) {
                //为悬浮view预留空间
                outRect.top = mGroupHeight;
            } else {
                //为分割线预留空间
                outRect.top = mDivideHeight;
            }
        } else {
            //其他的默认为线性布局
            //只有是同一组的第一个才显示悬浮栏
            if (isFirstInGroup(pos)) {
                //为悬浮view预留空间
                outRect.top = mGroupHeight;
            } else {
                //为分割线预留空间
                outRect.top = mDivideHeight;
            }
        }
    }

    /**
     * 判断是不是组中的第一个位置
     * 根据前一个组名，判断当前是否为新的组
     * 当前为groupId为null时，则与上一个为同一组
     */
    protected boolean isFirstInGroup(int position) {
        String preGroupId;
        if (position == 0) {
            preGroupId = null;
        } else {
            preGroupId = getGroupName(position - 1);
        }
        String curGroupId = getGroupName(position);
        if (curGroupId == null) {
            return false;
        }
        return !TextUtils.equals(preGroupId, curGroupId);
    }

    /**
     * 判断是不是新组的第一行（GridLayoutManager使用）
     * 利用当前行的第一个对比前一个组名，判断当前是否为新组的第一样
     */
    protected boolean isFirstLineInGroup(int pos, int spanCount) {
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
     *
     * @param recyclerView      recyclerView
     * @param gridLayoutManager gridLayoutManager
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
            if (isFirstInGroup(position)) {
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
            int firstPositionInGroup = getFirstInGroupWithCash(position);
            findCount = spanCount - (position - firstPositionInGroup) % spanCount;
        }
        try {
            nextGroupName = getGroupName(position + findCount);
        } catch (Exception e) {
            nextGroupName = curGroupName;
        }
        if (nextGroupName == null) {
            return false;
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
     * 点击事件调用
     *
     * @param position position
     */
    private void onGroupClick(int position, int viewId) {
        if (mOnGroupClickListener != null) {
            mOnGroupClickListener.onClick(position, viewId);
        }
    }

    /**
     * 记录每个头部和悬浮头部的坐标信息【用于点击事件】
     * 位置由子类添加
     */
    protected HashMap<Integer, ClickInfo> stickyHeaderPosArray = new HashMap<>();
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
            for (Map.Entry<Integer, ClickInfo> entry : stickyHeaderPosArray.entrySet()) {

                ClickInfo value = stickyHeaderPosArray.get(entry.getKey());
                float y = e.getY();
                float x = e.getX();
                if (value.mBottom - mGroupHeight <= y && y <= value.mBottom) {
                    //如果点击到分组头
                    if (value.mDetailInfoList == null || value.mDetailInfoList.size() == 0) {
                        //没有子View的点击事件
                        onGroupClick(entry.getKey(), value.mGroupId);
                    } else {
                        List<ClickInfo.DetailInfo> list = value.mDetailInfoList;
                        boolean isChildViewClicked = false;
                        for (ClickInfo.DetailInfo detailInfo : list) {
                            if (detailInfo.top <= y && y <= detailInfo.bottom
                                    && detailInfo.left <= x && detailInfo.right >= x) {
                                //当前view被点击
                                onGroupClick(entry.getKey(), detailInfo.id);
                                isChildViewClicked = true;
                                break;
                            }
                        }
                        if (!isChildViewClicked) {
                            //点击范围不在带有id的子view中，则表示整个groupView被点击
                            onGroupClick(entry.getKey(), value.mGroupId);
                        }

                    }
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

    /**
     * 绘制分割线
     *
     * @param c
     * @param parent
     * @param childView
     * @param position
     * @param left
     * @param right
     */
    protected void drawDivide(Canvas c, RecyclerView parent, View childView, int position, int left, int right) {
        if (mDivideHeight != 0) {
            RecyclerView.LayoutManager manager = parent.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                int spanCount = ((GridLayoutManager) manager).getSpanCount();
                if (!isFirstLineInGroup(position, spanCount)) {
                    float bottom = childView.getTop() + parent.getPaddingTop();
                    //高度小于顶部悬浮栏时，跳过绘制
                    if (bottom >= mGroupHeight) {
                        c.drawRect(left, bottom - mDivideHeight, right, bottom, mDividePaint);
                    }
                }
            } else {
                float bottom = childView.getTop();
                //高度小于顶部悬浮栏时，跳过绘制
                if (bottom >= mGroupHeight) {
                    c.drawRect(left, bottom - mDivideHeight, right, bottom, mDividePaint);
                }
            }
        }
    }

}
