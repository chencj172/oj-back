package com.chencj.common.utils;


import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.BooleanUtil;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: SimpleRedisLock
 * @Description: redis中锁的获取与释放
 * @Author: chencj
 * @Datetime: 2025/2/18 16:35
 * @Version: 1.0
 */
public class SimpleRedisLock {

    private final StringRedisTemplate stringRedisTemplate;
    private final String name;

    public SimpleRedisLock(StringRedisTemplate stringRedisTemplate, String name) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.name = name;
    }

    private static final String KEY_PREFIX = "lock:";
    private static final String ID_PREFIX = UUID.randomUUID().toString(true);
    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT;
    static {
        // 加载Lua脚本
        UNLOCK_SCRIPT = new DefaultRedisScript<>();
        UNLOCK_SCRIPT.setLocation(new ClassPathResource("unlock.lua"));
        UNLOCK_SCRIPT.setResultType(Long.class);
    }

    public boolean tryLock(long timeoutSec) {
        String thread_name = Thread.currentThread().getName();
        String key = KEY_PREFIX + name;
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent(key, ID_PREFIX + thread_name, timeoutSec, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(lock);
    }

    public void unLock() {
        // 调用Lua脚本释放锁，判断和释放动作合成原子性操作
        stringRedisTemplate.execute(UNLOCK_SCRIPT,
                Collections.singletonList(KEY_PREFIX + name),
                ID_PREFIX + Thread.currentThread().getName());
    }
}

