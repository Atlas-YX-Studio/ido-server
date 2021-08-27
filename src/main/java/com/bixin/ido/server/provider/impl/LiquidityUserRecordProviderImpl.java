package com.bixin.ido.server.provider.impl;

import com.bixin.ido.server.bean.DO.LiquidityUserRecord;
import com.bixin.ido.server.provider.IStarSwapProvider;
import com.bixin.ido.server.service.ILiquidityUserRecordService;
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
