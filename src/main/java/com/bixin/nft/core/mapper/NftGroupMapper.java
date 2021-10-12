package com.bixin.nft.core.mapper;

import com.bixin.nft.bean.DO.NftGroupDo;

import java.util.List;
import java.util.Map;

public interface NftGroupMapper {
    /**
     * 根据主键删除数据
     * @param id
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据
     * @param record
     */
    int insert(NftGroupDo record);

    /**
     * 根据主键id查询
     * @param id
     */
    NftGroupDo selectByPrimaryKey(Long id);

    /**
     * 修改数据
     * @param record
     */
    int updateByPrimaryKeySelective(NftGroupDo record);

    int updateByPrimaryKeyWithBLOBs(NftGroupDo record);

    /**
     * 根据条件查询对象
     * @param record
     */
    NftGroupDo selectByPrimaryKeySelective(NftGroupDo record);

    /**
     * 根据条件查询列表
     * @param record
     */
    List<NftGroupDo> selectByPrimaryKeySelectiveList(NftGroupDo record);

    /**
     * 待发售盲盒
     */
    NftGroupDo offering(Boolean offering);

    List<NftGroupDo> getListByEnabled(Boolean enabled);

    /**
     * 根据分页查询
     * @param paramMap
     * @return
     */
    List<NftGroupDo> selectByPage(Map<String,Object> paramMap);
}