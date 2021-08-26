package com.bixin.ido.server.scheduler;

import com.bixin.ido.server.bean.DO.IdoDxProduct;
import com.bixin.ido.server.core.redis.RedisCache;
import com.bixin.ido.server.enums.ProductState;
import com.bixin.ido.server.service.IDxProductService;
import com.bixin.ido.server.utils.LocalDateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author zhangcheng
 * create          2021-08-11 2:26 下午
 */
@Slf4j
@Component
public class ScheduleProduct {

    @Resource
    IDxProductService idoDxProductService;
    @Resource
    RedisCache redisCache;

    static final String UPDATE_PROCESSING_LOCK_KEY = "updateProduct4ProcessingTask";
    static final String UPDATE_FINISH_LOCK_KEY = "updateProduct4FinishTask";

    static final Long PROCESSING_EXPIRE_TIME = 30 * 1000L;
    static final Long FINISH_EXPIRE_TIME = 30 * 1000L;
    //如果未获取到锁，则持续等待【xx】时间ms后不在尝试获取
    static final Long LOCK_EXPIRE_TIME = 0L;


    @Scheduled(cron = "5 0/1 * * * ?")
    public void updateProduct4Processing() {

        String requestId = UUID.randomUUID().toString();
        Long currentTime = LocalDateTimeUtil.getMilliByTime(LocalDateTime.now());

        redisCache.tryGetDistributedLock(
                UPDATE_PROCESSING_LOCK_KEY,
                requestId,
                PROCESSING_EXPIRE_TIME,
                LOCK_EXPIRE_TIME,
                () -> {
                    List<IdoDxProduct> products = idoDxProductService.getProducts4UpdateState(currentTime, ProductState.PROCESSING);
                    if (CollectionUtils.isEmpty(products)) {
                        return null;
                    }

                    products.forEach(p -> {
                        p.setState(ProductState.PROCESSING.getDesc());
                        p.setUpdateTime(currentTime);
                        idoDxProductService.updateProduct(p);
                        log.info("scheduler product to processing {}", p);
                    });

                    return null;
                }
        );

    }

    @Scheduled(cron = "15 0/1 * * * ?")
    public void updateProduct4Finish() {

        String requestId = UUID.randomUUID().toString();
        Long currentTime = LocalDateTimeUtil.getMilliByTime(LocalDateTime.now());

        redisCache.tryGetDistributedLock(
                UPDATE_FINISH_LOCK_KEY,
                requestId,
                FINISH_EXPIRE_TIME,
                LOCK_EXPIRE_TIME,
                () -> {
                    List<IdoDxProduct> products = idoDxProductService.getProducts4UpdateState(currentTime, ProductState.FINISH);
                    if (CollectionUtils.isEmpty(products)) {
                        return null;
                    }

                    products.forEach(p -> {
                        p.setState(ProductState.FINISH.getDesc());
                        p.setUpdateTime(currentTime);
                        idoDxProductService.updateProduct(p);
                        log.info("scheduler product to finish {}", p);
                    });

                    return null;
                }
        );

    }


}
