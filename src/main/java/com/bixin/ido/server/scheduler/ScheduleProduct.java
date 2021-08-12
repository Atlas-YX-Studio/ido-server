package com.bixin.ido.server.scheduler;

import com.bixin.ido.server.bean.DO.IdoDxProduct;
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

/**
 * @author zhangcheng
 * create          2021-08-11 2:26 下午
 */
@Slf4j
@Component
public class ScheduleProduct {

    @Resource
    IIdoDxProductService idoDxProductService;

    @Scheduled(cron = "5 0/1 * * * ?")
    public void updateProduct4Processing() {
        List<IdoDxProduct> products = idoDxProductService.getProducts(ProductState.INIT);
        if (CollectionUtils.isEmpty(products)) {
            return;
        }

        Long currentTime = LocalDateTimeUtil.getMilliByTime(LocalDateTime.now());

        products.forEach(p -> {
            Long startTime = p.getStartTime();
            Long endTime = p.getEndTime();

            //由于是毫秒级别的判断，加上定时任务间隔，可以提前1秒判断
            if (currentTime >= (startTime - 1000) && currentTime <= (endTime - 1000)) {
                p.setState(ProductState.PROCESSING.getDesc());
                idoDxProductService.updateProduct(p);
                log.info("scheduler product to processing {}", p);
            }

        });

    }

    @Scheduled(cron = "15 0/1 * * * ?")
    public void updateProduct4Finish() {
        List<IdoDxProduct> products = idoDxProductService.getProducts(ProductState.PROCESSING);
        if (CollectionUtils.isEmpty(products)) {
            return;
        }

        Long currentTime = LocalDateTimeUtil.getMilliByTime(LocalDateTime.now());

        products.forEach(p -> {
            Long endTime = p.getEndTime();

            //由于是毫秒级别的判断，加上定时任务间隔，可以提前1秒判断
            if (currentTime >= (endTime - 1000)) {
                p.setState(ProductState.FINISH.getDesc());
                idoDxProductService.updateProduct(p);
                log.info("scheduler product to finish {}", p);
            }

        });

    }


}
