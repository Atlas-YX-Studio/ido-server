package com.bixin.ido.server.core.mapper;


import com.bixin.ido.server.bean.DO.TradingPoolUserDo;

import java.math.BigDecimal;
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

    /**
     * 累加交易额
     * @param id
     * @param tradingAmount
     * @return
     */
    int addTradingAmount(Long id, BigDecimal tradingAmount, Long updateTime);

    /**
     * 提取收益
     * @param id
     * @param tradingAmount
     * @param rewardAmount
     * @param updateTime
     * @return
     */
    int harvestReward(Long id, BigDecimal tradingAmount, BigDecimal rewardAmount, Long updateTime);

    /**
     * 提取成功
     * @param id
     * @param tradingAmount
     * @param rewardAmount
     * @param updateTime
     * @return
     */
    int harvestSuccess(Long id, BigDecimal tradingAmount, BigDecimal rewardAmount, Long updateTime);

    /**
     * 提取失败
     * @param id
     * @param tradingAmount
     * @param rewardAmount
     * @param updateTime
     * @return
     */
    int harvestFailed(Long id, BigDecimal tradingAmount, BigDecimal rewardAmount, Long updateTime);

    /**
     * 衰减
     *
     * @param rate
     * @param updateTime
     * @return
     */
    int attenuation(BigDecimal rate, Long updateTime);

    /**
     * 计算发放收益
     *
     * @param poolId
     * @param rewardAmount
     * @param updateTime
     * @return
     */
    int currentReward(Long poolId, BigDecimal rewardAmount, Long updateTime);

}