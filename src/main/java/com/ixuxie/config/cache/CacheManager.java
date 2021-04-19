package com.ixuxie.config.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.core.env.Environment;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@Slf4j
public class CacheManager {

    private static volatile CacheManager instance;

    private Cache cache;

    ScheduledExecutorService timer;

    private Map<Object, Long> cacheKeyMap;


    private CacheManager(Environment environment) {
        if (instance != null) {
            throw new IllegalStateException("Already initialized.");
        }
        //cache的最大size
        long maximumSize = environment.getProperty("disco.cache.local.maximum-size", Long.class, 3000L);
        //读写后多久expire
        long expireAfterAccess = environment.getProperty("disco.cache.local.expire-after-access", Long.class, 3600L);
        //配置同时可以有多少个写线程同时写
        int concurrencyLevel = environment.getProperty("disco.cache.local.concurrency-level", Integer.class, 4);
        cache = CacheBuilder.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterAccess(expireAfterAccess, TimeUnit.SECONDS)
                .concurrencyLevel(concurrencyLevel)
                .removalListener(notification -> {
                    Object key = notification.getKey();
                    if (cacheKeyMap.containsKey(key)) {
                        cacheKeyMap.remove(key);
                    }
                    log.debug(key + " was removed, cause is " + notification.getCause());
                })
                .build();
        timer = new ScheduledThreadPoolExecutor(1, new BasicThreadFactory.Builder().namingPattern("cache-expire-check-pool-%d").daemon(true).build());
        cacheKeyMap = new HashMap<>();
    }

    public static CacheManager getInstance(Environment environment) {
        CacheManager result = instance;
        if (result == null) {
            synchronized (CacheManager.class) {
                result = instance;
                if (result == null) {
                    instance = result = new CacheManager(environment);
                }
            }
        }
        return result;
    }

    public Cache getCache() {
        return cache;
    }

    public Boolean expire(Object key, long timeout, TimeUnit unit) {
        try {
            long rawTimeout = unit.toSeconds(timeout);
            long expireTime = System.currentTimeMillis()+rawTimeout;
            cacheKeyMap.put(key, expireTime);
            timer.schedule(new TimeoutTask(key, expireTime), rawTimeout, TimeUnit.MILLISECONDS);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public Boolean expireAt(Object key, Date date) {
        try {
            long rawTimeout = date.getTime()- System.currentTimeMillis();
            if(rawTimeout>0){
                cacheKeyMap.put(key, date.getTime());
                timer.schedule(new TimeoutTask(key, date.getTime()), rawTimeout, TimeUnit.MILLISECONDS);
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public Long getExpire(Object key) {
        return getExpire(key, TimeUnit.SECONDS);
    }

    public Long getExpire(Object key, TimeUnit timeUnit) {
        Long rawTimeout = cacheKeyMap.get(key);
        if(rawTimeout!=null){
            long delay = rawTimeout- System.currentTimeMillis();
            if(delay>0){
                return timeUnit.convert(delay, TimeUnit.MILLISECONDS);
            }else{
                return 0L;
            }

        }
        return -1L;
    }

    class TimeoutTask implements Runnable {
        private Object key ;
        private Long expireTime;

        public TimeoutTask(Object key, Long expireTime){
            this.key = key;
            this.expireTime = expireTime;
        }

        @Override
        public void run() {
            if(this.expireTime.equals(cacheKeyMap.get(key))){
                cache.invalidate(key);
                log.debug("remove : "+key);
            }
        }
    }
}
