package com.bixin.ido.server.provider.impl;

import com.bixin.ido.server.bean.DO.LiquidityPool;
import com.bixin.ido.server.provider.IStarSwapProvider;
import com.bixin.ido.server.service.ILiquidityPoolService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author
 * create  2021-08-27 11:11 上午
 */
@Component
public class LiquidityPoolProviderImpl implements IStarSwapProvider<LiquidityPool> {

    @Resource
    ILiquidityPoolService liquidityPoolService;

    @Override
    public void dispatcher(LiquidityPool liquidityPool) {

    }

}
