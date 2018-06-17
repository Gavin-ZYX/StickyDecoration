package com.gavin.com.library;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.View;

import com.gavin.com.library.listener.GroupListener;
import com.gavin.com.library.listener.OnGroupClickListener;

/**
 * Created by gavin
 * Created date 17/5/24
 * 文字悬浮
 * 利用分割线实现悬浮功能
 */

public class StickyDecoration extends BaseDecoration {
    @ColorInt
    private int mGroupTextColor = Color.WHITE;//字体颜色，默认黑色
    private int mSideMargin = 10;   //边距 左边距
    private int mTextSize = 50;     //字体大小
    private GroupListener mGroupListener;

    private TextPaint mTextPaint;
    private Paint mGroutPaint;

    private StickyDecoration(GroupListener groupListener) {
        super();
        this.mGroupListener = groupListener;
        //设置悬浮栏的画笔---mGroutPaint
        mGroutPaint = new Paint();
        mGroutPaint.setColor(mGroupBackground);
        //设置悬浮栏中文本的画笔
        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mGroupTextColor);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        final int itemCount = state.getItemCount();
        final int childCount = parent.getChildCount();
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        for (int i = 0; i < childCount; i++) {
            View childView = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(childView);
            if (isFirstInGroup(position) || isFirstInRecyclerView(position, i)) {
                //绘制悬浮
                int bottom = Math.max(mGroupHeight, (childView.getTop() + parent.getPaddingTop()));//决定当前顶部第一个悬浮Group的bottom
                if (position + 1 < itemCount) {
                    //下一组的第一个View接近头部
                    int viewBottom = childView.getBottom();
                    if (isLastLineInGroup(parent, position) && viewBottom < bottom) {
                        bottom = viewBottom;
                    }
                }
                drawDecoration(c, position, left, right, bottom);
                if (mOnGroupClickListener != null) {
                    stickyHeaderPosArray.put(position, new ClickInfo(bottom));
                }
            } else {
                //绘制分割线
                drawDivide(c, parent, childView, position, left, right);
            }
        }
    }


    /**
     * 绘制悬浮框
     *
     * @param c
     * @param position
     * @param left
     * @param right
     * @param bottom
     */
    private void drawDecoration(Canvas c, int position, int left, int right, int bottom) {
        String curGroupName;       //当前item对应的Group
        int firstPositionInGroup = getFirstInGroupWithCash(position);
        curGroupName = getGroupName(firstPositionInGroup);
        //根据top绘制group背景
        c.drawRect(left, bottom - mGroupHeight, right, bottom, mGroutPaint);
        if (curGroupName == null) {
            return;
        }
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        //文字竖直居中显示
        float baseLine = bottom - (mGroupHeight - (fm.bottom - fm.top)) / 2 - fm.bottom;
        //获取文字宽度
        mSideMargin = Math.abs(mSideMargin);
        c.drawText(curGroupName, left + mSideMargin, baseLine, mTextPaint);
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


    public static class Builder {
        private StickyDecoration mDecoration;

        private Builder(GroupListener groupListener) {
            mDecoration = new StickyDecoration(groupListener);
        }

        public static Builder init(GroupListener groupListener) {
            return new Builder(groupListener);
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
         * 设置字体大小
         *
         * @param textSize 字体大小
         */
        public Builder setGroupTextSize(int textSize) {
            mDecoration.mTextSize = textSize;
            mDecoration.mTextPaint.setTextSize(mDecoration.mTextSize);
            return this;
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
         * 组TextColor
         *
         * @param color 颜色
         * @return this
         */
        public Builder setGroupTextColor(@ColorInt int color) {
            mDecoration.mGroupTextColor = color;
            mDecoration.mTextPaint.setColor(mDecoration.mGroupTextColor);
            return this;
        }

        /**
         * 设置边距
         * 靠左时为左边距  靠右时为右边距
         *
         * @param leftMargin 左右距离
         * @return this
         */
        public Builder setTextSideMargin(int leftMargin) {
            mDecoration.mSideMargin = leftMargin;
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
            mDecoration.mDividePaint.setColor(color);
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

        /**
         * 设置头部数量
         * 用于顶部Header不需要设置悬浮的情况（仅LinearLayoutManager）
         * @param headerCount
         * @return
         */
        public Builder setHeaderCount(int headerCount) {
            if (headerCount >= 0) {
                mDecoration.mHeaderCount = headerCount;
            }
            return this;
        }

        public StickyDecoration build() {
            return mDecoration;
        }
    }

}