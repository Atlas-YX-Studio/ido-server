package com.bixin.nft.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bixin.common.config.StarConfig;
import com.bixin.common.response.R;
import com.bixin.common.utils.StarCoinJsonUtil;
import com.bixin.ido.core.client.ChainClientHelper;
import com.bixin.nft.bean.DO.NftCompositeCard;
import com.bixin.nft.bean.DO.NftCompositeElement;
import com.bixin.nft.bean.DO.NftGroupDo;
import com.bixin.nft.bean.DO.NftInfoDo;
import com.bixin.nft.bean.bo.CompositeCardBean;
import com.bixin.nft.bean.vo.NftSelfResourceVo;
import com.bixin.nft.common.enums.CardElementType;
import com.bixin.nft.common.enums.NftType;
import com.bixin.nft.core.mapper.NftCompositeCardMapper;
import com.bixin.nft.core.mapper.NftCompositeElementMapper;
import com.bixin.nft.core.mapper.NftGroupMapper;
import com.bixin.nft.core.mapper.NftInfoMapper;
import com.bixin.nft.service.NftGroupService;
import com.bixin.nft.service.NftInfoService;
import com.bixin.nft.service.NftMetareverseService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangcheng
 * create  2021/12/23
 */
@Slf4j
@Service
public class NftMetaverseServiceImpl implements NftMetareverseService {

    @Resource
    ChainClientHelper chainClientHelper;
    @Resource
    NftCompositeCardMapper compositeCardMapper;
    @Resource
    NftCompositeElementMapper compositeElementMapper;
    @Resource
    NftInfoMapper nftInfoMapper;
    @Resource
    NftGroupMapper nftGroupMapper;
    @Resource
    NftGroupService nftGroupService;
    @Resource
    NftInfoService nftInfoService;
    @Resource
    StarConfig idoStarConfig;

    @Override
    public List<Map<String, Object>> getSumByOccupationGroup() {
        QueryWrapper<NftCompositeCard> wrapper = new QueryWrapper<>();
        wrapper.select("occupation, count(id) as sum")
                .groupBy("occupation");
        return compositeCardMapper.selectMaps(wrapper);
    }


    public String compositeCard(CompositeCardBean bean) {
        long groupId = 0;
        if (bean.getGroupId() <= 0) {
            String likeValue = idoStarConfig.getNft().getCatadd() + "::KikoCatCard";
            List<NftGroupDo> groupDos = nftGroupService.getListByEnabled(true);
            for (NftGroupDo p : groupDos) {
                if (p.getNftMeta().startsWith(likeValue) && p.getNftBody().startsWith(likeValue)) {
                    groupId = p.getId();
                    break;
                }
            }
        } else {
            groupId = bean.getGroupId();
        }

        List<NftInfoDo> list = nftInfoService.listByObject(NftInfoDo.builder().groupId(groupId).build());
        NftInfoDo nftInfoDo = list.get(list.size() - 1);
        String name = nftInfoDo.getName();
        String[] nameArray = name.split("#");
        String newName = nameArray[0] + " # " + (NumberUtils.toInt(nameArray[1].trim(), -1));

        List<Long> elementIds = bean.getElementList().stream()
                .map(CompositeCardBean.CustomCardElement::getId)
                .collect(Collectors.toList());
        List<NftCompositeElement> elementList = compositeElementMapper.selectBatchIds(elementIds);

        BigDecimal sumScore = elementList.stream()
                .map(NftCompositeElement::getScore)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        NftInfoDo newNftInfo = NftInfoDo.builder()
                .nftId(0L)
                .groupId(groupId)
                .type(NftType.COMPOSITE_CARD.getType())
                .name(newName)
                .owner(bean.getUserAddress())
                .imageLink("")
                .imageData("")
                .score(sumScore)
                // TODO: 2022/1/11 排名
                .rank(100)
                .build();
        //插入新的 nftInfo
//        nftInfoService.insert(newNftInfo);

        //元素排序 key=order,value=elementId
        Map<Long, Long> orderMap = new TreeMap<>();
        bean.getElementList().forEach(p -> {
            long maskOrder = CardElementType.of(p.getEleName()).getMaskOrder();
            orderMap.put(maskOrder, p.getId());
        });


        //校验 需要组合的卡牌是否已经存在，如果存在则直接反悔原有的图片链接，如果不存在，继续下一步

        //http 调用远程组合卡牌服务，获取新组合的卡牌 url

        //查询原始表 nft_info 的 nft 原始数据，然后把新组合的卡牌数据插入到组合卡牌表

        //返回 卡牌的 url

        return null;
    }

