package com.bixin.ido.server.scheduler;

import com.bixin.ido.server.core.redis.RedisCache;
import com.bixin.ido.server.service.ITradingMiningService;
import com.bixin.ido.server.service.ITradingRewardUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.UUID;

@Slf4j
@Component
public class ScheduleFreedReward {

    private static final long PROCESSING_EXPIRE_TIME = 30 * 1000L;
    private static final long LOCK_EXPIRE_TIME = 0L;
    private static final String UPDATE_FREED_REWARD_LOCK = "update_freed_reward_lock";
    private static final String UPDATE_ATTENUATION_LOCK = "update_attenuation_lock";
    private static final String UPDATE_UNLOCK_REWARD_LOCK = "update_unlock_reward_lock";

    @Resource
    private RedisCache redisCache;
    @Resource
    private ITradingRewardUserService tradingRewardUserService;

    private ITradingMiningService tradingMiningService;

    @Scheduled(cron = "0 */5 * * * ?")
    public void updateFreedReward() {
        redisCache.tryGetDistributedLock(
                UPDATE_FREED_REWARD_LOCK,
                UUID.randomUUID().toString(),
                PROCESSING_EXPIRE_TIME,
                LOCK_EXPIRE_TIME,
                () -> {

                });
    }

    @Scheduled(cron = "0 0 0/4 * * ?")
    public void attenuation() {
        redisCache.tryGetDistributedLock(
                UPDATE_ATTENUATION_LOCK,
                UUID.randomUUID().toString(),
                PROCESSING_EXPIRE_TIME,
                LOCK_EXPIRE_TIME,
                () -> {
                    tradingMiningService.attenuation();
                });
    }

    @Scheduled(cron = "0 */5 * * * ?")
    public void unlockReward() {
        redisCache.tryGetDistributedLock(
                UPDATE_UNLOCK_REWARD_LOCK,
                UUID.randomUUID().toString(),
                PROCESSING_EXPIRE_TIME,
                LOCK_EXPIRE_TIME,
                () -> {
                    tradingMiningService.unlockReward();
                });
    }

}
