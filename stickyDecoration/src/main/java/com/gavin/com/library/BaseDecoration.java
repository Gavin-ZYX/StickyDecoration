package com.gavin.com.library;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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
    // TODO: 2018/4/13 加载更新后闪动

    /**
     * group背景色，默认透明
     */
    @ColorInt
    int mGroupBackground = Color.parseColor("#48BDFF");
    /**
     * 悬浮栏高度
     */
    int mGroupHeight = 120;
    /**
     * 分割线颜色，默认灰色
     */
    @ColorInt
    int mDivideColor = Color.parseColor("#CCCCCC");
    /**
     * 分割线宽度
     */
    int mDivideHeight = 0;

    /**
     * RecyclerView头部数量
     * 最小为0
     */
    int mHeaderCount;

    Paint mDividePaint;
    /**
     * 缓存分组第一个item的position
     */
    private SparseIntArray firstInGroupCash = new SparseIntArray(100);

    protected OnGroupClickListener mOnGroupClickListener;

    protected boolean mSticky = true;

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
     * @param realPosition realPosition
     * @return group
     */
    abstract String getGroupName(int realPosition);

    /**
     * 获取真实的position
     * @param position
     * @return
     */
    protected int getRealPosition(int position) {
        return position - mHeaderCount;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        int realPosition = getRealPosition(position);
        RecyclerView.LayoutManager manager = parent.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            //网格布局
            int spanCount = ((GridLayoutManager) manager).getSpanCount();
            if (!isHeader(realPosition)) {
                if (isFirstLineInGroup(realPosition, spanCount)) {
                    //为悬浮view预留空间
                    outRect.top = mGroupHeight;
                } else {
                    //为分割线预留空间
                    outRect.top = mDivideHeight;
                }
            }
        } else {
            //其他的默认为线性布局
            //只有是同一组的第一个才显示悬浮栏
            if (!isHeader(realPosition)) {
                if (isFirstInGroup(realPosition)) {
                    //为悬浮view预留空间
                    outRect.top = mGroupHeight;
                } else {
                    //为分割线预留空间
                    outRect.top = mDivideHeight;
                }
            }
        }
    }

    /**
     * 判断是不是组中的第一个位置
     * 根据前一个组名，判断当前是否为新的组
     * 当前为groupId为null时，则与上一个为同一组
     */
    protected boolean isFirstInGroup(int realPosition) {
        if (realPosition < 0) {
            //小于header数量，不是第一个
            return false;
        } else if (realPosition == 0) {
            //等于header数量，为第一个
            return true;
        }
        String preGroupId;
        if (realPosition <= 0) {
            preGroupId = null;
        } else {
            preGroupId = getGroupName(realPosition - 1);
        }
        String curGroupId = getGroupName(realPosition);
        if (curGroupId == null) {
            return false;
        }
        return !TextUtils.equals(preGroupId, curGroupId);
    }

    /**
     * 是否在RecyclerView处于第一个（header部分不算）
     *
     * @param realPosition 总的position
     * @param index    RecyclerView中的Index
     * @return
     */
    protected boolean isFirstInRecyclerView(int realPosition, int index) {
        return realPosition >= 0 && index == 0;
    }

    /**
     * 是否为Header
     *
     */
    protected boolean isHeader(int realPosition) {
        return realPosition < 0;
    }


    /**
     * 判断是不是新组的第一行（GridLayoutManager使用）
     * 利用当前行的第一个对比前一个组名，判断当前是否为新组的第一样
     */
    protected boolean isFirstLineInGroup(int realPosition, int spanCount) {
        if (realPosition < 0) {
            //小于header数量，不是第一个
            return false;
        } else if (realPosition == 0) {
            return true;
        } else {
            int posFirstInGroup = getFirstInGroupWithCash(realPosition);
            if (realPosition - posFirstInGroup < spanCount) {
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
        //相当于weight
        GridLayoutManager.SpanSizeLookup lookup = new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int span;
                int realPosition = getRealPosition(position);
                if (realPosition < 0) {
                    //小于header数量
                    span = spanCount;
                } else {
                    String curGroupId = getGroupName(realPosition);
                    String nextGroupId;
                    try {
                        //防止外面没判断，导致越界
                        nextGroupId = getGroupName(realPosition + 1);
                    } catch (Exception e) {
                        nextGroupId = curGroupId;
                    }
                    if (!TextUtils.equals(curGroupId, nextGroupId)) {
                        //为本行的最后一个
                        int posFirstInGroup = getFirstInGroupWithCash(realPosition);
                        span = spanCount - (realPosition - posFirstInGroup) % spanCount;
                    } else {
                        span = 1;
                    }
                }
                return span;
            }
        };
        gridLayoutManager.setSpanSizeLookup(lookup);
    }

    /**
     * down事件在顶部悬浮栏中
     */
    private boolean mDownInHeader;

    /**
     * RecyclerView onInterceptEvent中down事件调用，用于处理点击穿透问题
     *
     * @param event
     */
    public void onEventDown(MotionEvent event) {
        if (event == null) {
            mDownInHeader = false;
            return;
        }
        mDownInHeader = event.getY() > 0 && event.getY() < mGroupHeight;
    }

    /**
     * RecyclerView onInterceptEvent中up事件调用，用于处理点击穿透问题
     *
     * @param event
     * @return
     */
    public boolean onEventUp(MotionEvent event) {
        if (mDownInHeader) {
            float y = event.getY();
            boolean isInHeader = y > 0 && y < mGroupHeight;
            if (isInHeader) {
                return onTouchEvent(event);
            }
        }
        return false;
    }

    /**
     * 得到当前分组第一个item的position
     *
     * @param realPosition realPosition
     */
    protected int getFirstInGroupWithCash(int realPosition) {
        return getFirstInGroup(realPosition);
    }

    /**
     * 得到当前分组第一个item的position
     *
     * @param realPosition realPosition
     */
    private int getFirstInGroup(int realPosition) {
        if (realPosition <= 0) {
            return 0;
        } else {
            if (isFirstInGroup(realPosition)) {
                return realPosition;
            } else {
                return getFirstInGroup(realPosition - 1);
            }
        }
    }


    /**
     * 判断自己是否为group的最后一行
     *
     * @param recyclerView recyclerView
     * @param realPosition     realPosition
     * @return
     */
    protected boolean isLastLineInGroup(RecyclerView recyclerView, int realPosition) {
        if (realPosition < 0) {
            return true;
        } else {
            String curGroupName = getGroupName(realPosition);
            String nextGroupName;
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            //默认往下查找的数量
            int findCount = 1;
            if (manager instanceof GridLayoutManager) {
                int spanCount = ((GridLayoutManager) manager).getSpanCount();
                int firstPositionInGroup = getFirstInGroupWithCash(realPosition);
                findCount = spanCount - (realPosition - firstPositionInGroup) % spanCount;
            }
            try {
                nextGroupName = getGroupName(realPosition + findCount);
            } catch (Exception e) {
                nextGroupName = curGroupName;
            }
            if (nextGroupName == null) {
                return true;
            }
            return !TextUtils.equals(curGroupName, nextGroupName);
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
     * 点击事件调用
     *
     * @param realPosition realPosition
     */
    private void onGroupClick(int realPosition, int viewId) {
        if (mOnGroupClickListener != null) {
            mOnGroupClickListener.onClick(realPosition, viewId);
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
            return onTouchEvent(e);
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
     * 对touch事件处理，找到点击事件
     *
     * @param e
     * @return
     */
    private boolean onTouchEvent(MotionEvent e) {
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

    /**
     * 绘制分割线
     *
     * @param c
     * @param parent
     * @param childView
     * @param realPosition
     * @param left
     * @param right
     */
    protected void drawDivide(Canvas c, RecyclerView parent, View childView, int realPosition, int left, int right) {
        if (mDivideHeight != 0 && !isHeader(realPosition)) {
            RecyclerView.LayoutManager manager = parent.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                int spanCount = ((GridLayoutManager) manager).getSpanCount();
                if (!isFirstLineInGroup(realPosition, spanCount)) {
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

    protected void log(String content) {
        if (false) {
            Log.i("StickDecoration", content);
        }
    }

}
