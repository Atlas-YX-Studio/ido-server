package com.bixin.nft.scheduler;

import com.bixin.common.config.StarConfig;
import com.bixin.common.utils.LocalDateTimeUtil;
import com.bixin.core.redis.RedisCache;
import com.bixin.nft.bean.DO.NftInfoDo;
import com.bixin.nft.service.NftInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author zhangcheng
 * create  2022/1/19
 */
@Slf4j
@Component
public class ScheduleNftRank {

    @Resource
    NftInfoService nftInfoService;
    @Resource
    private RedisCache redisCache;


    private static final long EXPIRE_TIME = 20 * 1000L;
    private static final long LOCK_EXPIRE_TIME = 0L;
    private static final String LOCK_KEY = "NFT_INFO_RANK_KEY";

    private static final int PAGE_SIZE = 2000;


    //        @Scheduled(cron = "0/10 * * * * ?")
    @Scheduled(fixedDelay = 20000)
    public void getNftMarketList() {
        redisCache.tryGetDistributedLock(
                LOCK_KEY,
                UUID.randomUUID().toString(),
                EXPIRE_TIME,
                LOCK_EXPIRE_TIME,
                this::updateNftInfoRank);
    }

    private void updateNftInfoRank() {
        AtomicInteger count = new AtomicInteger(0);
        List<NftInfoDo> allList = new ArrayList<>();
        for (; ; ) {
            List<NftInfoDo> list = nftInfoService.selectAll4Rank(true,
                    count.incrementAndGet(),
                    PAGE_SIZE + 1);
            if (CollectionUtils.isEmpty(list)) {
                log.error("ScheduleNftRank get nft info is empty");
                break;
            }
            if (list.size() <= PAGE_SIZE) {
                allList.addAll(list.subList(0, list.size()));
                break;
            }
            allList.addAll(list.subList(0, PAGE_SIZE));
        }
        List<NftInfoDo> sortList = allList.stream().sorted(Comparator.comparing(NftInfoDo::getScore))
                .collect(Collectors.toList());

        AtomicInteger rank = new AtomicInteger(0);
        List<NftInfoDo> updateList = new ArrayList<>();
        Long currentTime = LocalDateTimeUtil.getMilliByTime(LocalDateTime.now());
        sortList.forEach(p -> {
            updateList.add(NftInfoDo.builder()
                    .id(p.getId())
                    .rank(rank.incrementAndGet())
                    .updateTime(currentTime)
                    .build());
        });

        updateList.forEach(p -> {
            nftInfoService.update(p);
        });
    }

}
