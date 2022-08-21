package com.gavin.com.stickydecoration.view.widget;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.gavin.com.library.BaseDecoration;

/**
 * Created by gavin
 * date 2018/8/26
 */
public class MyRecyclerView extends RecyclerView {

    private BaseDecoration mDecoration;

    public MyRecyclerView(Context context) {
        super(context);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void addItemDecoration(ItemDecoration decor) {
        if (decor != null && decor instanceof BaseDecoration) {
            mDecoration = (BaseDecoration) decor;
        }
        super.addItemDecoration(decor);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (mDecoration != null) {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mDecoration.onEventDown(e);
                    break;
                case MotionEvent.ACTION_UP:
                    if (mDecoration.onEventUp(e)) {
                        return true;
                    }
                    break;
                default:
            }
        }
        return super.onInterceptTouchEvent(e);
    }
}

