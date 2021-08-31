package com.bixin.ido.server.service;

import com.bixin.ido.server.bean.DO.LiquidityUserRecord;

import java.util.List;

/**
 * @author zhangcheng
 * create  2021-08-25 5:13 下午
 */
public interface ILiquidityUserRecordService {

    int insert(LiquidityUserRecord record);

    List<LiquidityUserRecord> getALlByPage(String userAddress, long pageSize, long nextId);

}
