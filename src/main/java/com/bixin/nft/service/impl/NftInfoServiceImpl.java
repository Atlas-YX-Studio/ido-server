package com.bixin.nft.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bixin.common.utils.BeanCopyUtil;
import com.bixin.common.utils.StarCoinJsonUtil;
import com.bixin.core.client.ChainClientHelper;
import com.bixin.nft.bean.DO.NftGroupDo;
import com.bixin.nft.bean.DO.NftInfoDo;
import com.bixin.nft.bean.vo.NftCollectionVo;
import com.bixin.nft.bean.vo.NftInfoVo;
import com.bixin.nft.common.enums.NftType;
import com.bixin.nft.common.enums.NftBoxType;
import com.bixin.nft.core.mapper.NftInfoMapper;
import com.bixin.nft.service.ContractService;
import com.bixin.nft.service.NftGroupService;
import com.bixin.nft.service.NftInfoService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableLong;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @class: NftInfoServiceImpl
 * @Description: NFT信息记录表 接口实现
 * @author: 系统
 * @created: 2021-09-15
 */
@Slf4j
@Service
public class NftInfoServiceImpl implements NftInfoService {

    @Resource
    private NftInfoMapper nftInfoMapper;
    @Resource
    private ChainClientHelper chainClientHelper;
    @Resource
    private NftGroupService nftGroupService;
    @Resource
    private NftInfoService nftInfoService;
    @Resource
    private ContractService contractService;

    /**
     * @explain: 添加NftInfoDo对象
     * @param: model 对象参数
     * @return: int
     */
    @Override
    public int insert(NftInfoDo model) {
        return nftInfoMapper.insert(model);
    }

    /**
     * @explain: 删除NftInfoDo对象
     * @param: id
     * @return: int
     */
    @Override
    public int deleteById(Long id) {
        return nftInfoMapper.deleteByPrimaryKey(id);
    }

    /**
     * @explain: 修改NftInfoDo对象
     * @param: model 对象参数
     * @return: int
     */
    @Override
    public int update(NftInfoDo model) {
        return nftInfoMapper.updateByPrimaryKeySelective(model);
    }

    /**
     * @explain: 查询NftInfoDo对象
     * @param: id
     * @return: NftInfoDo
     */
    @Override
    public NftInfoDo selectById(Long id) {
        return nftInfoMapper.selectByPrimaryKey(id);
    }

    /**
     * @explain: 查询NftInfoDo对象
     * @param: id
     * @return: NftInfoDo
     */
    @Override
    public NftInfoDo selectByIdWithImage(Long id) {
        return nftInfoMapper.selectByPrimaryKeyWithBLOBs(id);
    }

    /**
     * @explain: 查询NftInfoDo对象
     * @param: model 对象参数
     * @return: NftInfoDo 对象
     */
    @Override
    public NftInfoDo selectByObject(NftInfoDo model) {
        try {
            return nftInfoMapper.selectByPrimaryKeySelective(model);
        } catch (Exception e) {
            log.error("nft info selectByObject exception {}", model, e);
        }
        return null;
    }

    /**
     * @explain: 查询列表
     * @param: model  对象参数
     * @return: list
     */
    @Override
    public List<NftInfoDo> listByObject(NftInfoDo model) {
        return nftInfoMapper.selectByPrimaryKeySelectiveList(model);
    }

    @Override
    public List<NftInfoDo> selectByPage(boolean predicateNextPage, long pageNum, long pageSize, String order, String sort) {
        Map<String, Object> param = new HashMap<>();
        param.put("order", order);
        param.put("sort", sort);
        param.put("from", predicateNextPage ? (pageNum - 1) * (pageSize - 1) : (pageNum - 1) * pageSize);
        param.put("pageSize", pageSize);
        return nftInfoMapper.selectByPage(param);
    }

    @Override
    public List<NftInfoDo> selectAll4Rank(boolean predicateNextPage, long pageNum, long pageSize) {
        Map<String, Object> param = new HashMap<>();
        param.put("from", predicateNextPage ? (pageNum - 1) * (pageSize - 1) : (pageNum - 1) * pageSize);
        param.put("pageSize", pageSize);
        return nftInfoMapper.selectAll4Rank(param);
    }

    /**
     * 获取待质押NFT
     *
     * @param userAddress
     */
    public List<NftInfoVo> getUnStakingNftList(String userAddress) {
        List<NftGroupDo> nftGroups = nftGroupService.listByObject(NftGroupDo.builder().mining(true).build());
        List<NftInfoVo> nftInfoVos = getNftInfoVos(userAddress, nftGroups);

        return nftInfoVos.stream().sorted(Comparator.comparing(NftInfoVo::getScore).reversed()).collect(Collectors.toList());
    }

