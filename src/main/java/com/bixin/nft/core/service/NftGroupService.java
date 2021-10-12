package com.bixin.nft.core.service;

import com.bixin.nft.bean.DO.NftGroupDo;

import java.util.List;

/**
 * @interface: NftGroupService
 * @Description:  NFT分组表 接口
 * @author: 系统
 * @created: 2021-09-15
 */
public interface NftGroupService {

    /**
     * @explain: 添加NftGroupDo对象
     * @param:   model 对象参数
     * @return:  int
     */
    int insert(NftGroupDo model);

    /**
     * @explain: 删除NftGroupDo对象
     * @param:   id
     * @return:  int
     */
    int deleteById(Long id);

    /**
     * @explain: 修改NftGroupDo对象
     * @param:   model 对象参数
     * @return:  int
     */
    int update(NftGroupDo model);

    /**
     * @explain: 查询NftGroupDo对象
     * @param:   id
     * @return:  NftGroupDo
     */
    NftGroupDo selectById(Long id);

    /**
     * @explain: 查询NftGroupDo对象
     * @param:   model  对象参数
     * @return:  NftGroupDo 对象
     */
    NftGroupDo selectByObject(NftGroupDo model);

    /**
     * @explain: 查询列表
     * @param:  model  对象参数
     * @return: list
     */
    List<NftGroupDo> listByObject(NftGroupDo model);

    /**
     * 待发售盲盒
     * @return
     */
    NftGroupDo offering(Boolean offering);

    List<NftGroupDo> getListByEnabled(Boolean enabled);

    /**
     * 分页查询
     * @param enabled
     * @param pageSize
     * @param pageNum
     * @return
     */
    List<NftGroupDo> getListByPage(Boolean enabled, long pageSize, long pageNum);

}
