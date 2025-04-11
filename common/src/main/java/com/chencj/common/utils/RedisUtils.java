package com.chencj.common.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Redis 工具类
 * 基于 StringRedisTemplate 实现，所有键值都为 String 类型
 */
@Component
public class RedisUtils {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // ============================== 通用操作 ==============================

    /**
     * 设置键的过期时间
     * @param key 键
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return 是否设置成功
     */
    public boolean expire(String key, long timeout, TimeUnit unit) {
        try {
            return Boolean.TRUE.equals(stringRedisTemplate.expire(key, timeout, unit));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取键的剩余过期时间
     * @param key 键
     * @return 剩余时间(秒) -2:键不存在 -1:永久有效
     */
    public long getExpire(String key) {
        return stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断键是否存在
     * @param key 键
     * @return true存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除键
     * @param keys 可以传一个或多个键
     * @return 成功删除的个数
     */
    public long delete(String... keys) {
        if (keys == null || keys.length == 0) {
            return 0;
        }
        Long count = stringRedisTemplate.delete(Arrays.asList(keys));
        return count == null ? 0 : count;
    }

    // ============================== String 操作 ==============================

    /**
     * 获取键值
     * @param key 键
     * @return 值
     */
    public String get(String key) {
        return key == null ? null : stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 设置键值
     * @param key 键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, String value) {
        try {
            stringRedisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 设置键值并设置过期时间
     * @param key 键
     * @param value 值
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return true成功 false失败
     */
    public boolean set(String key, String value, long timeout, TimeUnit unit) {
        try {
            if (timeout > 0) {
                stringRedisTemplate.opsForValue().set(key, value, timeout, unit);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 递增
     * @param key 键
     * @param delta 递增因子(大于0)
     * @return 递增后的值
     */
    public long increment(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return stringRedisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     * @param key 键
     * @param delta 递减因子(大于0)
     * @return 递减后的值
     */
    public long decrement(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return stringRedisTemplate.opsForValue().decrement(key, delta);
    }

    // ============================== Hash 操作 ==============================

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<String, String> hGetAll(String key) {
        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(key);
        return map.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> (String) e.getKey(),
                        e -> (String) e.getValue(),
                        (v1, v2) -> v1,
                        HashMap::new));
    }

    /**
     * 向hash表中放入数据
     * @param key 键
     * @param hashKey hash键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean hSet(String key, String hashKey, String value) {
        try {
            stringRedisTemplate.opsForHash().put(key, hashKey, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向hash表中放入数据并设置过期时间
     * @param key 键
     * @param hashKey hash键
     * @param value 值
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return true成功 false失败
     */
    public boolean hSet(String key, String hashKey, String value, long timeout, TimeUnit unit) {
        try {
            stringRedisTemplate.opsForHash().put(key, hashKey, value);
            if (timeout > 0) {
                expire(key, timeout, unit);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取hash表中指定hashKey的值
     * @param key 键
     * @param hashKey hash键
     * @return 值
     */
    public String hGet(String key, String hashKey) {
        return (String) stringRedisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 删除hash表中的值
     * @param key 键
     * @param hashKeys 可以删除多个hash键
     * @return 删除的个数
     */
    public long hDelete(String key, Object... hashKeys) {
        return stringRedisTemplate.opsForHash().delete(key, hashKeys);
    }

    // ============================== List 操作 ==============================

    /**
     * 获取list缓存的内容
     * @param key 键
     * @param start 开始索引
     * @param end 结束索引 (0到-1代表所有值)
     * @return 列表范围内的元素
     */
    public List<String> lRange(String key, long start, long end) {
        try {
            return stringRedisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 获取list长度
     * @param key 键
     * @return 长度
     */
    public long lSize(String key) {
        try {
            return stringRedisTemplate.opsForList().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 通过索引获取list中的值
     * @param key 键
     * @param index 索引
     * @return 值
     */
    public String lIndex(String key, long index) {
        try {
            return stringRedisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将值放入列表头部
     * @param key 键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean lLeftPush(String key, String value) {
        try {
            stringRedisTemplate.opsForList().leftPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将值放入列表尾部
     * @param key 键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean lRightPush(String key, String value) {
        try {
            stringRedisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ============================== Set 操作 ==============================

    /**
     * 将数据放入set缓存
     * @param key 键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sAdd(String key, String... values) {
        try {
            return stringRedisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取set中的所有值
     * @param key 键
     * @return set集合
     */
    public Set<String> sMembers(String key) {
        try {
            return stringRedisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptySet();
        }
    }

    /**
     * 判断set中是否存在value
     * @param key 键
     * @param value 值
     * @return true存在 false不存在
     */
    public boolean sIsMember(String key, String value) {
        try {
            return Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(key, value));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ============================== ZSet 操作 ==============================

    /**
     * 向zset中添加元素
     * @param key 键
     * @param value 值
     * @param score 分数
     * @return 是否成功
     */
    public boolean zAdd(String key, String value, double score) {
        try {
            return Boolean.TRUE.equals(stringRedisTemplate.opsForZSet().add(key, value, score));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取zset指定范围的元素
     * @param key 键
     * @param start 开始位置
     * @param end 结束位置
     * @return 元素集合
     */
    public Set<String> zRange(String key, long start, long end) {
        try {
            return stringRedisTemplate.opsForZSet().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptySet();
        }
    }

    // ============================== 分布式锁 ==============================

    private static final String LOCK_SCRIPT =
            "if redis.call('setnx', KEYS[1], ARGV[1]) == 1 then " +
                    "return redis.call('expire', KEYS[1], ARGV[2]) " +
                    "else return 0 end";

    private static final String UNLOCK_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "return redis.call('del', KEYS[1]) " +
                    "else return 0 end";

    /**
     * 获取分布式锁
     * @param lockKey 锁键
     * @param requestId 请求标识(可使用UUID)
     * @param expireTime 过期时间(秒)
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, String requestId, long expireTime) {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>(LOCK_SCRIPT, Long.class);
        Long result = stringRedisTemplate.execute(script, Collections.singletonList(lockKey), requestId, String.valueOf(expireTime));
        return result != null && result == 1;
    }

    /**
     * 释放分布式锁
     * @param lockKey 锁键
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    public boolean releaseLock(String lockKey, String requestId) {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>(UNLOCK_SCRIPT, Long.class);
        Long result = stringRedisTemplate.execute(script, Collections.singletonList(lockKey), requestId);
        return result != null && result == 1;
    }

    // ============================== 其他高级操作 ==============================

    /**
     * 批量设置键值
     * @param map 键值对集合
     */
    public void multiSet(Map<String, String> map) {
        stringRedisTemplate.opsForValue().multiSet(map);
    }

    /**
     * 批量获取键值
     * @param keys 键集合
     * @return 值集合
     */
    public List<String> multiGet(Collection<String> keys) {
        return stringRedisTemplate.opsForValue().multiGet(keys);
    }
}
