package com.bixin.nft.core.service.impl;

import com.bixin.ido.server.utils.CaseUtil;
import com.bixin.nft.bean.DO.NftGroupDo;
import com.bixin.nft.bean.DO.NftMarketDo;
import com.bixin.nft.core.mapper.NftGroupMapper;
import com.bixin.nft.core.mapper.NftMarketMapper;
import com.bixin.nft.core.service.NftMarketService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    public List<NftMarketDo> selectByPage(long pageSize, long pageNum, int sort, long groupId, String currency, String open) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("sort", "sell_price");
        paramMap.put("pageSize", pageSize);
        paramMap.put("pageFrom", (pageNum - 1) * pageSize);
        if (StringUtils.isNoneEmpty(open) && !"all".equalsIgnoreCase(open)) {
            paramMap.put("type", open);
        }
        if (sort == 1) {
            paramMap.put("order", "desc");
        } else if (sort == 2) {
            paramMap.put("order", "asc");
        }

        NftGroupDo.NftGroupDoBuilder groupDoBuilder = NftGroupDo.builder();
        if (groupId > 0) {
            groupDoBuilder.id(groupId);
        }
        if (StringUtils.isNoneEmpty(currency) && !"all".equalsIgnoreCase(currency)) {
            groupDoBuilder.payToken(currency);
        }
        NftGroupDo groupParam = groupDoBuilder.build();
        if (StringUtils.isNoneEmpty(groupParam.getSeriesName()) || StringUtils.isNoneEmpty(groupParam.getPayToken())) {
            NftGroupDo nftGroupDo = nftGroupMapper.selectByPrimaryKeySelective(groupParam);
            if (Objects.nonNull(nftGroupDo)) {
                paramMap.put("groupId", nftGroupDo.getId());
            } else {
                paramMap.put("groupId", -1);
            }
        }
        return nftMarketMapper.selectByPage(paramMap);
    }

}
