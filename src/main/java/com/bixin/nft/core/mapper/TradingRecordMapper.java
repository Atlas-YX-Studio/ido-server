package com.bixin.nft.core.mapper;

import com.bixin.nft.bean.DO.TradingRecordDo;

import java.util.List;
import java.util.Map;

public interface TradingRecordMapper {
    List<TradingRecordDo> selectByPage(Map<String, Object> paramMap);

    /**
     * 根据主键删除数据
     * @param id
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据
     * @param record
     */
    int insert(TradingRecordDo record);

    /**
     * 根据主键id查询
     * @param id
     */
    TradingRecordDo selectByPrimaryKey(Long id);

    /**
     * 修改数据
     * @param record
     */
    int updateByPrimaryKeySelective(TradingRecordDo record);

    /**
     * 根据条件查询对象
     * @param record
     */
    TradingRecordDo selectByPrimaryKeySelective(TradingRecordDo record);

    /**
     * 根据条件查询列表
     * @param record
     */
    List<TradingRecordDo> selectByPrimaryKeySelectiveList(TradingRecordDo record);
}