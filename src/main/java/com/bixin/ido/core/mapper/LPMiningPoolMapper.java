package com.bixin.ido.core.mapper;

import com.bixin.ido.bean.DO.LPMiningPoolDo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LPMiningPoolMapper {
    /**
     * 根据主键删除数据
     * @param id
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据
     * @param record
     */
    int insert(LPMiningPoolDo record);

    /**
     * 根据主键id查询
     * @param id
     */
    LPMiningPoolDo selectByPrimaryKey(Long id);

    /**
     * 修改数据
     * @param record
     */
    int updateByPrimaryKeySelective(LPMiningPoolDo record);

    /**
     * 根据条件查询对象
     * @param record
     */
    LPMiningPoolDo selectByPrimaryKeySelective(LPMiningPoolDo record);

    /**
     * 根据条件查询列表
     * @param record
     */
    List<LPMiningPoolDo> selectByPrimaryKeySelectiveList(LPMiningPoolDo record);


    /**
     * 更新已发放收益
     *
     * @param updateTime
     * @return
     */
    int updateStatistic(@Param("updateTime") Long updateTime);
}