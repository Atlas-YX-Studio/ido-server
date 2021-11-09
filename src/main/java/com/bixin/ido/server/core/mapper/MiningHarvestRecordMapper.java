package com.bixin.ido.server.core.mapper;


import com.bixin.ido.server.bean.DO.MiningHarvestRecordDo;

import java.util.List;

public interface MiningHarvestRecordMapper {
    /**
     * 根据主键删除数据
     * @param id
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据
     * @param record
     */
    int insert(MiningHarvestRecordDo record);

    /**
     * 根据主键id查询
     * @param id
     */
    MiningHarvestRecordDo selectByPrimaryKey(Long id);

    /**
     * 修改数据
     * @param record
     */
    int updateByPrimaryKeySelective(MiningHarvestRecordDo record);

    /**
     * 根据条件查询对象
     * @param record
     */
    MiningHarvestRecordDo selectByPrimaryKeySelective(MiningHarvestRecordDo record);

    /**
     * 根据条件查询列表
     * @param record
     */
    List<MiningHarvestRecordDo> selectByPrimaryKeySelectiveList(MiningHarvestRecordDo record);
}