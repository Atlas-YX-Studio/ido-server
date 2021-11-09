package com.bixin.ido.server.core.mapper;


import com.bixin.ido.server.bean.DO.TradingRewardUserDo;

import java.util.List;

public interface TradingRewardUserMapper {
    /**
     * 根据主键删除数据
     * @param id
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据
     * @param record
     */
    int insert(TradingRewardUserDo record);

    /**
     * 根据主键id查询
     * @param id
     */
    TradingRewardUserDo selectByPrimaryKey(Long id);

    /**
     * 修改数据
     * @param record
     */
    int updateByPrimaryKeySelective(TradingRewardUserDo record);

    /**
     * 根据条件查询对象
     * @param record
     */
    TradingRewardUserDo selectByPrimaryKeySelective(TradingRewardUserDo record);

    /**
     * 根据条件查询列表
     * @param record
     */
    List<TradingRewardUserDo> selectByPrimaryKeySelectiveList(TradingRewardUserDo record);
}