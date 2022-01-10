package com.bixin.ido.scheduler;

import com.bixin.core.redis.RedisCache;
import com.bixin.ido.service.ITradingMiningService;
import com.bixin.ido.service.ITradingRewardUserService;
import com.bixin.ido.service.NftMiningUsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.UUID;

@Slf4j
@Component
public class ScheduleCompensate {

    private static final long PROCESSING_EXPIRE_TIME = 290 * 1000L;
    private static final long LOCK_EXPIRE_TIME = 0L;
    private static final String NFT_MINING_COMPENSATE_LOCK = "nft_mining_compensate_lock";

    @Resource
    private RedisCache redisCache;
    @Resource
    private NftMiningUsersService nftMiningUsersService;

    @Scheduled(cron = "0 0/5 * * * ?")
    public void nftMiningCompensate() {
        redisCache.tryGetDistributedLock(
                NFT_MINING_COMPENSATE_LOCK,
                UUID.randomUUID().toString(),
                PROCESSING_EXPIRE_TIME,
                LOCK_EXPIRE_TIME,
                () -> {
                    nftMiningUsersService.compensateNftMiningHarvest();
                });
    }

}
