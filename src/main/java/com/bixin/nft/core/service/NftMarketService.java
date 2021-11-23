package com.bixin.nft.core.service;

import com.bixin.nft.bean.DO.NftMarketDo;

import java.util.List;
import java.util.Map;

/**
 * @interface: NftMarketService
 * @Description: NFT/box市场销售列表 接口
 * @author: 系统
 * @created: 2021-09-17
 */
public interface NftMarketService {

    /**
     * @explain: 添加NftMarketDo对象
     * @param: model 对象参数
     * @return: int
     */
    int insert(NftMarketDo model);

    /**
     * @explain: 删除NftMarketDo对象
     * @param: id
     * @return: int
     */
    int deleteById(Long id);

    /**
     * @explain: 修改NftMarketDo对象
     * @param: model 对象参数
     * @return: int
     */
    int update(NftMarketDo model);

    /**
     * @explain: 查询NftMarketDo对象
     * @param: id
     * @return: NftMarketDo
     */
    NftMarketDo selectById(Long id);

    /**
     * @explain: 查询NftMarketDo对象
     * @param: model  对象参数
     * @return: NftMarketDo 对象
     */
    NftMarketDo selectByObject(NftMarketDo model);

    /**
     * @explain: 查询列表
     * @param: model  对象参数
     * @return: list
     */
    List<NftMarketDo> listByObject(NftMarketDo model);

    void deleteAll();

    List<Map<String, Object>> selectByPage(boolean predicateNextPage, long pageSize, long pageNum, int sort, long groupId, String currency, String open);

}
