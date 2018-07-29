package com.gavin.com.library.cache;

import android.util.LruCache;
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
    private boolean mUseCache = true;

    /**
     * lru花痴女
     */
    private LruCache<Integer, T> mLruCache;

    // TODO: gavin 2018/7/29  mLruCache移除后，使用软引用进行二级缓存

    /**
     * 二级缓存
     * 使用软引用缓存数据
     */
    private SparseArray<SoftReference<T>> mSoftCache;

    public CacheUtil() {
        initLruCache();
    }

    /**
     * 是否使用用缓存
     *
     * @param b
     */
    public void isCacheable(boolean b) {
        mUseCache = b;
    }

    private void initLruCache() {
        mLruCache = new LruCache<Integer, T>(2 * 1024 * 1024) {
            @Override
            protected void entryRemoved(boolean evicted, Integer key, T oldValue, T newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
            }
        };
    }

    @Override
    public void put(int position, T t) {
        if (!mUseCache) {
            return;
        }
        mLruCache.put(position, t);
    }

    @Override
    public T get(int position) {
        if (!mUseCache) {
            return null;
        }
        return mLruCache.get(position);
    }

    @Override
    public void remove(int position) {
        if (!mUseCache) {
            return;
        }
        mLruCache.remove(position);
    }

    @Override
    public void clean() {
        mLruCache.evictAll();
    }
}
