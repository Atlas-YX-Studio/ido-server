package com.bixin.ido.server.scheduler;

import com.bixin.ido.server.bean.DO.IdoDxProduct;
import com.bixin.ido.server.core.redis.RedisCache;
import com.bixin.ido.server.enums.ProductState;
import com.bixin.ido.server.service.IIdoDxProductService;
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
    IIdoDxProductService idoDxProductService;
    @Resource
    RedisCache redisCache;

    static final String UPDATE_PROCESSING_LOCK_KEY = "updateProduct4ProcessingTask";
    static final String UPDATE_FINISH_LOCK_KEY = "updateProduct4FinishTask";

    static final Long PROCESSING_EXPIRE_TIME = 30 * 1000L;
    static final Long FINISH_EXPIRE_TIME = 30 * 1000L;


    @Scheduled(cron = "5 0/1 * * * ?")
    public void updateProduct4Processing() {

        String requestId = UUID.randomUUID().toString();

        redisCache.tryGetDistributedLock(
                UPDATE_PROCESSING_LOCK_KEY,
                requestId,
                PROCESSING_EXPIRE_TIME,
                () -> {
                    List<IdoDxProduct> products = idoDxProductService.getProducts(ProductState.INIT);
                    if (CollectionUtils.isEmpty(products)) {
                        return null;
                    }
                    Long currentTime = LocalDateTimeUtil.getMilliByTime(LocalDateTime.now());

                    products.forEach(p -> {
                        Long startTime = p.getStartTime();
                        Long endTime = p.getEndTime();

                        //由于是毫秒级别的判断，加上定时任务间隔，可以提前1秒判断
                        if (currentTime >= (startTime - 1000) && currentTime <= (endTime - 1000)) {
                            p.setState(ProductState.PROCESSING.getDesc());
                            p.setUpdateTime(currentTime);
                            idoDxProductService.updateProduct(p);
                            log.info("scheduler product to processing {}", p);
                        }

                    });

                    return null;
                }
        );

    }

    @Scheduled(cron = "15 0/1 * * * ?")
    public void updateProduct4Finish() {

        String requestId = UUID.randomUUID().toString();

        redisCache.tryGetDistributedLock(
                UPDATE_FINISH_LOCK_KEY,
                requestId,
                FINISH_EXPIRE_TIME,
                () -> {
                    List<IdoDxProduct> products = idoDxProductService.getProducts(ProductState.PROCESSING);
                    if (CollectionUtils.isEmpty(products)) {
                        return null;
                    }
                    Long currentTime = LocalDateTimeUtil.getMilliByTime(LocalDateTime.now());

                    products.forEach(p -> {
                        Long endTime = p.getEndTime();

                        //由于是毫秒级别的判断，加上定时任务间隔，可以提前1秒判断
                        if (currentTime >= (endTime - 1000)) {
                            p.setState(ProductState.FINISH.getDesc());
                            p.setUpdateTime(currentTime);
                            idoDxProductService.updateProduct(p);
                            log.info("scheduler product to finish {}", p);
                        }

                    });

                    return null;
                }
        );

    }


}