    /**
     * 获取我的NFT和盲盒
     * @param userAddress
     */
    public List<NftCollectionVo> getUnSellNftList(String userAddress) {
        List<NftCollectionVo> nftCollectionVos = Lists.newArrayList();

        List<NftGroupDo> nftGroups = nftGroupService.getListByEnabled(true);
        nftGroups.forEach(nftGroupDo -> {
            // nft
            List<NftInfoDo> nftListFromChain = getNftListFromChain(userAddress, nftGroupDo);
            List<NftCollectionVo> tempCollectionVos = BeanCopyUtil.copyListProperties(nftListFromChain,
                    () -> {
                        NftCollectionVo vo = BeanCopyUtil.copyProperties(nftGroupDo, NftCollectionVo::new);
                        vo.setCollectionType(NftBoxType.NFT.getDesc());
                        return vo;
                    });
            nftCollectionVos.addAll(tempCollectionVos);
            // 盲盒
            long boxAmount = contractService.getAddressAmount(userAddress, nftGroupDo.getBoxToken());
            for (int i = 0; i < boxAmount; i++) {
                NftCollectionVo vo = BeanCopyUtil.copyProperties(nftGroupDo, NftCollectionVo::new);
                vo.setType(NftBoxType.BOX.getDesc());
                vo.setCollectionType(NftBoxType.BOX.getDesc());
                vo.setImageLink(nftGroupDo.getBoxTokenLogo());
                vo.setScore(BigDecimal.ZERO);
                nftCollectionVos.add(vo);
            }
        });

        return nftCollectionVos.stream().sorted(Comparator.comparing(NftCollectionVo::getScore).reversed()).collect(Collectors.toList());
    }

    @Override
    public int selectCountBySelective(NftInfoDo model) {
        return nftInfoMapper.selectCountBySelective(model);
    }

    private List<NftInfoVo> getNftInfoVos(String userAddress, List<NftGroupDo> nftGroups) {
        List<NftInfoVo> nftInfoVos = Lists.newArrayList();
        nftGroups.forEach(nftGroupDo -> {
            List<NftInfoDo> nftListFromChain = getNftListFromChain(userAddress, nftGroupDo);
            List<NftInfoVo> nftInfoVoList = BeanCopyUtil.copyListProperties(nftListFromChain, nftInfoDo -> {
                NftInfoVo nftInfoVo = BeanCopyUtil.copyProperties(nftGroupDo, NftInfoVo::new);
                nftInfoVo.setNftType(NftType.of(nftInfoDo.getType()));
                return nftInfoVo;
            });
            nftInfoVos.addAll(nftInfoVoList);
        });
        return nftInfoVos;
    }

    /**
     * 从链上获取当前用户NFT
     *
     * @param userAddress
     * @param nftGroupDo
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<NftInfoDo> getNftListFromChain(String userAddress, NftGroupDo nftGroupDo) {
        MutableTriple<ResponseEntity<String>, String, HttpEntity<Map<String, Object>>> triple =
                chainClientHelper.getNftListResp(userAddress, nftGroupDo.getNftMeta(), nftGroupDo.getNftBody());
        ResponseEntity<String> resp = triple.getLeft();
        String url = triple.getMiddle();
        HttpEntity<Map<String, Object>> httpEntity = triple.getRight();

        if (resp.getStatusCode() != HttpStatus.OK) {
            log.info("getNftListFromChain 获取失败 {}, {}, {}", JSON.toJSONString(httpEntity), url, JSON.toJSONString(resp));
            return List.of();
        }
        List<JSONArray> values = StarCoinJsonUtil.parseRpcResult(resp);
        if (CollectionUtils.isEmpty(values)) {
            return List.of();
        }
        List<NftInfoDo> nftInfoDos = Lists.newArrayList();
        values.forEach(value -> {
            Object[] stcResult = value.toArray();
            if ("items".equalsIgnoreCase(String.valueOf(stcResult[0]))) {
                List<JSONObject> vector = StarCoinJsonUtil.parseVectorObj(stcResult[1]);
                vector.forEach(el -> {
                    MutableLong nftId = new MutableLong(0);
                    List<JSONArray> structValue = StarCoinJsonUtil.parseStructObj(el);
                    structValue.forEach(v -> {
                        Object[] info = v.toArray();
                        if ("id".equals(String.valueOf(info[0]))) {
                            Map<String, Object> valueMap = (Map<String, Object>) info[1];
                            nftId.setValue(Long.valueOf((String) valueMap.get("U64")));
                        }
                    });
                    if (nftId.getValue() <= 0) {
                        log.error("NFTId解析错误, groupId:{}，nftId:{}, struct:{}", nftGroupDo.getId(), nftId.getValue(), el);
                        return;
                    }
                    NftInfoDo selectNftInfoDo = new NftInfoDo();
                    selectNftInfoDo.setGroupId(nftGroupDo.getId());
                    selectNftInfoDo.setNftId(nftId.getValue());
                    NftInfoDo nftInfoDo = nftInfoService.selectByObject(selectNftInfoDo);
                    if (ObjectUtils.isEmpty(nftInfoDo)) {
                        //todo
                        log.error("why NFTInfo not exist, user:{} groupId:{}，nftId:{}", userAddress, nftGroupDo.getId(), nftId.getValue());
                        return;
                    }
                    // 以链上为准，更新当前owner
                    if (!StringUtils.equalsIgnoreCase(userAddress, nftInfoDo.getOwner())) {
                        nftInfoDo.setOwner(userAddress);
                        nftInfoService.update(nftInfoDo);
                    }
                    nftInfoDos.add(nftInfoDo);
                });
            }
        });
        return nftInfoDos;
    }


}
