package com.ixuxie.config.cache.impl;

@Deprecated
public class RedisCacheImpl<K, V, HK> {


   /*
    public class RedisCacheImpl<K, V, HK> implements ICache<K, V, HK> {
    @Autowired
    private RedisTemplate<K, V> redisTemplate;

    @Override
    public void valueSet(K key, V value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public Boolean valueSetAndExpire(K key, V value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value);
        return redisTemplate.expire(key, timeout, unit);
    }

    @Override
    public V valueGet(Object key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void setAdd(K key, V... values) {
        redisTemplate.opsForSet().add(key, values);
    }

    @Override
    public void setRemove(K key, Object... values) {
        redisTemplate.opsForSet().remove(key, values);
    }

    @Override
    public Boolean setIsMember(K key, Object o) {

        return redisTemplate.opsForSet().isMember(key, o);
    }

    @Override
    public Set<V> setMembers(K key) {
        return redisTemplate.opsForSet().members(key);
    }

    @Override
    public Long setSize(K key) {
        return redisTemplate.opsForSet().size(key);
    }

    @Override
    public void listLeftPush(K key, V value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    @Override
    public void listRightPush(K key, V value) {
        redisTemplate.opsForList().rightPush(key, value);
    }

    @Override
    public V listLeftPop(K key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    @Override
    public V listRightPop(K key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    @Override
    public List<V> listRange(K key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    @Override
    public void listRemove(K key, long count, Object value) {
        redisTemplate.opsForList().remove(key, count, value);
    }

    @Override
    public Long listSize(K key) {
        return redisTemplate.opsForList().size(key);
    }

    @Override
    public void hashPut(K key, HK hashKey, V value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    @Override
    public V hashGet(K key, Object hashKey) {
        return (V) redisTemplate.opsForHash().get(key, hashKey);
    }

    @Override
    public Boolean hashHasKey(K key, Object hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    @Override
    public Long hashDelete(K key, Object... hashKeys) {
        return redisTemplate.opsForHash().delete(key, hashKeys);
    }

    @Override
    public Map<HK, V> hashEntries(K key) {
        return (Map<HK, V>) redisTemplate.opsForHash().entries(key);
    }

    @Override
    public List<V> hashValues(K key) {
        return (List<V>) redisTemplate.opsForHash().values(key);
    }

    @Override
    public Set<HK> hashKeys(K key) {
        return (Set<HK>) redisTemplate.opsForHash().keys(key);
    }

    @Override
    public Long hashSize(K key) {
        return redisTemplate.opsForHash().size(key);
    }

    @Override
    public Boolean delete(K key) {
        return redisTemplate.delete(key);
    }

    @Override
    public Long delete(Collection<K> keys) {
        return redisTemplate.delete(keys);
    }

    @Override
    public Boolean expire(K key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    @Override
    public Boolean expireAt(K key, Date date) {
        return redisTemplate.expireAt(key, date);
    }

    @Override
    public Long getExpire(K key) {
        return redisTemplate.getExpire(key);
    }

    @Override
    public Long getExpire(K key, TimeUnit timeUnit) {
        return redisTemplate.getExpire(key, timeUnit);
    }

    @Override
    public Set<K> keys(K pattern) {
        return redisTemplate.keys(pattern);
    }

    @Override
    public Boolean hasKey(K key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public void cleanUp() {
        Set<K> keys = redisTemplate.keys((K) "*");
        redisTemplate.delete(keys);
    }

    @Override
    public DataType type(K key) {
        return redisTemplate.type(key);
    }

    */
}
