package icu.xchat.server.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 缓存工具类
 *
 * @author shouchen
 */
public class CacheManager<K, V> {
    private static final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private final ConcurrentHashMap<K, Cache> cacheMap;
    private final long liveTime;

    static {
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1, runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        });
    }

    /**
     * 构造一个缓存管理器
     *
     * @param liveTime 缓存有效时长 单位:毫秒 为0永不超时
     */
    public CacheManager(long liveTime) {
        this.liveTime = liveTime;
        cacheMap = new ConcurrentHashMap<>();
        clearCacheLoop();
    }

    private void clearCacheLoop() {
        clearCache();
        scheduledThreadPoolExecutor.schedule(this::clearCacheLoop, 3, TimeUnit.MINUTES);
    }

    /**
     * 清理过时的Cache
     */
    public void clearCache() {
        for (Map.Entry<K, Cache> entry : cacheMap.entrySet()) {
            if (entry.getValue().isExpire()) {
                cacheMap.remove(entry.getKey());
            }
        }
    }

    /**
     * 获取一个Cache
     *
     * @param key key
     * @return Cache
     */
    public V getCache(K key) {
        Cache cache = cacheMap.get(key);
        return cache == null ? null : cache.getCache();
    }

    /**
     * 设置一个Cache
     *
     * @param key   key
     * @param value value
     * @return this
     */
    public CacheManager<K, V> putCache(K key, V value) {
        cacheMap.put(key, new Cache(value));
        return this;
    }

    /**
     * 移除一个Cache
     *
     * @param key key
     */
    public void removeCache(K key) {
        cacheMap.remove(key);
    }

    private class Cache {
        private final V cache;
        private long updateTime;

        public Cache(V cache) {
            this.cache = cache;
            this.updateTime = System.currentTimeMillis();
        }

        public V getCache() {
            updateTime = System.currentTimeMillis();
            return cache;
        }

        public boolean isExpire() {
            return liveTime != 0 && updateTime + liveTime < System.currentTimeMillis();
        }
    }
}
