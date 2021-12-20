package com.bixin.ido.service;

import com.bixin.ido.bean.DO.SwapUserRecord;

import java.util.List;

/**
 * @author zhangcheng
 * create  2021-08-25 4:30 下午
 */
public interface ISwapUserRecordService {

    int insert(SwapUserRecord record);

    List<SwapUserRecord> getALlByPage(String userAddress, long pageSize, long nextId);

    Long countVisits(Long timestamp);

    List<String> selectAllAddress();
}
