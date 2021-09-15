package com.bixin.nft.core.service;

import com.bixin.nft.bean.DO.NftKikoCatDo;

import java.util.List;

/**
 * @interface: NftKikoCatService
 * @Description:  NFT Kiko猫信息表 接口
 * @author: 系统
 * @created: 2021-09-15
 */
public interface NftKikoCatService {

    /**
     * @explain: 添加NftKikoCatDo对象
     * @param:   model 对象参数
     * @return:  int
     */
    int insert(NftKikoCatDo model);

    /**
     * @explain: 删除NftKikoCatDo对象
     * @param:   id
     * @return:  int
     */
    int deleteById(Long id);

    /**
     * @explain: 修改NftKikoCatDo对象
     * @param:   model 对象参数
     * @return:  int
     */
    int update(NftKikoCatDo model);

    /**
     * @explain: 查询NftKikoCatDo对象
     * @param:   id
     * @return:  NftKikoCatDo
     */
    NftKikoCatDo selectById(Long id);

    /**
     * @explain: 查询NftKikoCatDo对象
     * @param:   model  对象参数
     * @return:  NftKikoCatDo 对象
     */
    NftKikoCatDo selectByObject(NftKikoCatDo model);

    /**
     * @explain: 查询列表
     * @param:  model  对象参数
     * @return: list
     */
    List<NftKikoCatDo> listByObject(NftKikoCatDo model);

}
