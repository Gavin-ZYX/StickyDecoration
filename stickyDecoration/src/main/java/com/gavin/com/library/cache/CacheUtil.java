package com.gavin.com.library.cache;

import android.util.SparseArray;

import java.lang.ref.SoftReference;

/**
 * Created by gavin
 * date 2018/3/4
 * 缓存工具
 */

public class CacheUtil<T> implements CacheInterface<T> {

    /**
     * 是否缓存
     */
    private boolean museCache = false;
    /**
     * 是否使用强引用（默认软引用）
     * 引用类型：强引用、软引用
     */
    private boolean isStrongReference = false;

    /**
     * 使用强引用缓存数据
     */
    private SparseArray<T> mStrongCache;

    /**
     * 使用软引用缓存数据
     */
    private SparseArray<SoftReference<T>> mSoftCache;

    /**
     * 是否使用强引用缓存
     *
     * @param b
     */
    public void isCacheable(boolean b) {
        museCache = b;
    }

    /**
     * 是否使用强引用缓存
     *
     * @param b
     */
    public void isStrongReference(boolean b) {
        isStrongReference = b;
    }

    @Override
    public void put(int position, T t) {
        if (!museCache) {
            return;
        }
        if (isStrongReference) {
            if (mStrongCache == null) {
                mStrongCache = new SparseArray<>();
            }
            mStrongCache.put(position, t);
        } else {
            if (mSoftCache == null) {
                mSoftCache = new SparseArray<>();
            }
            mSoftCache.put(position, new SoftReference<T>(t));
        }
    }

    @Override
    public T get(int position) {
        if (!museCache) {
            return null;
        }
        if (isStrongReference) {
            if (mStrongCache == null) {
                mStrongCache = new SparseArray<>();
            }
            return mStrongCache.get(position);
        } else {
            if (mSoftCache == null) {
                mSoftCache = new SparseArray<>();
            }
            SoftReference<T> reference = mSoftCache.get(position);
            return reference == null ? null : reference.get();
        }
    }

    @Override
    public void remove(int position) {
        if (!museCache) {
            return;
        }
        if (mStrongCache != null){
            mStrongCache.remove(position);
        }

        if (mSoftCache != null) {
            mSoftCache.remove(position);
        }
    }

    @Override
    public void clean() {
        if (mStrongCache != null) {
            mStrongCache.clear();
        }
        if (mSoftCache != null) {
            mSoftCache.clear();
        }
    }
}
