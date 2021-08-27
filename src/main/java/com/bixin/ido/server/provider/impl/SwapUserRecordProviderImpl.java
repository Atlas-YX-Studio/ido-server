package com.bixin.ido.server.provider.impl;

import com.bixin.ido.server.bean.DO.SwapUserRecord;
import com.bixin.ido.server.provider.IStarSwapProvider;
import com.bixin.ido.server.service.ISwapUserRecordService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zhangcheng
 * create  2021-08-25 3:04 下午
 */
@Component
public class SwapUserRecordProviderImpl implements IStarSwapProvider<SwapUserRecord> {

    @Resource
    ISwapUserRecordService swapUserRecordService;

    @Override
    public void dispatcher(SwapUserRecord idoSwapUserRecord) {
        swapUserRecordService.insert(idoSwapUserRecord);
    }

}
