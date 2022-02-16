package com.bixin.nft.scheduler;

import com.bixin.common.utils.LocalDateTimeUtil;
import com.bixin.core.redis.RedisCache;
import com.bixin.nft.bean.DO.NftGroupDo;
import com.bixin.nft.bean.DO.NftInfoDo;
import com.bixin.nft.common.enums.NftType;
import com.bixin.nft.service.NftGroupService;
import com.bixin.nft.service.NftInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author zhangcheng
 * create  2022/1/19
 */
@Slf4j
@Component
public class ScheduleNftRank {

    @Resource
    private NftGroupService nftGroupService;
    @Resource
    private NftInfoService nftInfoService;
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
        // update card quantity
        NftGroupDo selectNftGroupDo = new NftGroupDo();
        selectNftGroupDo.setType(NftType.COMPOSITE_CARD.getType());
        List<NftGroupDo> nftGroupDos = nftGroupService.listByObject(selectNftGroupDo);
        if (CollectionUtils.isNotEmpty(nftGroupDos)) {
            nftGroupDos.forEach(nftGroupDo -> {
                NftInfoDo selectNftInfoDo = new NftInfoDo();
                selectNftInfoDo.setGroupId(nftGroupDo.getId());
                selectNftInfoDo.setCreated(true);
                int count = nftInfoService.selectCountBySelective(selectNftInfoDo);
                nftGroupDo.setQuantity(count);
                nftGroupService.update(nftGroupDo);
            });
        }
        // update rank
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
        AtomicInteger lastRank = new AtomicInteger(0);
        List<NftInfoDo> updateList = new ArrayList<>();
        AtomicReference<BigDecimal> scoreReference = new AtomicReference<>();
        Long currentTime = LocalDateTimeUtil.getMilliByTime(LocalDateTime.now());

        sortList.forEach(p -> {
            NftInfoDo.NftInfoDoBuilder nftInfoBuilder = NftInfoDo.builder()
                    .id(p.getId())
                    .updateTime(currentTime);

            if (Objects.isNull(scoreReference.get())) {
                nftInfoBuilder.rank(rank.incrementAndGet());
                lastRank.set(rank.get());
            } else {
                if (p.getScore().compareTo(scoreReference.get()) == 0) {
                    nftInfoBuilder.rank(lastRank.get());
                    rank.incrementAndGet();
                }else{
                    nftInfoBuilder.rank(rank.incrementAndGet());
                    lastRank.set(rank.get());
                }
            }
            scoreReference.set(p.getScore());

            updateList.add(nftInfoBuilder.build());
        });

        updateList.forEach(p -> {
            nftInfoService.update(p);
        });
    }

}
