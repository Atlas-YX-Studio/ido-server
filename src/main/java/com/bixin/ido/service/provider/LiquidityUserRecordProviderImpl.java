package com.bixin.ido.service.provider;

import com.bixin.ido.bean.DO.LiquidityUserRecord;
import com.bixin.core.provider.IStarSwapProvider;
import com.bixin.ido.service.ILiquidityUserRecordService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zhangcheng
 * create          2021-08-25 2:52 下午
 */
@Component
public class LiquidityUserRecordProviderImpl implements IStarSwapProvider<LiquidityUserRecord> {

    @Resource
    ILiquidityUserRecordService liquidityUserRecordService;

    @Override
    public void dispatcher(LiquidityUserRecord liquidityUserRecord) {
        liquidityUserRecordService.insert(liquidityUserRecord);
    }

}
