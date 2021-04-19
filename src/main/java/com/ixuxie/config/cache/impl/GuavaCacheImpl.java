package com.ixuxie.config.cache.impl;

import com.ixuxie.config.cache.CacheManager;
import com.ixuxie.config.cache.ICache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class GuavaCacheImpl<K, V, HK> implements ICache<K, V, HK> {

    private CacheManager cacheManager;

    @Autowired
    private Environment environment;

    @PostConstruct
    public void init() {
        cacheManager = CacheManager.getInstance(environment);
    }

    @Override
    public void valueSet(K key, V value) {
        cacheManager.getCache().put(key, value);
    }

    @Override
    public V valueGet(Object key) {
        return (V) cacheManager.getCache().getIfPresent(key);
    }

    @Override
    public Boolean valueSetAndExpire(K key, V value, long timeout, TimeUnit unit) {
        cacheManager.getCache().put(key, value);
        return cacheManager.expire(key, timeout, unit);
    }

    @Override
    public void setAdd(K key, V... values) {

        try {
            Set<V> set = (Set<V>) cacheManager.getCache().get(key, new Callable() {
                @Override
                public Object call() throws Exception {
                    return new CopyOnWriteArraySet<V>();
                }
            });

            for (V value : values) {
                set.add(value);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setRemove(K key, Object... values) {
        Set<V> set = (Set<V>) cacheManager.getCache().getIfPresent(key);
        if (set != null) {
            for (Object value : values) {
                set.remove(value);
            }
        }
    }

    @Override
    public Boolean setIsMember(K key, Object o) {
        Set<V> set = (Set<V>) cacheManager.getCache().getIfPresent(key);
        if (set != null) {
            return set.contains(o);
        }
        return false;
    }

    @Override
    public Set<V> setMembers(K key) {
        Set<V> set = (Set<V>) cacheManager.getCache().getIfPresent(key);
        return set;
    }

    @Override
    public Long setSize(K key) {
        Set<V> set = (Set<V>) cacheManager.getCache().getIfPresent(key);
        if (set != null) {
            return Long.valueOf(set.size());
        }
        return 0L;
    }

    @Override
    public void listLeftPush(K key, V value) {
        List<V> list = null;
        try {
            list = (List<V>) cacheManager.getCache().get(key, new Callable() {
                @Override
                public Object call() throws Exception {
                    return Collections.synchronizedList(new LinkedList<V>());
                }
            });
            list.add(0, value);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void listRightPush(K key, V value) {
        List<V> list = null;
        try {
            list = (List<V>) cacheManager.getCache().get(key, new Callable() {
                @Override
                public Object call() throws Exception {
                    return Collections.synchronizedList(new LinkedList<V>());
                }
            });
            list.add(value);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public V listLeftPop(K key) {
        List<V> list = (List<V>) cacheManager.getCache().getIfPresent(key);
        if (list != null) {
            return list.remove(0);
        }
        return null;
    }

    @Override
    public V listRightPop(K key) {
        List<V> list = (List<V>) cacheManager.getCache().getIfPresent(key);
        if (list != null) {
            return list.remove(list.size() - 1);
        }
        return null;
    }

    @Override
    public List<V> listRange(K key, long start, long end) {
        List<V> list = (List<V>) cacheManager.getCache().getIfPresent(key);
        if (list != null) {
            int fromIndex = 0;
            int toIndex = list.size();
            if (start >= fromIndex) {
                fromIndex = ((Long) start).intValue();
            }
            if (end == -1) {
                return list.subList(fromIndex, list.size());
            } else if (end < toIndex) {
                toIndex = ((Long) (end + 1)).intValue();
            }
            if (toIndex >= fromIndex) {
                return list.subList(fromIndex, toIndex);
            }
        }
        return null;
    }

    @Override
    public void listRemove(K key, long count, Object value) {
        List<V> list = (List<V>) cacheManager.getCache().getIfPresent(key);
        List<Integer> indexs = new ArrayList<>();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (count <= 0) {
                    break;
                }
                if (list.get(i) == null) {
                    if (value == null) {
                        indexs.add(i);
                        count--;
                    }
                } else if (list.get(i).equals(value)) {
                    indexs.add(i);
                    count--;
                }
            }
        }
        for (int i = (indexs.size() - 1); i >= 0; i--) {
            list.remove(indexs.get(i));
        }
    }

    @Override
    public Long listSize(K key) {
        List<V> list = (List<V>) cacheManager.getCache().getIfPresent(key);
        if (list != null) {
            return Long.valueOf(list.size());
        }
        return 0L;
    }

    @Override
    public void hashPut(K key, HK hashKey, V value) {
        Map<HK, V> map = null;
        try {
            map = (Map<HK, V>) cacheManager.getCache().get(key, new Callable() {
                @Override
                public Object call() throws Exception {
                    return new ConcurrentHashMap<HK, V>();
                }
            });
            map.put(hashKey, value);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public V hashGet(K key, Object hashKey) {
        Map<HK, V> map = (Map<HK, V>) cacheManager.getCache().getIfPresent(key);
        if (map != null) {
            return map.get(hashKey);
        }
        return null;
    }

    @Override
    public Boolean hashHasKey(K key, Object hashKey) {
        Map<HK, V> map = (Map<HK, V>) cacheManager.getCache().getIfPresent(key);
        if (map != null) {
            return map.containsKey(hashKey);
        }
        return false;
    }

    @Override
    public Long hashDelete(K key, Object... hashKeys) {
        Map<HK, V> map = (Map<HK, V>) cacheManager.getCache().getIfPresent(key);
        if (map != null) {
            for (Object hashkey : hashKeys) {
                map.remove(hashkey);
            }
        }
        return null;
    }

    @Override
    public Map<HK, V> hashEntries(K key) {
        Map<HK, V> map = (Map<HK, V>) cacheManager.getCache().getIfPresent(key);
        return map;
    }

    @Override
    public List<V> hashValues(K key) {
        Map<HK, V> map = null;
        map = (Map<HK, V>) cacheManager.getCache().getIfPresent(key);
        if (map != null) {
            return new ArrayList<>(map.values());
        }
        return null;
    }

    @Override
    public Set<HK> hashKeys(K key) {
        Map<HK, V> map = null;
        map = (Map<HK, V>) cacheManager.getCache().getIfPresent(key);
        if (map != null) {
            return map.keySet();
        }
        return null;
    }

    @Override
    public Long hashSize(K key) {
        Map<HK, V> map = (Map<HK, V>) cacheManager.getCache().getIfPresent(key);
        if (map != null) {
            return Long.valueOf(map.size());
        }
        return 0L;
    }

    @Override
    public Boolean delete(K key) {
        cacheManager.getCache().invalidate(key);
        return true;
    }

    @Override
    public Long delete(Collection<K> keys) {
        cacheManager.getCache().invalidateAll(keys);
        return Long.valueOf(keys.size());
    }

    @Override
    public Boolean expire(K key, long timeout, TimeUnit unit) {
        return cacheManager.expire(key, timeout, unit);
    }

    @Override
    public Boolean expireAt(K key, Date date) {
        return cacheManager.expireAt(key, date);
    }

    @Override
    public Long getExpire(K key) {
        return cacheManager.getExpire(key);
    }

    @Override
    public Long getExpire(K key, TimeUnit timeUnit) {
        return cacheManager.getExpire(key, timeUnit);
    }

    @Override
    public Set<K> keys(K pattern) {
        String keys = (String) pattern;
        if ("*".equals(pattern)) {
            return cacheManager.getCache().asMap().keySet();
        } else if (keys.endsWith("*")) {
            return (Set<K>) cacheManager.getCache().asMap().keySet()
                    .stream().filter(key -> ((String) key).startsWith(keys.substring(0, keys.length() - 1)))
                    .collect(Collectors.toSet());
        }
        return null;
    }

    @Override
    public Boolean hasKey(K key) {
        return cacheManager.getCache().getIfPresent(key) != null ? true : false;
    }

    @Override
    public void cleanUp() {
        cacheManager.getCache().cleanUp();
    }
}
