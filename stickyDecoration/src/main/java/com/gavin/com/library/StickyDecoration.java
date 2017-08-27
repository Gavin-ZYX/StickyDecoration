package com.gavin.com.library;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

public class StickyDecoration extends BaseDecoration {
    @ColorInt
    private int mGroupTextColor = Color.WHITE;//字体颜色，默认白色
    private int mSideMargin = 10;   //边距 靠左时为左边距  靠右时为右边距
    private int mTextSize = 40;     //字体大小
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

        String preGroupName;      //标记上一个item对应的Group
        String currentGroupName = null;       //当前item对应的Group
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(view);
            preGroupName = currentGroupName;
            currentGroupName = getGroupName(position);

            if (currentGroupName == null || TextUtils.equals(currentGroupName, preGroupName)) {
                //绘制分割线
                if (mDivideHeight != 0) {
                    float bottom = view.getTop();
                    if (bottom < mGroupHeight) {
                        //高度小于顶部悬浮栏时，跳过绘制
                        continue;
                    }
                    c.drawRect(left, bottom - mDivideHeight, right, bottom, mDividePaint);
                }
            } else {
                //绘制悬浮
                float bottom = Math.max(mGroupHeight, view.getTop());//决定当前顶部第一个悬浮Group的bottom
                if (position + 1 < itemCount) {
                    //获取下个GroupName
                    String nextGroupName = getGroupName(position + 1);
                    //下一组的第一个View接近头部
                    int viewBottom = view.getBottom();
                    if (!currentGroupName.equals(nextGroupName) && viewBottom < bottom) {
                        bottom = viewBottom;
                    }
                }
                //根据top绘制group背景
                c.drawRect(left, bottom - mGroupHeight, right, bottom, mGroutPaint);
                Paint.FontMetrics fm = mTextPaint.getFontMetrics();
                //文字竖直居中显示
                float baseLine = bottom - (mGroupHeight - (fm.bottom - fm.top)) / 2 - fm.bottom;
                //获取文字宽度
                float textWidth = mTextPaint.measureText(currentGroupName);
                float marginLeft = isAlignLeft ? 0 : right - textWidth;
                mSideMargin = Math.abs(mSideMargin);
                mSideMargin = isAlignLeft ? mSideMargin : -mSideMargin;
                c.drawText(currentGroupName, left + mSideMargin + marginLeft, baseLine, mTextPaint);
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
         * 是否靠左边
         * true 靠左边（默认）、false 靠右边
         *
         * @param b b
         * @return this
         */
        public Builder isAlignLeft(boolean b) {
            mDecoration.isAlignLeft = b;
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

        public StickyDecoration build() {
            return mDecoration;
        }
    }

}