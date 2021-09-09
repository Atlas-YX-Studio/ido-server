package com.bixin.ido.server.core.redis;

import com.bixin.ido.server.utils.LocalDateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
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


    static final Long RELEASE_SUCCESS = 1L;
    static final int parkMilliSeconds = 10;


    public Object getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void setValue(String key, Object value, long expiredTime, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, expiredTime, unit);
    }

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
     * @param lockNextExpireTime 如果未获取到锁，则持续等待【lockNextExpireTime】时间ms后不在尝试获取
     * @param supplier
     * @return
     */
    public <T> T tryGetDistributedLock(String lockKey, String requestId, Long expireTime, long lockNextExpireTime, Supplier<T> supplier) {
        Long currentTime = LocalDateTimeUtil.getMilliByTime(LocalDateTime.now());
        T t = null;
        try {
            for (; ; ) {
                if (tryGetDistributedLock(lockKey, requestId, expireTime)) {
                    t = supplier.get();
                    break;
                } else {
                    Long nextTime = LocalDateTimeUtil.getMilliByTime(LocalDateTime.now());
                    if ((currentTime + lockNextExpireTime) <= nextTime) {
//                        log.info("try to get distributed lock next time {}, {}, {}", Thread.currentThread().getName(), lockKey, requestId);
                        break;
                    }
                }
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(parkMilliSeconds));
            }
        } catch (Exception e) {
            log.error("try to get distributed lock exception", e);
            throw new RuntimeException(e);
        } finally {
            releaseDistributedLock(lockKey, requestId);
//            boolean hasInitiative = releaseDistributedLock(lockKey, requestId);
            //被动释放锁、或者未获取到锁，打印日志
//            if (!hasInitiative) {
//                log.info("try to get distributed lock passive release {}, {}, {}",
//                        Thread.currentThread().getName(), lockKey, requestId);
//            }
        }

        return t;
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
