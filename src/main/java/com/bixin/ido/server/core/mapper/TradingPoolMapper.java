package com.bixin.ido.server.core.mapper;

import com.bixin.ido.server.bean.DO.TradingPoolDo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TradingPoolMapper {
    /**
     * 根据主键删除数据
     * @param id
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据
     * @param record
     */
    int insert(TradingPoolDo record);

    /**
     * 根据主键id查询
     * @param id
     */
    TradingPoolDo selectByPrimaryKey(Long id);

    /**
     * 修改数据
     * @param record
     */
    int updateByPrimaryKeySelective(TradingPoolDo record);

    /**
     * 根据条件查询对象
     * @param record
     */
    TradingPoolDo selectByPrimaryKeySelective(TradingPoolDo record);

    /**
     * 根据条件查询列表
     * @param record
     */
    List<TradingPoolDo> selectByPrimaryKeySelectiveList(TradingPoolDo record);

    /**
     * 更新已发放收益
     *
     * @param updateTime
     * @return
     */
    int updateStatistic(@Param("updateTime") Long updateTime);
}