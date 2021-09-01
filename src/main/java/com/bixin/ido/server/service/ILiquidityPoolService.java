package com.bixin.ido.server.service;

import com.bixin.ido.server.bean.DO.LiquidityPool;

import java.util.List;

/**
 * @author zhangcheng
 * create  2021-08-27 11:13 上午
 */
public interface ILiquidityPoolService {

    int insert(LiquidityPool record);

    List<LiquidityPool> getAllPools();

}
