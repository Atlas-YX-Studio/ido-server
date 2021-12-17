package com.bixin.nft.service.impl;

import com.bixin.nft.bean.DO.NftKikoCatDo;
import com.bixin.nft.core.mapper.NftKikoCatMapper;
import com.bixin.nft.service.NftKikoCatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @class: NftKikoCatServiceImpl
 * @Description:  NFT Kiko猫信息表 接口实现
 * @author: 系统
 * @created: 2021-09-15
 */
@Slf4j
@Service
public class NftKikoCatServiceImpl implements NftKikoCatService {

    @Autowired
    private NftKikoCatMapper nftKikoCatMapper;

    /**
     * @explain: 添加NftKikoCatDo对象
     * @param:   model 对象参数
     * @return:  int
     */
    @Override
    public int insert(NftKikoCatDo model) {
        return nftKikoCatMapper.insert(model);
    }

    /**
     * @explain: 删除NftKikoCatDo对象
     * @param:   id
     * @return:  int
     */
    @Override
    public int deleteById(Long id) {
        return nftKikoCatMapper.deleteByPrimaryKey(id);
    }

    /**
     * @explain: 修改NftKikoCatDo对象
     * @param:   model 对象参数
     * @return:  int
     */
    @Override
    public int update(NftKikoCatDo model) {
        return nftKikoCatMapper.updateByPrimaryKeySelective(model);
    }

    /**
     * @explain: 查询NftKikoCatDo对象
     * @param:   id
     * @return:  NftKikoCatDo
     */
    @Override
    public NftKikoCatDo selectById(Long id) {
        return nftKikoCatMapper.selectByPrimaryKey(id);
    }

    /**
     * @explain: 查询NftKikoCatDo对象
     * @param:   nftId
     * @return:  NftKikoCatDo
     */
    @Override
    public NftKikoCatDo selectByNftId(Long nftId) {
        return nftKikoCatMapper.selectByNftId(nftId);
    }

    /**
     * @explain: 查询NftKikoCatDo对象
     * @param:   model 对象参数
     * @return:  NftKikoCatDo 对象
     */
    @Override
    public NftKikoCatDo selectByObject(NftKikoCatDo model) {
        return nftKikoCatMapper.selectByPrimaryKeySelective(model);
    }

    /**
     * @explain: 查询列表
     * @param:  model  对象参数
     * @return: list
     */
    @Override
    public List<NftKikoCatDo> listByObject(NftKikoCatDo model) {
        return nftKikoCatMapper.selectByPrimaryKeySelectiveList(model);
    }

}
