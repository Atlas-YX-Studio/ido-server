package com.bixin.nft.service;

import com.bixin.nft.bean.DO.NftInfoDo;

import java.util.List;

/**
 * @interface: NftInfoService
 * @Description: NFT信息记录表 接口
 * @author: 系统
 * @created: 2021-09-15
 */
public interface NftInfoService {

    /**
     * @explain: 添加NftInfoDo对象
     * @param: model 对象参数
     * @return: int
     */
    int insert(NftInfoDo model);

    /**
     * @explain: 删除NftInfoDo对象
     * @param: id
     * @return: int
     */
    int deleteById(Long id);

    /**
     * @explain: 修改NftInfoDo对象
     * @param: model 对象参数
     * @return: int
     */
    int update(NftInfoDo model);

    /**
     * @explain: 查询NftInfoDo对象
     * @param: id
     * @return: NftInfoDo
     */
    NftInfoDo selectById(Long id);

    /**
     * @explain: 查询NftInfoDo对象
     * @param: id
     * @return: NftInfoDo
     */
    NftInfoDo selectByIdWithImage(Long id);

    /**
     * @explain: 查询NftInfoDo对象
     * @param: model  对象参数
     * @return: NftInfoDo 对象
     */
    NftInfoDo selectByObject(NftInfoDo model);

    /**
     * @explain: 查询列表
     * @param: model  对象参数
     * @return: list
     */
    List<NftInfoDo> listByObject(NftInfoDo model);


    List<NftInfoDo> selectByPage(boolean predicateNextPage, long pageNum, long pageSize, String order, String sort);

}
