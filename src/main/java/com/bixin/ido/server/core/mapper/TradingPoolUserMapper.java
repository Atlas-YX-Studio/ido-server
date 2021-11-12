package com.bixin.ido.server.core.mapper;


import com.bixin.ido.server.bean.DO.TradingPoolUserDo;

<<<<<<< HEAD
import java.math.BigDecimal;
=======
>>>>>>> c0d1f3c80114c9ef73676cd582dc8ae73da841f8
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
<<<<<<< HEAD

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

=======
>>>>>>> c0d1f3c80114c9ef73676cd582dc8ae73da841f8
}