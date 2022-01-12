package com.bixin.nft.service.impl;

import com.bixin.nft.bean.DO.NftMarketDo;
import com.bixin.nft.core.mapper.NftGroupMapper;
import com.bixin.nft.core.mapper.NftMarketMapper;
import com.bixin.nft.service.NftMarketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @class: NftMarketServiceImpl
 * @Description: NFT/box市场销售列表 接口实现
 * @author: 系统
 * @created: 2021-09-17
 */
@Slf4j
@Service
public class NftMarketServiceImpl implements NftMarketService {


    @Resource
    private NftMarketMapper nftMarketMapper;
    @Resource
    private NftGroupMapper nftGroupMapper;

    /**
     * @explain: 添加NftMarketDo对象
     * @param: model 对象参数
     * @return: int
     */
    @Override
    public int insert(NftMarketDo model) {
        return nftMarketMapper.insert(model);
    }

    /**
     * @explain: 删除NftMarketDo对象
     * @param: id
     * @return: int
     */
    @Override
    public int deleteById(Long id) {
        return nftMarketMapper.deleteByPrimaryKey(id);
    }

    /**
     * @explain: 修改NftMarketDo对象
     * @param: model 对象参数
     * @return: int
     */
    @Override
    public int update(NftMarketDo model) {
        return nftMarketMapper.updateByPrimaryKeySelective(model);
    }

    /**
     * @explain: 查询NftMarketDo对象
     * @param: id
     * @return: NftMarketDo
     */
    @Override
    public NftMarketDo selectById(Long id) {
        return nftMarketMapper.selectByPrimaryKey(id);
    }

    /**
     * @explain: 查询NftMarketDo对象
     * @param: model 对象参数
     * @return: NftMarketDo 对象
     */
    @Override
    public NftMarketDo selectByObject(NftMarketDo model) {
        return nftMarketMapper.selectByPrimaryKeySelective(model);
    }


    /**
     * @explain: 查询列表
     * @param: model  对象参数
     * @return: list
     */
    @Override
    public List<NftMarketDo> listByObject(NftMarketDo model) {
        return nftMarketMapper.selectByPrimaryKeySelectiveList(model);
    }


    @Override
    public void deleteAll() {
        nftMarketMapper.deleteAll();
    }


    @Override
    public void deleteAllByGroupIds(List<Long> groupIds) {
        nftMarketMapper.deleteAllByGroupIds(groupIds);
    }

    @Override
    public void deleteAllByGroupIdTypes(Map<Long, Object> groupIdTypeMap) {
        nftMarketMapper.deleteAllByGroupIdTypes(groupIdTypeMap);
    }

    @Override
    public void deleteAllByIds(List<Long> ids) {
        nftMarketMapper.deleteAllByIds(ids);
    }

    /**
     * @param predicateNextPage
     * @param pageSize
     * @param pageNum
     * @param sort
     * @param groupId
     * @param nftType
     * @return
     */
    @Override
    public List<Map<String, Object>> selectByPage(boolean predicateNextPage, long pageSize, long pageNum, int sort, long groupId, String sortRule, String nftType) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("pageSize", pageSize);
        paramMap.put("pageFrom", predicateNextPage ? (pageNum - 1) * (pageSize - 1) : (pageNum - 1) * pageSize);
        paramMap.put("type", nftType);
        if (groupId > 0) {
            paramMap.put("groupId", groupId);
        }

        String sortValue = "";
        if ("price".equalsIgnoreCase(sortRule.trim())) {
            sortValue = "mm.sell_price ";
        } else if ("rarity".equalsIgnoreCase(sortRule.trim())) {
            sortValue = "ff.score ";
        } else if ("ctime".equalsIgnoreCase(sortRule.trim())) {
            sortValue = "mm.create_time ";
        }
        if (0 == sort || 1 == sort) {
            if (!"ctime".equalsIgnoreCase(sortRule.trim())) {
                sortValue += " desc, mm.create_time desc";
            } else {
                sortValue += " desc";
            }
        } else if (2 == sort) {
            if (!"ctime".equalsIgnoreCase(sortRule.trim())) {
                sortValue += " asc, mm.create_time desc";
            } else {
                sortValue += " asc";
            }
        }
        paramMap.put("sort", sortValue);

        return nftMarketMapper.selectPages(paramMap);
    }

    @Override
    public List<Map<String, Object>> selectScoreByOwner(String owner) {
        return nftMarketMapper.selectScoreByOwner(owner);
    }

}
