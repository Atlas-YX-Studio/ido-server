package com.bixin.nft.service.impl;

import com.bixin.common.config.StarConfig;
import com.bixin.common.utils.TypeArgsUtil;
import com.bixin.ido.bean.DO.LPMiningPoolDo;
import com.bixin.nft.bean.DO.NftGroupDo;
import com.bixin.nft.bean.DO.NftMarketDo;
import com.bixin.nft.common.enums.NftBoxType;
import com.bixin.nft.core.mapper.NftGroupMapper;
import com.bixin.nft.core.mapper.NftMarketMapper;
import com.bixin.nft.service.ContractService;
import com.bixin.nft.service.NftMarketService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.starcoin.bean.ScriptFunctionObj;
import org.starcoin.bean.TypeObj;
import org.starcoin.utils.AccountAddressUtils;
import org.starcoin.utils.BcsSerializeHelper;

import javax.annotation.Resource;
import java.math.BigInteger;
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
    @Resource
    private ContractService contractService;
    @Resource
    private StarConfig starConfig;

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
    public void deleteAllByGroupIdTypes(Long groupId, List<String> types) {
        nftMarketMapper.deleteAllByGroupIdTypes(groupId, types);
    }

    @Override
    public void deleteAllByIds(List<Long> ids) {
        nftMarketMapper.deleteAllByIds(ids);
    }

    @Override
    public NftMarketDo popAuctionEndItem(Long endTime) {
        return nftMarketMapper.selectOneEndItem(endTime);
    }

    /**
     * @param predicateNextPage
     * @param pageSize
     * @param pageNum
     * @param sort
     * @param groupId
     * @param nftTypes
     * @return
     */
    @Override
    public List<Map<String, Object>> selectByPage(boolean predicateNextPage, long pageSize, long pageNum, int sort, long groupId, String sortRule, List<String> nftTypes) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("pageSize", pageSize);
        paramMap.put("pageFrom", predicateNextPage ? (pageNum - 1) * (pageSize - 1) : (pageNum - 1) * pageSize);
        if (CollectionUtils.isNotEmpty(nftTypes)) {
            paramMap.put("types", nftTypes);
        }
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
        } else if ("etime".equalsIgnoreCase(sortRule.trim())) {
            sortValue = "mm.end_time ";
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


    /**
     * 执行合约结算nft拍卖 todo function name
     *
     * @param nftMarketDo
     * @return
     */
    @Override
    public String auctionSettlement(NftMarketDo nftMarketDo) {
        log.info("auctionSettlement sellAddress:{} nft_id:{} 拍卖结算中...", nftMarketDo.getAddress(), nftMarketDo.getChainId());
        NftGroupDo nftGroupDo = nftGroupMapper.selectByPrimaryKey(nftMarketDo.getGroupId());

        ScriptFunctionObj scriptFunctionObj;
        if (nftMarketDo.getType().equalsIgnoreCase(NftBoxType.NFT.getDesc())
                || nftMarketDo.getType().equalsIgnoreCase(NftBoxType.COMPOSITE_CARD.getDesc())
                || nftMarketDo.getType().equalsIgnoreCase(NftBoxType.COMPOSITE_ELEMENT.getDesc())) {
            scriptFunctionObj = ScriptFunctionObj
                    .builder()
                    .moduleAddress(starConfig.getNft().getScripts())
                    .moduleName(starConfig.getNft().getScriptsModule())
                    .functionName("nft_delivery")
                    .args(Lists.newArrayList(
                        BcsSerializeHelper.serializeU64ToBytes(nftMarketDo.getChainId())
                    ))
                    .tyArgs(Lists.newArrayList(
                            TypeArgsUtil.parseTypeObj(nftGroupDo.getNftMeta()),
                            TypeArgsUtil.parseTypeObj(nftGroupDo.getNftBody()),
                            TypeArgsUtil.parseTypeObj(nftMarketDo.getPayToken())))
                    .build();
        } else if (nftMarketDo.getType().equalsIgnoreCase(NftBoxType.BOX.getDesc())) {
            scriptFunctionObj = ScriptFunctionObj
                    .builder()
                    .moduleAddress(starConfig.getNft().getScripts())
                    .moduleName(starConfig.getNft().getScriptsModule())
                    .functionName("box_delivery")
                    .args(Lists.newArrayList(
                        BcsSerializeHelper.serializeU128ToBytes(BigInteger.valueOf(nftMarketDo.getChainId()))
                    ))
                    .tyArgs(Lists.newArrayList(
                            TypeArgsUtil.parseTypeObj(nftGroupDo.getBoxToken()),
                            TypeArgsUtil.parseTypeObj(nftMarketDo.getPayToken())))
                    .build();
        } else {
            log.error("auctionSettlement unknown type:{}", nftMarketDo.getType());
            return null;
        }
        return contractService.callFunctionAndGetHash(starConfig.getNft().getScripts(), scriptFunctionObj);
    }

}
