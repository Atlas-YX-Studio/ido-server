package com.bixin.ido.server.core.mapper;


import com.bixin.ido.server.bean.DO.TradingRewardUserDo;

import java.math.BigDecimal;
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

    /**
     * 提取收益
     * @param id
     * @param rewardAmount
     * @param updateTime
     * @return
     */
    int harvestReward(Long id, BigDecimal rewardAmount, Long updateTime);

    /**
     * 提取成功
     * @param id
     * @param rewardAmount
     * @param updateTime
     * @return
     */
    int harvestSuccess(Long id, BigDecimal rewardAmount, Long updateTime);

    /**
     * 提取失败
     * @param id
     * @param rewardAmount
     * @param updateTime
     * @return
     */
    int harvestFailed(Long id, BigDecimal rewardAmount, Long updateTime);

}