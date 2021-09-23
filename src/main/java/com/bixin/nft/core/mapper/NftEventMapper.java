package com.bixin.nft.core.mapper;

import com.bixin.nft.bean.DO.NftEventDo;

import java.util.List;
import java.util.Map;

public interface NftEventMapper {
    /**
     * 根据主键删除数据
     * @param id
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据
     * @param record
     */
    int insert(NftEventDo record);

    /**
     * 根据主键id查询
     * @param id
     */
    NftEventDo selectByPrimaryKey(Long id);

    /**
     * 修改数据
     * @param record
     */
    int updateByPrimaryKeySelective(NftEventDo record);

    /**
     * 根据条件查询对象
     * @param record
     */
    NftEventDo selectByPrimaryKeySelective(NftEventDo record);

    /**
     * 根据条件查询列表
     * @param record
     */
    List<NftEventDo> selectByPrimaryKeySelectiveList(NftEventDo record);


    List<NftEventDo> selectByPage(Map<String,Object> paramMap);
}