package com.bixin.ido.server.core.mapper;


import com.bixin.ido.server.bean.DO.TradingPoolUserDo;

import java.util.List;

public interface TradingPoolUserMapper {
    /**
     * 根据主键删除数据
     * @param id
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据
     * @param record
     */
    int insert(TradingPoolUserDo record);

    /**
     * 根据主键id查询
     * @param id
     */
    TradingPoolUserDo selectByPrimaryKey(Long id);

    /**
     * 修改数据
     * @param record
     */
    int updateByPrimaryKeySelective(TradingPoolUserDo record);

    /**
     * 根据条件查询对象
     * @param record
     */
    TradingPoolUserDo selectByPrimaryKeySelective(TradingPoolUserDo record);

    /**
     * 根据条件查询列表
     * @param record
     */
    List<TradingPoolUserDo> selectByPrimaryKeySelectiveList(TradingPoolUserDo record);
}