package com.gavin.com.stickydecoration.view.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by gavin
 * Created date 17/6/4
 * Created log  简单的分割线
 */

public class SimpleDecoration extends RecyclerView.ItemDecoration {

    private int mHeight = 5; //分割线高度
    private String mDecorationColor = "#48BDFF"; //分割线颜色
    private Paint mPaint;

    public SimpleDecoration() {
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor(mDecorationColor));
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        if (position != 0) {
            //第一个item预留空间
            outRect.top = mHeight;
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        final int left = parent.getLeft() + parent.getPaddingLeft();
        final int right = parent.getRight() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View childView = parent.getChildAt(i);
            final int bottom = childView.getTop();
            final int top = bottom - mHeight;
            c.drawRect(left, top, right, bottom, mPaint);
        }
    }
}
