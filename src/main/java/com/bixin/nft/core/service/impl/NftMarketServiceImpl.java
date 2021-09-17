package com.bixin.nft.core.service.impl;

import com.bixin.nft.bean.DO.NftMarketDo;
import com.bixin.nft.core.mapper.NftMarketMapper;
import com.bixin.nft.core.service.NftMarketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @class: NftMarketServiceImpl
 * @Description:  NFT/box市场销售列表 接口实现
 * @author: 系统
 * @created: 2021-09-17
 */
@Slf4j
@Service
public class NftMarketServiceImpl implements NftMarketService {

    @Autowired
    private NftMarketMapper nftMarketMapper;

    /**
     * @explain: 添加NftMarketDo对象
     * @param:   model 对象参数
     * @return:  int
     */
    @Override
    public int insert(NftMarketDo model) {
        return nftMarketMapper.insert(model);
    }

    /**
     * @explain: 删除NftMarketDo对象
     * @param:   id
     * @return:  int
     */
    @Override
    public int deleteById(Long id) {
        return nftMarketMapper.deleteByPrimaryKey(id);
    }

    /**
     * @explain: 修改NftMarketDo对象
     * @param:   model 对象参数
     * @return:  int
     */
    @Override
    public int update(NftMarketDo model) {
        return nftMarketMapper.updateByPrimaryKeySelective(model);
    }

    /**
     * @explain: 查询NftMarketDo对象
     * @param:   id
     * @return:  NftMarketDo
     */
    @Override
    public NftMarketDo selectById(Long id) {
        return nftMarketMapper.selectByPrimaryKey(id);
    }

    /**
     * @explain: 查询NftMarketDo对象
     * @param:   model 对象参数
     * @return:  NftMarketDo 对象
     */
    @Override
    public NftMarketDo selectByObject(NftMarketDo model) {
        return nftMarketMapper.selectByPrimaryKeySelective(model);
    }

    /**
     * @explain: 查询列表
     * @param:  model  对象参数
     * @return: list
     */
    @Override
    public List<NftMarketDo> listByObject(NftMarketDo model) {
        return nftMarketMapper.selectByPrimaryKeySelectiveList(model);
    }

}
