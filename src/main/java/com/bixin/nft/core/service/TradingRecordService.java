package com.bixin.nft.core.service;

import com.bixin.nft.bean.DO.TradingRecordDo;

import java.util.List;

public interface TradingRecordService {

    List<TradingRecordDo> selectByPage(long pageSize, long pageNum, String address, String direction);

    /**
     * @explain: 添加TradingRecordDo对象
     * @param:   model 对象参数
     * @return:  int
     */
    int insert(TradingRecordDo model);

    /**
     * @explain: 删除TradingRecordDo对象
     * @param:   id
     * @return:  int
     */
    int deleteById(Long id);

    /**
     * @explain: 修改TradingRecordDo对象
     * @param:   model 对象参数
     * @return:  int
     */
    int update(TradingRecordDo model);

    /**
     * @explain: 查询TradingRecordDo对象
     * @param:   id
     * @return:  TradingRecordDo
     */
    TradingRecordDo selectById(Long id);

    /**
     * @explain: 查询TradingRecordDo对象
     * @param:   model  对象参数
     * @return:  TradingRecordDo 对象
     */
    TradingRecordDo selectByObject(TradingRecordDo model);

    /**
     * @explain: 查询列表
     * @param:  model  对象参数
     * @return: list
     */
    List<TradingRecordDo> listByObject(TradingRecordDo model);
}
