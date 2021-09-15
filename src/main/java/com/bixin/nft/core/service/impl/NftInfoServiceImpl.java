package com.bixin.nft.core.service.impl;

import com.bixin.nft.bean.DO.NftInfoDo;
import com.bixin.nft.core.mapper.NftInfoMapper;
import com.bixin.nft.core.service.NftInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @class: NftInfoServiceImpl
 * @Description:  NFT信息记录表 接口实现
 * @author: 系统
 * @created: 2021-09-15
 */
@Slf4j
@Service
public class NftInfoServiceImpl implements NftInfoService {

    @Autowired
    private NftInfoMapper nftInfoMapper;

    /**
     * @explain: 添加NftInfoDo对象
     * @param:   model 对象参数
     * @return:  int
     */
    @Override
    public int insert(NftInfoDo model) {
        return nftInfoMapper.insert(model);
    }

    /**
     * @explain: 删除NftInfoDo对象
     * @param:   id
     * @return:  int
     */
    @Override
    public int deleteById(Long id) {
        return nftInfoMapper.deleteByPrimaryKey(id);
    }

    /**
     * @explain: 修改NftInfoDo对象
     * @param:   model 对象参数
     * @return:  int
     */
    @Override
    public int update(NftInfoDo model) {
        return nftInfoMapper.updateByPrimaryKeySelective(model);
    }

    /**
     * @explain: 查询NftInfoDo对象
     * @param:   id
     * @return:  NftInfoDo
     */
    @Override
    public NftInfoDo selectById(Long id) {
        return nftInfoMapper.selectByPrimaryKey(id);
    }

    /**
     * @explain: 查询NftInfoDo对象
     * @param:   model 对象参数
     * @return:  NftInfoDo 对象
     */
    @Override
    public NftInfoDo selectByObject(NftInfoDo model) {
        return nftInfoMapper.selectByPrimaryKeySelective(model);
    }

    /**
     * @explain: 查询列表
     * @param:  model  对象参数
     * @return: list
     */
    @Override
    public List<NftInfoDo> listByObject(NftInfoDo model) {
        return nftInfoMapper.selectByPrimaryKeySelectiveList(model);
    }

}
