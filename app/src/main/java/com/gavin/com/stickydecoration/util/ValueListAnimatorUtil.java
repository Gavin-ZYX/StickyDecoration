package com.gavin.com.stickydecoration.util;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gavin
 * date 17/12/28
 * 维护多次变化的动画
 * {@link OnProgressListener} 监听每次的值
 */

public class ValueListAnimatorUtil {
    private int mDuration = 500;
    private ValueAnimator mAnimator;

    private List<Integer> mList = new ArrayList<>();
    private int mCurrent;
    private int mIndex;
    private boolean isRunning = false;

    private OnProgressListener mListener;

    public ValueListAnimatorUtil() {
    }

    /**
     * 添加新的进度
     * @param value
     */
    public void addValue(int value) {
        mList.add(value);
        startAnimator();
    }

    /**
     * 设计监听
     * @param listener
     */
    public void setListener(OnProgressListener listener) {
        this.mListener = listener;
    }

    /**
     * 开始动画（当上个动画执行中时，将不执行）
     */
    private void startAnimator() {
        if (isRunning) {
            return;
        }
        if (mIndex < mList.size()) {
            int next = mList.get(mIndex);
            mAnimator = ValueAnimator.ofInt(mCurrent, next);
            mAnimator.setDuration(mDuration);
            mAnimator.setInterpolator(new LinearInterpolator());
            mAnimator.addListener(new Animator.AnimatorListener() {

                @Override
                public void onAnimationStart(Animator animation) {
                    isRunning = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    isRunning = false;
                    mCurrent = next;
                    mIndex++;
                    startAnimator();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    isRunning = false;
                    reset();
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            mAnimator.addUpdateListener(animation -> {
                if (mListener != null) {
                    int progress = (int) animation.getAnimatedValue();
                    mListener.onProgress(progress);
                }
            });
            mAnimator.start();
        }
    }

    /**
     * 重置
     */
    public void reset() {
        mIndex = 0;
        mCurrent = 0;
        mList.clear();
    }

    /**
     * 结束动画
     */
    public void finish() {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }
        if (mListener != null && mList.size() > 0) {
            int progress = mList.get(mList.size() - 1);
            mListener.onProgress(progress);
        }
    }

    public interface OnProgressListener {
        /**
         * 当前值
         *
         * @param current
         */
        void onProgress(int current);
    }

}
