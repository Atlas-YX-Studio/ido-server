package com.bixin.nft.service.impl;

import com.bixin.nft.bean.DO.NftGroupDo;
import com.bixin.nft.core.mapper.NftGroupMapper;
import com.bixin.nft.service.NftGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @class: NftGroupServiceImpl
 * @Description: NFT分组表 接口实现
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
     * @param: model 对象参数
     * @return: int
     */
    @Override
    public int insert(NftGroupDo model) {
        return nftGroupMapper.insert(model);
    }

    /**
     * @explain: 删除NftGroupDo对象
     * @param: id
     * @return: int
     */
    @Override
    public int deleteById(Long id) {
        return nftGroupMapper.deleteByPrimaryKey(id);
    }

    /**
     * @explain: 修改NftGroupDo对象
     * @param: model 对象参数
     * @return: int
     */
    @Override
    public int update(NftGroupDo model) {
        return nftGroupMapper.updateByPrimaryKeySelective(model);
    }

    /**
     * @explain: 查询NftGroupDo对象
     * @param: id
     * @return: NftGroupDo
     */
    @Override
    public NftGroupDo selectById(Long id) {
        return nftGroupMapper.selectByPrimaryKey(id);
    }

    /**
     * @explain: 查询NftGroupDo对象
     * @param: model 对象参数
     * @return: NftGroupDo 对象
     */
    @Override
    public NftGroupDo selectByObject(NftGroupDo model) {
        return nftGroupMapper.selectByPrimaryKeySelective(model);
    }

    /**
     * @explain: 查询列表
     * @param: model  对象参数
     * @return: list
     */
    @Override
    public List<NftGroupDo> listByObject(NftGroupDo model) {
        return nftGroupMapper.selectByPrimaryKeySelectiveList(model);
    }

    /**
     * @explain: 待发售盲盒
     */
    @Override
    public NftGroupDo offering(Boolean offering) {
        return nftGroupMapper.offering(offering);
    }

    @Override
    public List<NftGroupDo> getListByEnabled(Boolean enabled) {
        return nftGroupMapper.getListByEnabled(enabled);
    }

    /**
     * 根据分页查询盲盒
     *
     * @param offering
     * @param pageSize
     * @param pageNum
     * @return
     */
    @Override
    public List<NftGroupDo> getListByPage(Boolean offering, long pageSize, long pageNum, boolean predicateNextPage) {
        Map<String, Object> paramMap = new HashMap<>();
        Optional.ofNullable(offering).filter(Objects::nonNull).ifPresent(data -> paramMap.put("offering", offering));
        paramMap.put("pageSize", pageSize);
        paramMap.put("offset", predicateNextPage ? (pageNum - 1) * (pageSize - 1) : (pageNum - 1) * pageSize);
        paramMap.put("sort", "id");
        paramMap.put("order", "desc");
        return nftGroupMapper.selectByPage(paramMap);
    }

}
