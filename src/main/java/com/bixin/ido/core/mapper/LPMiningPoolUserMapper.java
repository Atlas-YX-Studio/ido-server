package com.bixin.ido.core.mapper;

import com.bixin.ido.bean.DO.LPMiningPoolUserDo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface LPMiningPoolUserMapper {
    /**
     * 根据主键删除数据
     * @param id
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据
     * @param record
     */
    int insert(LPMiningPoolUserDo record);

    /**
     * 根据主键id查询
     * @param id
     */
    LPMiningPoolUserDo selectByPrimaryKey(Long id);

    /**
     * 修改数据
     * @param record
     */
    int updateByPrimaryKeySelective(LPMiningPoolUserDo record);

    /**
     * 根据条件查询对象
     * @param record
     */
    LPMiningPoolUserDo selectByPrimaryKeySelective(LPMiningPoolUserDo record);

    /**
     * 根据条件查询列表
     * @param record
     */
    List<LPMiningPoolUserDo> selectByPrimaryKeySelectiveList(LPMiningPoolUserDo record);

    /**
     * 计算发放收益
     *
     * @param poolId
     * @param rewardAmount
     * @param updateTime
     * @return
     */
    int computeReward(@Param("poolId") Long poolId, @Param("rewardAmount") BigDecimal rewardAmount, @Param("updateTime") Long updateTime);


    /**
     * 提取收益
     * @param id
     * @param rewardAmount
     * @param updateTime
     * @return
     */
    int harvestReward(@Param("id") Long id, @Param("rewardAmount") BigDecimal rewardAmount, @Param("updateTime") Long updateTime);

    /**
     * 提取成功
     * @param id
     * @param rewardAmount
     * @param updateTime
     * @return
     */
    int harvestSuccess(@Param("id") Long id, @Param("rewardAmount") BigDecimal rewardAmount, @Param("updateTime") Long updateTime);

    /**
     * 提取失败
     * @param id
     * @param rewardAmount
     * @param updateTime
     * @return
     */
    int harvestFailed(@Param("id") Long id, @Param("rewardAmount") BigDecimal rewardAmount, @Param("updateTime") Long updateTime);


}