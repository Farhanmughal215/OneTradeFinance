//package com.xstocks.referral.utils;
//
//import com.github.benmanes.caffeine.cache.Cache;
//import com.github.benmanes.caffeine.cache.CacheLoader;
//import com.github.benmanes.caffeine.cache.Caffeine;
//import com.github.benmanes.caffeine.cache.LoadingCache;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReentrantReadWriteLock;
//
///**
// * @author firtuss
// */
//public class LocalCacheUtil {
//    private static final Map<String, Cache> manualCaches = new HashMap<>();
//    private static final Map<String, LoadingCache> loadingCaches = new HashMap<>();
//
////    private static final Map<String, AsyncLoadingCache> asyncLoadingCaches = new HashMap<>();
//
//    private static final ReentrantReadWriteLock rwlOfManual = new ReentrantReadWriteLock();
//
//    private static final Lock rlOfManual = rwlOfManual.readLock();
//
//    private static final Lock wlOfManual = rwlOfManual.writeLock();
//
//    private static final ReentrantReadWriteLock rwlOfLoading = new ReentrantReadWriteLock();
//
//    private static final Lock rlOfLoading = rwlOfLoading.readLock();
//
//    private static final Lock wlOfLoading = rwlOfLoading.writeLock();
//
//    private static <K, V> Cache<K, V> getManualCache(String key) {
//        rlOfManual.lock();
//        try {
//            return manualCaches.get(key);
//        } finally {
//            rlOfManual.unlock();
//        }
//    }
//
//    private static <K, V> void createManualCache(int size, long expire, String key) {
//        wlOfManual.lock();
//        try {
//            if (!manualCaches.containsKey(key)) {
//                Cache<K, V> cache =
//                        Caffeine.newBuilder()
//                                .expireAfterWrite(expire, TimeUnit.MINUTES)
//                                .maximumSize(size)
//                                .build();
//                manualCaches.put(key, cache);
//            }
//        } finally {
//            wlOfManual.unlock();
//        }
//    }
//
//    public static <K, V> Cache<K, V> initManualCache(int size, long expire, String key) {
//        Cache cache = getManualCache(key);
//        if (cache != null) {
//            return cache;
//        }
//        createManualCache(size, expire, key);
//        return getManualCache(key);
//    }
//
//    public static <K, V> void removeManualCache(String key) {
//        wlOfManual.lock();
//        try {
//            manualCaches.remove(key);
//        } finally {
//            wlOfManual.unlock();
//        }
//    }
//
//    public static <K, V> LoadingCache<K, V> getLoadingCache(String key) {
//        rlOfLoading.lock();
//        try {
//            return loadingCaches.get(key);
//        } finally {
//            rlOfLoading.unlock();
//        }
//    }
//
//    private static <K, V> void createLoadingCache(String key, int size, long expire, CacheLoader<K, V> cacheLoader) {
//        wlOfLoading.lock();
//        try {
//            if (!loadingCaches.containsKey(key)) {
//                LoadingCache<K, V>
//                        cache = Caffeine.newBuilder()
//                        .maximumSize(size)
//                        .refreshAfterWrite(expire, TimeUnit.MINUTES)
//                        .build(cacheLoader);
//                loadingCaches.put(key, cache);
//            }
//        } finally {
//            wlOfLoading.unlock();
//        }
//    }
//
//    public static <K, V> LoadingCache<K, V> initLoadingCache(String key, int size, long expire,
//                                                             CacheLoader<K, V> cacheLoader) {
//        LoadingCache cache = getLoadingCache(key);
//        if (cache != null) {
//            return cache;
//        }
//        createLoadingCache(key, size, expire, cacheLoader);
//        return getLoadingCache(key);
//    }
//
//    public static <K, V> void removeLoadingCache(String key) {
//        wlOfLoading.lock();
//        try {
//            loadingCaches.remove(key);
//        } finally {
//            wlOfLoading.unlock();
//        }
//    }
//}
