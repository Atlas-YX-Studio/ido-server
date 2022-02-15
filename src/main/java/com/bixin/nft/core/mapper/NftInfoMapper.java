package com.bixin.nft.core.mapper;

import com.bixin.nft.bean.DO.NftInfoDo;

import java.util.List;
import java.util.Map;

public interface NftInfoMapper {
    /**
     * 根据主键删除数据
     * @param id
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据
     * @param record
     */
    int insert(NftInfoDo record);

    /**
     * 根据主键id查询
     * @param id
     */
    NftInfoDo selectByPrimaryKey(Long id);

    /**
     * 根据主键id查询
     * @param id
     */
    NftInfoDo selectByPrimaryKeyWithBLOBs(Long id);

    /**
     * 修改数据
     * @param record
     */
    int updateByPrimaryKeySelective(NftInfoDo record);

    int updateByPrimaryKeyWithBLOBs(NftInfoDo record);

    /**
     * 根据条件查询对象
     * @param record
     */
    NftInfoDo selectByPrimaryKeySelective(NftInfoDo record);

    /**
     * 根据条件查询列表
     * @param record
     */
    List<NftInfoDo> selectByPrimaryKeySelectiveList(NftInfoDo record);


    List<NftInfoDo> selectByPage(Map<String,Object> map);

    List<NftInfoDo> selectAll4Rank(Map<String,Object> map);

    List<NftInfoDo> selectByIds(List<Long> list);

    List<NftInfoDo> selectByNftIds(Map<String,Object> map);

    int selectCountBySelective(NftInfoDo model);

}