    @Override
    public R analysisCard(String userAddress, long cardId) {
        NftInfoDo nftInfoDo = nftInfoMapper.selectByPrimaryKey(cardId);
        if (Objects.isNull(nftInfoDo)) {
            return R.failed("cardId is invalid");
        }
        if (!userAddress.equalsIgnoreCase(nftInfoDo.getOwner())) {
            return R.failed("userAddress is invalid");
        }


        return R.success();
    }

    /**
     * @param userAddress
     * @param nftType     element 素材/元素; split  可拆解nft
     * @return
     */
    @Override
    public NftSelfResourceVo selfResource(String userAddress, String nftType) {
        Map<String, Set<NftSelfResourceVo.ElementVo>> elementMap = new HashMap<>();
        List<NftSelfResourceVo.CardVo> cardList = new ArrayList<>();

        String nftMeta_element = idoStarConfig.getNft().getCatadd() + "::KikoCatElement05::KikoCatMeta";
        String nftBody_element = idoStarConfig.getNft().getCatadd() + "::KikoCatElement05::KikoCatBody";
        String nftMeta_card = idoStarConfig.getNft().getCatadd() + "::KikoCatCard05::KikoCatMeta";
        String nftBody_card = idoStarConfig.getNft().getCatadd() + "::KikoCatCard05::KikoCatBody";

        if ("element".equalsIgnoreCase(nftType)) {
            NftGroupDo groupParam = NftGroupDo.builder()
                    .nftMeta(nftMeta_element)
                    .nftBody(nftBody_element)
                    .build();
            NftGroupDo groupDo = nftGroupService.selectByObject(groupParam);
            if (Objects.isNull(groupDo)) {
                log.error("nftMetaverse get groupDo element is empty {}", groupParam);
                return null;
            }
            List<NftInfoDo> nftInfoDos = getNftListFromChain(userAddress, groupDo);
            List<Long> eleInfoIds = nftInfoDos.stream().map(NftInfoDo::getId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(eleInfoIds)) {
                log.error("nftMetaverse get eleInfoIds element is empty {},{}", userAddress, groupDo);
                return null;
            }
            // TODO: 2022/1/11
            log.info("nftMetaverse eleInfoIds {}", eleInfoIds);

            QueryWrapper<NftCompositeElement> wrapper = new QueryWrapper<>();
            wrapper.lambda().in(NftCompositeElement::getInfoId, eleInfoIds);
            List<NftCompositeElement> compositeElements = compositeElementMapper.selectList(wrapper);
            if (CollectionUtils.isEmpty(compositeElements)) {
                log.error("nftMetaverse get compositeElements is empty {}", eleInfoIds);
                return null;
            }
            Map<Long, List<NftInfoDo>> infoMap = nftInfoDos.stream()
                    .collect(Collectors.groupingBy(NftInfoDo::getId));

            Map<String, Map<String, Long>> sumMap = new HashMap<>();
            compositeElements.forEach(p -> {
                List<NftInfoDo> infoDos = infoMap.get(p.getInfoId());
                if (CollectionUtils.isEmpty(infoDos)) {
                    log.error("nftMetaverse get infoMap element is empty {}", p);
                    return;
                }
                NftInfoDo infoDo = infoDos.get(0);
                String type = p.getType().toLowerCase();
                String property = p.getProperty();

                NftSelfResourceVo.ElementVo elementVo = NftSelfResourceVo.ElementVo.builder()
                        .type(type)
                        .property(property)
                        .image(infoDo.getImageLink())
//                        .score(BigDecimalUtil.div(p.getScore(), BigDecimal.valueOf(1000000000), 2))
                        .score(p.getScore())
                        .sum(0)
                        .groupId(infoDo.getGroupId())
                        .name(infoDo.getName())
                        .build();
                elementMap.computeIfAbsent(type, k -> new HashSet<>());
                elementMap.get(type).add(elementVo);

                sumMap.computeIfAbsent(type, k -> new HashMap<>());
                Long tmpSum = sumMap.get(type).get(property);
                sumMap.get(type).put(property, Objects.isNull(tmpSum) ? 0 : tmpSum + 1);
            });

            elementMap.forEach((key, value) -> {
                Map<String, Long> propertyMap = sumMap.get(key);
                value.forEach(p -> p.setSum(propertyMap.get(p.getProperty())));
            });
        } else if ("split".equalsIgnoreCase(nftType)) {
            NftGroupDo groupParam = NftGroupDo.builder()
                    .nftMeta(nftMeta_card)
                    .nftBody(nftBody_card)
                    .build();
            NftGroupDo groupDo = nftGroupService.selectByObject(groupParam);
            if (Objects.isNull(groupDo)) {
                log.error("nftMetaverse get groupDo card is empty {}", groupParam);
                return null;
            }
            List<NftInfoDo> nftInfoDos = getNftListFromChain(userAddress, groupDo);
            List<Long> cardInfoIds = nftInfoDos.stream().map(NftInfoDo::getId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(cardInfoIds)) {
                log.error("nftMetaverse get cardInfoIds card is empty {}, {}", userAddress, groupDo);
                return null;
            }
            // TODO: 2022/1/11
            log.info("nftMetaverse cardInfoIds {}", cardInfoIds);

            QueryWrapper<NftCompositeCard> wrapper = new QueryWrapper<>();
            wrapper.lambda().in(NftCompositeCard::getInfoId, cardInfoIds);
            List<NftCompositeCard> compositeCards = compositeCardMapper.selectList(wrapper);
            if (CollectionUtils.isEmpty(compositeCards)) {
                log.error("nftMetaverse get compositeCards is empty {}", cardInfoIds);
                return null;
            }
            Map<Long, List<NftInfoDo>> infoMap = nftInfoDos.stream()
                    .collect(Collectors.groupingBy(NftInfoDo::getId));
            compositeCards.forEach(p -> {
                List<NftInfoDo> infoDos = infoMap.get(p.getInfoId());
                if (CollectionUtils.isEmpty(infoDos)) {
                    log.error("nftMetaverse get infoMap card is empty {}", p);
                    return;
                }
                NftInfoDo infoDo = infoDos.get(0);
                NftSelfResourceVo.CardVo cardVo = NftSelfResourceVo.CardVo.builder()
                        .original(p.getOriginal())
                        .customName(p.getCustomName())
                        .sex(p.getSex())
                        .image(infoDo.getImageLink())
                        .groupId(infoDo.getGroupId())
                        .name(infoDo.getName())
                        .build();
                cardList.add(cardVo);
            });
        }
        NftSelfResourceVo resourceVo = NftSelfResourceVo.builder()
                .cardList(cardList)
                .elementMap(elementMap)
                .build();
        return resourceVo;
    }


