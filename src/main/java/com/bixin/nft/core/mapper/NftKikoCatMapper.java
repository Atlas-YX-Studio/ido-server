package com.bixin.nft.core.mapper;

import com.bixin.nft.bean.DO.NftKikoCatDo;

import java.util.List;

public interface NftKikoCatMapper {
    /**
     * 根据主键删除数据
     * @param id
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据
     * @param record
     */
    int insert(NftKikoCatDo record);

    /**
     * 根据主键id查询
     * @param id
     */
    NftKikoCatDo selectByPrimaryKey(Long id);

    /**
     * 修改数据
     * @param record
     */
    int updateByPrimaryKeySelective(NftKikoCatDo record);

    /**
     * 根据条件查询对象
     * @param record
     */
    NftKikoCatDo selectByPrimaryKeySelective(NftKikoCatDo record);

    /**
     * 根据条件查询列表
     * @param record
     */
    List<NftKikoCatDo> selectByPrimaryKeySelectiveList(NftKikoCatDo record);
}