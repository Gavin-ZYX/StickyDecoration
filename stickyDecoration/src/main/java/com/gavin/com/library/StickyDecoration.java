package com.gavin.com.library;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;

import com.gavin.com.library.listener.GroupListener;

/**
 * Created by gavin
 * Created date 17/5/24
 * 文字悬浮
 * 利用分割线实现悬浮功能
 */

public class StickyDecoration extends RecyclerView.ItemDecoration {
    @ColorInt
    private int mGroupBackground = Color.GRAY;//group背景色，默认灰色
    @ColorInt
    private int mGroupTextColor = Color.WHITE;//字体颜色，默认白色
    private int mGroupHeight = 80;  //悬浮栏高度
    private int mLeftMargin = 10;   //文字距离左边的距离
    private int mTextSize = 40;     //字体大小
    private GroupListener mGroupListener;

    private TextPaint mTextPaint;
    private Paint mGroutPaint;

    private StickyDecoration(GroupListener groupListener) {
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
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int pos = parent.getChildAdapterPosition(view);
        String groupId = getGroupName(pos);
        if (groupId == null) return;
        //只有是同一组的第一个才显示悬浮栏
        if (pos == 0 || isFirstInGroup(pos)) {
            outRect.top = mGroupHeight;
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        final int itemCount = state.getItemCount();
        final int childCount = parent.getChildCount();
        final int left = parent.getLeft() + parent.getPaddingLeft();
        final int right = parent.getRight() - parent.getPaddingRight();
        String preGroupName;      //标记上一个item对应的Group
        String currentGroupName = null;       //当前item对应的Group
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(view);
            preGroupName = currentGroupName;
            currentGroupName = getGroupName(position);
            if (currentGroupName == null || TextUtils.equals(currentGroupName, preGroupName))
                continue;
            int viewBottom = view.getBottom();
            float bottom = Math.max(mGroupHeight, view.getTop());//决定当前顶部第一个悬浮Group的bottom
            if (position + 1 < itemCount) {
                //获取下个GroupName
                String nextGroupName = getGroupName(position + 1);
                //下一组的第一个View接近头部
                if (!currentGroupName.equals(nextGroupName) && viewBottom < bottom) {
                    bottom = viewBottom;
                }
            }
            //根据top绘制group
            c.drawRect(left, bottom - mGroupHeight, right, bottom, mGroutPaint);
            Paint.FontMetrics fm = mTextPaint.getFontMetrics();
            //文字竖直居中显示
            float baseLine = bottom - (mGroupHeight - (fm.bottom - fm.top)) / 2 - fm.bottom;
            c.drawText(currentGroupName, left + mLeftMargin, baseLine, mTextPaint);
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
            String prevGroupId = getGroupName(pos - 1);
            String groupId = getGroupName(pos);
            return !TextUtils.equals(prevGroupId, groupId);
        }
    }

    /**
     * 获取组名
     *
     * @param position position
     * @return 组名
     */
    private String getGroupName(int position) {
        if (mGroupListener != null) {
            return mGroupListener.getGroupName(position);
        } else {
            return null;
        }
    }

    public static class Builder {
        private StickyDecoration mStickyDecoration;

        private Builder(GroupListener groupListener) {
            mStickyDecoration = new StickyDecoration(groupListener);
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
            mStickyDecoration.mGroupBackground = background;
            mStickyDecoration.mGroutPaint.setColor(mStickyDecoration.mGroupBackground);
            return this;
        }

        /**
         * 设置字体大小
         *
         * @param textSize 字体大小
         */
        public Builder setGroupTextSize(int textSize) {
            mStickyDecoration.mTextSize = textSize;
            mStickyDecoration.mTextPaint.setTextSize(mStickyDecoration.mTextSize);
            return this;
        }

        /**
         * 设置Group高度
         *
         * @param groutHeight 高度
         * @return this
         */
        public Builder setGroupHeight(int groutHeight) {
            mStickyDecoration.mGroupHeight = groutHeight;
            return this;
        }

        /**
         * 组TextColor
         *
         * @param color 颜色
         * @return this
         */
        public Builder setGroupTextColor(@ColorInt int color) {
            mStickyDecoration.mGroupTextColor = color;
            return this;
        }

        /**
         * 设置左边距
         *
         * @param leftMargin 左边距
         * @return this
         */
        public Builder setTextLeftMargin(int leftMargin) {
            mStickyDecoration.mLeftMargin = leftMargin;
            return this;
        }

        public StickyDecoration build() {
            return mStickyDecoration;
        }
    }

}