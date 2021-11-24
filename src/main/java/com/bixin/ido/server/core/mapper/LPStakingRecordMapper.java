package com.bixin.ido.server.core.mapper;

import com.bixin.ido.server.bean.DO.LPStakingRecordDo;

import java.util.List;

public interface LPStakingRecordMapper {
    /**
     * 根据主键删除数据
     * @param id
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据
     * @param record
     */
    int insert(LPStakingRecordDo record);

    /**
     * 根据主键id查询
     * @param id
     */
    LPStakingRecordDo selectByPrimaryKey(Long id);

    /**
     * 修改数据
     * @param record
     */
    int updateByPrimaryKeySelective(LPStakingRecordDo record);

    /**
     * 根据条件查询对象
     * @param record
     */
    LPStakingRecordDo selectByPrimaryKeySelective(LPStakingRecordDo record);

    /**
     * 根据条件查询列表
     * @param record
     */
    List<LPStakingRecordDo> selectByPrimaryKeySelectiveList(LPStakingRecordDo record);
}