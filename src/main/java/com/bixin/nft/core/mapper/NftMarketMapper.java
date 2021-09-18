package com.bixin.nft.core.mapper;

import com.bixin.nft.bean.DO.NftMarketDo;

import java.util.List;

public interface NftMarketMapper {
    /**
     * 根据主键删除数据
     * @param id
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据
     * @param record
     */
    int insert(NftMarketDo record);

    /**
     * 根据主键id查询
     * @param id
     */
    NftMarketDo selectByPrimaryKey(Long id);

    /**
     * 修改数据
     * @param record
     */
    int updateByPrimaryKeySelective(NftMarketDo record);

    /**
     * 根据条件查询对象
     * @param record
     */
    NftMarketDo selectByPrimaryKeySelective(NftMarketDo record);

    /**
     * 根据条件查询列表
     * @param record
     */
    List<NftMarketDo> selectByPrimaryKeySelectiveList(NftMarketDo record);

    void deleteAll();
}