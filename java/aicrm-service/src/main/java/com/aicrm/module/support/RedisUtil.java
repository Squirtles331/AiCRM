package com.aicrm.module.support;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redis 操作模板：缓存读写 + 分布式锁
 * <p>
 * 所有 key 建议按业务域加前缀，如 aicrm:{tenantId}:{biz}:{key}。
 */
@Component
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedissonClient redissonClient;

    public RedisUtil(RedisTemplate<String, Object> redisTemplate, RedissonClient redissonClient) {
        this.redisTemplate = redisTemplate;
        this.redissonClient = redissonClient;
    }

    // ---- 缓存 ----

    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void set(String key, Object value, Duration timeout) {
        redisTemplate.opsForValue().set(key, value, timeout);
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public <T> T get(String key, Class<T> type) {
        Object value = get(key);
        return type.isInstance(value) ? type.cast(value) : null;
    }

    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /** 自增并设置过期时间（用于频控计数） */
    public long incr(String key, Duration ttl) {
        Long value = redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, ttl);
        return value == null ? 0 : value;
    }

    // ---- 分布式锁 ----

    /**
     * 加锁执行：同一 key 互斥
     *
     * @param key      锁 key
     * @param waitSec  获取锁等待秒数
     * @param leaseSec 锁自动释放秒数
     * @param supplier 锁内执行逻辑
     * @return 是否获得锁并执行（未获锁返回 false，锁内异常向上抛出）
     */
    public boolean tryLock(String key, long waitSec, long leaseSec, Supplier<Void> supplier) {
        RLock lock = redissonClient.getLock(key);
        try {
            if (lock.tryLock(waitSec, leaseSec, TimeUnit.SECONDS)) {
                try {
                    supplier.get();
                    return true;
                } finally {
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                }
            }
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
