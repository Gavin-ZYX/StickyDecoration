package com.gavin.com.library;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.gavin.com.library.listener.OnGroupClickListener;
import com.gavin.com.library.listener.PowerGroupListener;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;

/**
 * Created by gavin
 * Created date 17/5/24
 * View悬浮
 * 利用分割线实现悬浮
 */

public class PowerfulStickyDecoration extends BaseDecoration {


    private Paint mGroutPaint;
    /**
     * 使用软引用缓存图片，防止内存泄露
     */
    private SparseArray<Reference<Bitmap>> mBitmapCache = new SparseArray<>();

    private PowerGroupListener mGroupListener;

    /**
     * 缓存View
     */
    private SparseArray<View> headViewMap = new SparseArray<>();

    private PowerfulStickyDecoration(PowerGroupListener groupListener) {
        super();
        this.mGroupListener = groupListener;
        //设置悬浮栏的画笔---mGroutPaint
        mGroutPaint = new Paint();
        mGroutPaint.setColor(mGroupBackground);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        //绘制
        int itemCount = state.getItemCount();
        int childCount = parent.getChildCount();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        String preGroupName;
        String curGroupName;
        for (int i = 0; i < childCount; i++) {
            View childView = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(childView);
            curGroupName = getGroupName(position);
            if (i == 0) {
                preGroupName = curGroupName;
            } else {
                preGroupName = getGroupName(position - 1);
            }
            boolean isFirstInGroup = i != 0 && TextUtils.equals(curGroupName, preGroupName);
            if (isFirstInGroup || curGroupName == null) {
                //绘制分割线
                if (mDivideHeight != 0) {
                    float bottom = childView.getTop();
                    if (bottom < mGroupHeight) {
                        //高度小于顶部悬浮栏时，跳过绘制
                        continue;
                    }
                    c.drawRect(left, bottom - mDivideHeight, right, bottom, mDividePaint);
                }
            } else {
                int viewBottom = childView.getBottom();
                //top 决定当前顶部第一个悬浮Group的位置
                int bottom = Math.max(mGroupHeight, childView.getTop() + parent.getPaddingTop());
                if (position + 1 < itemCount) {
                    //下一组的第一个View接近头部
                    if (isLastLineInGroup(parent, position) && viewBottom < bottom) {
                        bottom = viewBottom;
                    }
                }
                c.drawRect(left, bottom - mGroupHeight, right, bottom, mGroutPaint);
                //根据position获取View
                View groupView;
                if (headViewMap.get(position) == null) {
                    groupView = getGroupView(position);
                    if (groupView == null) {
                        return;
                    }
                    groupView.setDrawingCacheEnabled(true);
                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(right, mGroupHeight);
                    groupView.setLayoutParams(layoutParams);
                    groupView.setMinimumWidth(right);
                    groupView.measure(
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                    //指定高度、宽度的groupView
                    groupView.layout(0, 0, right, mGroupHeight);
                } else {
                    groupView = headViewMap.get(position);
                }
                Bitmap bitmap;
                if (mBitmapCache.get(position) != null && mBitmapCache.get(position).get() != null) {
                    bitmap = mBitmapCache.get(position).get();
                } else {
                    bitmap = Bitmap.createBitmap(groupView.getDrawingCache());
                    mBitmapCache.put(position, new SoftReference<Bitmap>(bitmap));
                }

                c.drawBitmap(bitmap, left, bottom - mGroupHeight, null);
                //将头部信息放进array，用于处理点击事件
                stickyHeaderPosArray.put(position, bottom);
            }
        }
    }

    /**
     * 获取组名
     *
     * @param position position
     * @return 组名
     */
    @Override
    String getGroupName(int position) {
        if (mGroupListener != null) {
            return mGroupListener.getGroupName(position);
        } else {
            return null;
        }
    }

    /**
     * 获取组View
     *
     * @param position position
     * @return 组名
     */
    private View getGroupView(int position) {
        if (mGroupListener != null) {
            return mGroupListener.getGroupView(position);
        } else {
            return null;
        }
    }

    public static class Builder {
        PowerfulStickyDecoration mDecoration;

        private Builder(PowerGroupListener listener) {
            mDecoration = new PowerfulStickyDecoration(listener);
        }

        public static Builder init(PowerGroupListener listener) {
            return new Builder(listener);
        }

        /**
         * 设置Group高度
         *
         * @param groutHeight 高度
         * @return this
         */
        public Builder setGroupHeight(int groutHeight) {
            mDecoration.mGroupHeight = groutHeight;
            return this;
        }


        /**
         * 设置Group背景
         *
         * @param background 背景色
         */
        public Builder setGroupBackground(@ColorInt int background) {
            mDecoration.mGroupBackground = background;
            mDecoration.mGroutPaint.setColor(mDecoration.mGroupBackground);
            return this;
        }

        /**
         * 设置分割线高度
         *
         * @param height 高度
         * @return this
         */
        public Builder setDivideHeight(int height) {
            mDecoration.mDivideHeight = height;
            return this;
        }

        /**
         * 设置分割线颜色
         *
         * @param color color
         * @return this
         */
        public Builder setDivideColor(@ColorInt int color) {
            mDecoration.mDivideColor = color;
            return this;
        }

        /**
         * 设置点击事件
         *
         * @param listener 点击事件
         * @return this
         */
        public Builder setOnClickListener(OnGroupClickListener listener) {
            mDecoration.setOnGroupClickListener(listener);
            return this;
        }

        /**
         * 重置span
         *
         * @param recyclerView      recyclerView
         * @param gridLayoutManager gridLayoutManager
         * @return this
         */
        public Builder resetSpan(RecyclerView recyclerView, GridLayoutManager gridLayoutManager) {
            mDecoration.resetSpan(recyclerView, gridLayoutManager);
            return this;
        }

        public PowerfulStickyDecoration build() {
            return mDecoration;
        }
    }

    private void l(String message) {
        Log.i("TAG", message);
    }
}