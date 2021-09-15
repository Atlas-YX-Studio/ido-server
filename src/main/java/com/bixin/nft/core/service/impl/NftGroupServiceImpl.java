package com.bixin.nft.core.service.impl;

import com.bixin.nft.bean.DO.NftGroupDo;
import com.bixin.nft.core.mapper.NftGroupMapper;
import com.bixin.nft.core.service.NftGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @class: NftGroupServiceImpl
 * @Description:  NFT分组表 接口实现
 * @author: 系统
 * @created: 2021-09-15
 */
@Slf4j
@Service
public class NftGroupServiceImpl implements NftGroupService {

    @Autowired
    private NftGroupMapper nftGroupMapper;

    /**
     * @explain: 添加NftGroupDo对象
     * @param:   model 对象参数
     * @return:  int
     */
    @Override
    public int insert(NftGroupDo model) {
        return nftGroupMapper.insert(model);
    }

    /**
     * @explain: 删除NftGroupDo对象
     * @param:   id
     * @return:  int
     */
    @Override
    public int deleteById(Long id) {
        return nftGroupMapper.deleteByPrimaryKey(id);
    }

    /**
     * @explain: 修改NftGroupDo对象
     * @param:   model 对象参数
     * @return:  int
     */
    @Override
    public int update(NftGroupDo model) {
        return nftGroupMapper.updateByPrimaryKeySelective(model);
    }

    /**
     * @explain: 查询NftGroupDo对象
     * @param:   id
     * @return:  NftGroupDo
     */
    @Override
    public NftGroupDo selectById(Long id) {
        return nftGroupMapper.selectByPrimaryKey(id);
    }

    /**
     * @explain: 查询NftGroupDo对象
     * @param:   model 对象参数
     * @return:  NftGroupDo 对象
     */
    @Override
    public NftGroupDo selectByObject(NftGroupDo model) {
        return nftGroupMapper.selectByPrimaryKeySelective(model);
    }

    /**
     * @explain: 查询列表
     * @param:  model  对象参数
     * @return: list
     */
    @Override
    public List<NftGroupDo> listByObject(NftGroupDo model) {
        return nftGroupMapper.selectByPrimaryKeySelectiveList(model);
    }

}
