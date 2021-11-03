package com.bixin.ido.server.core.redis;

import com.alibaba.fastjson.JSON;
import com.bixin.ido.server.utils.BeanCopyUtil;
import com.bixin.ido.server.utils.LocalDateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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

    public <T> T getValue(String key, Class<T> tClass) {
        Object value = redisTemplate.opsForValue().get(key);
        log.info("getValue key {} value {}", key, value);
        if (Objects.isNull(value)) {
            return null;
        }
        return JSON.parseObject(JSON.toJSONString(value), tClass);
    }

    public void setValue(String key, Object value, long expiredTime, TimeUnit unit) {
        log.info("setValue key {} value {} expiredTime{}", key, value, expiredTime);
        redisTemplate.opsForValue().set(key, value, expiredTime, unit);
    }

    /**
     * 增加元素到sorted list
     * @param key
     * @param value
     * @param score
     */
    public void zAdd(String key, Object value, double score) {
        log.info("zAdd key {} value {} score {}", key, value, score);
        redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 返回指定 key 的 指定下标的成员，结果按照分数从小到大排列
     * start=0 && stop=-1 表示取出所有
     * @param key
     * @param start
     * @param stop
     */
    public <T> List<T> zRange(String key, long start, long stop, Class<T> tClass) {
        Set<Object> objectSet = redisTemplate.opsForZSet().range(key, start, stop);
        log.info("zRange key {} start {} stop {} object {}", key, start, stop, JSON.toJSON(objectSet));
        if (CollectionUtils.isEmpty(objectSet)) {
            return List.of();
        }
        return BeanCopyUtil.copyListProperties(Arrays.asList(objectSet.toArray()), object -> JSON.parseObject(JSON.toJSONString(object), tClass));
    }

    /**
     * 移除指定key的 分数介于 min 和 max 之间的成员
     * @param key
     * @param min
     * @param max
     */
    public void zRemoveRangeByScore(String key, long min, long max) {
        log.info("zRemoveRangeByScore key {} min {} max {}", key, min, max);
        redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
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
     * 尝试获取分布式锁, 执行任务后 并主动释放锁
     *
     * @param lockKey
     * @param requestId
     * @param expireTime
     * @param lockNextExpireTime 如果未获取到锁，则持续等待【lockNextExpireTime】时间ms后不在尝试获取
     * @param runnable
     */
    public void tryGetDistributedLock(String lockKey, String requestId, Long expireTime, long lockNextExpireTime, Runnable runnable) {
        tryGetDistributedLock(lockKey, requestId, expireTime, lockNextExpireTime, () -> {
            runnable.run();
            return null;
        });
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
