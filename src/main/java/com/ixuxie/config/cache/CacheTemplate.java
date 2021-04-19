package com.ixuxie.config.cache;

import com.ixuxie.config.cache.impl.GuavaCacheImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class CacheTemplate<K, V, HK> implements ICache<K, V, HK> {

/*    @Autowired
    private RedisCacheImpl<K, V, HK> redisCache;*/

    @Autowired
    private GuavaCacheImpl<K, V, HK> guavaCache;

    @Value("${disco.cache.has-redis:true}")
    private boolean hasRedis;

    private ICache<K, V, HK> getCache() {
       /* if (hasRedis) {
            return redisCache;
        } else {
            return guavaCache;
        }*/
        return guavaCache;
    }

    @Override
    public void valueSet(K key, V value) {
        getCache().valueSet(key, value);
    }

    @Override
    public Boolean valueSetAndExpire(K key, V value, long timeout, TimeUnit unit) {
        this.valueSet(key, value);
        return this.expire(key, timeout, unit);
    }

    @Override
    public V valueGet(Object key) {
        return getCache().valueGet(key);
    }

    @Override
    public void setAdd(K key, V... values) {
        getCache().setAdd(key, values);
    }

    @Override
    public void setRemove(K key, Object... values) {
        getCache().setRemove(key, values);
    }

    @Override
    public Boolean setIsMember(K key, Object o) {
        return getCache().setIsMember(key, o);
    }

    @Override
    public Set<V> setMembers(K key) {
        return getCache().setMembers(key);
    }

    @Override
    public Long setSize(K key) {
        return getCache().setSize(key);
    }

    @Override
    public void listLeftPush(K key, V value) {
        getCache().listLeftPush(key, value);
    }

    @Override
    public void listRightPush(K key, V value) {
        getCache().listRightPush(key, value);
    }

    @Override
    public V listLeftPop(K key) {
        return getCache().listLeftPop(key);
    }

    @Override
    public V listRightPop(K key) {
        return getCache().listRightPop(key);
    }

    @Override
    public List<V> listRange(K key, long start, long end) {
        return getCache().listRange(key, start, end);
    }

    @Override
    public void listRemove(K key, long count, Object value) {
        getCache().listRemove(key, count, value);
    }

    @Override
    public Long listSize(K key) {
        return getCache().listSize(key);
    }

    @Override
    public void hashPut(K key, HK hashKey, V value) {
        getCache().hashPut(key, hashKey, value);
    }

    @Override
    public V hashGet(K key, Object hashKey) {
        return getCache().hashGet(key, hashKey);
    }

    @Override
    public Boolean hashHasKey(K key, Object hashKey) {
        return getCache().hashHasKey(key, hashKey);
    }

    @Override
    public Long hashDelete(K key, Object... hashKeys) {
        return getCache().hashDelete(key, hashKeys);
    }

    @Override
    public Map<HK, V> hashEntries(K key) {
        return getCache().hashEntries(key);
    }

    @Override
    public List<V> hashValues(K key) {
        return getCache().hashValues(key);
    }

    @Override
    public Set<HK> hashKeys(K key) {
        return getCache().hashKeys(key);
    }

    @Override
    public Long hashSize(K key) {
        return getCache().hashSize(key);
    }

    @Override
    public Boolean delete(K key) {
        return getCache().delete(key);
    }

    @Override
    public Long delete(Collection<K> keys) {
        return getCache().delete(keys);
    }

    @Override
    public Boolean expire(K key, long timeout, TimeUnit unit) {
        return getCache().expire(key, timeout, unit);
    }

    @Override
    public Boolean expireAt(K key, Date date) {
        return getCache().expireAt(key, date);
    }

    @Override
    public Long getExpire(K key) {
        return getCache().getExpire(key);
    }

    @Override
    public Long getExpire(K key, TimeUnit timeUnit) {
        return getCache().getExpire(key, timeUnit);
    }

    @Override
    public Set<K> keys(K pattern) {
        return getCache().keys(pattern);
    }

    @Override
    public Boolean hasKey(K key) {
        return getCache().hasKey(key);
    }

    @Override
    public void cleanUp() {
        getCache().cleanUp();
    }

    public boolean hasRedis() {
        return hasRedis;
    }
}
