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
     * @param liveTime 缓存有效时长 单位:毫秒
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

    public void clearCache() {
        for (Map.Entry<K, Cache> entry : cacheMap.entrySet()) {
            if (entry.getValue().isExpire()) {
                cacheMap.remove(entry.getKey());
            }
        }
    }

    public V getCache(K key) {
        Cache cache = cacheMap.get(key);
        return cache == null ? null : cache.getCache();
    }

    public CacheManager<K, V> putCache(K key, V value) {
        cacheMap.put(key, new Cache(value));
        return this;
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
            return updateTime + liveTime < System.currentTimeMillis();
        }
    }
}