    private List<NftInfoDo> getNftListFromChain(String userAddress, NftGroupDo nftGroupDo) {
        MutableTriple<ResponseEntity<String>, String, HttpEntity<Map<String, Object>>> triple =
                chainClientHelper.getNftListResp(userAddress, nftGroupDo.getNftMeta(), nftGroupDo.getNftBody());
        ResponseEntity<String> resp = triple.getLeft();
        String url = triple.getMiddle();
        HttpEntity<Map<String, Object>> httpEntity = triple.getRight();

        if (resp.getStatusCode() != HttpStatus.OK) {
            log.info("nftMetaverse getNftListFromChain 获取失败 {}, {}, {}", JSON.toJSONString(httpEntity), url, JSON.toJSONString(resp));
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
                        log.error("nftMetaverse NFTId解析错误, groupId:{}，nftId:{}, struct:{}", nftGroupDo.getId(), nftId.getValue(), el);
                        return;
                    }
                    NftInfoDo selectNftInfoDo = new NftInfoDo();
                    selectNftInfoDo.setGroupId(nftGroupDo.getId());
                    selectNftInfoDo.setNftId(nftId.getValue());
                    NftInfoDo nftInfoDo = nftInfoService.selectByObject(selectNftInfoDo);
                    if (ObjectUtils.isEmpty(nftInfoDo)) {
                        log.error("nftMetaverse NFTInfo不存在, groupId:{}，nftId:{}", nftGroupDo.getId(), nftId.getValue());
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
