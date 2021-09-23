package com.bixin.nft.core.service;

import com.bixin.nft.bean.DO.NftEventDo;

import java.util.List;

/**
 * @interface: NftEventService
 * @Description:  nft事件表 接口
 * @author: 系统
 * @created: 2021-09-22
 */
public interface NftEventService {

    /**
     * @explain: 添加NftEventDo对象
     * @param:   model 对象参数
     * @return:  int
     */
    int insert(NftEventDo model);

    /**
     * @explain: 删除NftEventDo对象
     * @param:   id
     * @return:  int
     */
    int deleteById(Long id);

    /**
     * @explain: 修改NftEventDo对象
     * @param:   model 对象参数
     * @return:  int
     */
    int update(NftEventDo model);

    /**
     * @explain: 查询NftEventDo对象
     * @param:   id
     * @return:  NftEventDo
     */
    NftEventDo selectById(Long id);

    /**
     * @explain: 查询NftEventDo对象
     * @param:   model  对象参数
     * @return:  NftEventDo 对象
     */
    NftEventDo selectByObject(NftEventDo model);

    /**
     * @explain: 查询列表
     * @param:  model  对象参数
     * @return: list
     */
    List<NftEventDo> listByObject(NftEventDo model);

}
