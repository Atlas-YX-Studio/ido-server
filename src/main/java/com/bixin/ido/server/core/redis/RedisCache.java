package com.bixin.ido.server.core.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author zhangcheng
 * create          2021-08-17 8:01 下午
 */
@Slf4j
@Component
public class RedisCache {


    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    static final String LOCK_SUCCESS = "OK";
    static final Long RELEASE_SUCCESS = 1L;

    /**
     * 尝试获取分布式锁
     *
     * @param lockKey
     * @param requestId
     * @param expireTime
     * @return
     */
    public boolean tryGetDistributedLock(String lockKey, String requestId, Long expireTime) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(lockKey, requestId, Duration.ofMillis(expireTime)));
    }


    /**
     * 尝试获取分布式锁, 执行任务后 并主动释放锁
     *
     * @param lockKey
     * @param requestId
     * @param expireTime
     * @param supplier
     * @return
     */
    public <T> T tryGetDistributedLock(String lockKey, String requestId, Long expireTime, Supplier<T> supplier) {
        try {
            if (tryGetDistributedLock(lockKey, requestId, expireTime)) {
                return supplier.get();
            }
        } catch (Exception e) {
            log.error("tryGetDistributedLock exception", e);
        } finally {
            releaseDistributedLock(lockKey, requestId);
        }
        return null;
    }

    /**
     * 主动释放分布式锁
     *
     * @param lockKey
     * @param requestId
     * @return
     */
    public boolean releaseDistributedLock(String lockKey, String requestId) {
        return RELEASE_SUCCESS.equals(redisTemplate.execute(
                new DefaultRedisScript<>("if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end", Long.class),
                List.of(lockKey),
                requestId
        ));
    }


}
