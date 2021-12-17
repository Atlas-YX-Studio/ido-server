package com.bixin.nft.service.impl;

import com.bixin.nft.bean.DO.NftMarketDo;
import com.bixin.nft.core.mapper.NftGroupMapper;
import com.bixin.nft.core.mapper.NftMarketMapper;
import com.bixin.nft.service.NftMarketService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    @Override
    public List<Map<String, Object>> selectByPage(boolean predicateNextPage, long pageSize, long pageNum, int sort, long groupId, String currency, String open) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("pageSize", pageSize);
        paramMap.put("pageFrom", predicateNextPage ? (pageNum - 1) * (pageSize - 1) : (pageNum - 1) * pageSize);
        if (StringUtils.isNoneEmpty(open) && !"all".equalsIgnoreCase(open)) {
            paramMap.put("type", open.toLowerCase());
        }
        if (StringUtils.isNoneEmpty(currency) && !"all".equalsIgnoreCase(currency)) {
            paramMap.put("payToken", currency);
        }
        if (groupId > 0) {
            paramMap.put("groupId", groupId);
        }

        if (sort == 0) {
            paramMap.put("sort", "mm.create_time desc");
        } else if (sort == 1) {
            paramMap.put("sort", "mm.sell_price desc, mm.create_time desc");
        } else if (sort == 2) {
            paramMap.put("sort", "mm.sell_price asc, mm.create_time desc");
        }else if (sort == 3) {
            paramMap.put("sort", "ff.score desc, mm.create_time desc");
        }

        return nftMarketMapper.selectPages(paramMap);
    }

    @Override
    public List<Map<String, Object>> selectScoreByOwner(String owner) {
        return nftMarketMapper.selectScoreByOwner(owner);
    }

}
