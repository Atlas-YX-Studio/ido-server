package com.bixin.nft.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bixin.common.config.StarConfig;
import com.bixin.common.exception.BizException;
import com.bixin.common.response.R;
import com.bixin.common.utils.JacksonUtil;
import com.bixin.common.utils.LocalDateTimeUtil;
import com.bixin.common.utils.StarCoinJsonUtil;
import com.bixin.core.client.ChainClientHelper;
import com.bixin.core.client.HttpClientHelper;
import com.bixin.nft.bean.DO.NftCompositeCard;
import com.bixin.nft.bean.DO.NftCompositeElement;
import com.bixin.nft.bean.DO.NftGroupDo;
import com.bixin.nft.bean.DO.NftInfoDo;
import com.bixin.nft.bean.bo.CompositeCardBean;
import com.bixin.nft.bean.bo.CreateCompositeCardBean;
import com.bixin.nft.bean.vo.NftSelfResourceVo;
import com.bixin.nft.common.enums.CardElementType;
import com.bixin.nft.common.enums.CardState;
import com.bixin.nft.common.enums.NftInfoState;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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
    @Resource
    HttpClientHelper httpClientHelper;


    public List<NftCompositeCard> getCompositeCard(long nftId) {
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("info_id", nftId);
        return compositeCardMapper.selectByMap(columnMap);
    }

    public List<NftCompositeElement> getCompositeElements(Set<Long> nftIds) {
        QueryWrapper<NftCompositeElement> wrapper = new QueryWrapper<>();
        wrapper.lambda().in(NftCompositeElement::getInfoId, nftIds);
        return compositeElementMapper.selectList(wrapper);
    }

    @Override
    public List<Map<String, Object>> getSumByOccupationGroup() {
        QueryWrapper<NftCompositeCard> wrapper = new QueryWrapper<>();
        wrapper.select("occupation, count(id) as sum")
                .groupBy("occupation");
        return compositeCardMapper.selectMaps(wrapper);
    }

    @Transactional
    public Map<String, Object> compositeCard(CompositeCardBean bean) {
        long eleGroupId = bean.getGroupId();
        NftGroupDo nftGroupDo = nftGroupService.selectByObject(NftGroupDo.builder().elementId(eleGroupId).build());
        //name编号递增
        List<NftInfoDo> list = nftInfoService.listByObject(NftInfoDo.builder().groupId(nftGroupDo.getId()).build());
        Optional<NftInfoDo> maxNameInfo = list.stream()
                .filter(p -> NftType.COMPOSITE_CARD.getType().equals(p.getType()))
                .max(Comparator.comparing(NftInfoDo::getId));
        String[] nameArray = maxNameInfo.get().getName().split("#");
        String newName = nameArray[0].trim();
        if (nameArray.length == 2) {
            newName += " # " + (NumberUtils.toInt(nameArray[1].trim(), 0) + 1);
        } else {
            newName += " # 1";
        }

        List<Long> elementNftIds = bean.getElementList().stream()
                .map(CompositeCardBean.CustomCardElement::getId)
                .collect(Collectors.toList());
        List<NftCompositeElement> elementList = getCompositeElements(new HashSet<>(elementNftIds));

        BigDecimal sumScore = elementList.stream()
                .map(NftCompositeElement::getScore)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //插入新的 nftInfo、卡牌
        Long currentTime = LocalDateTimeUtil.getMilliByTime(LocalDateTime.now());
        NftInfoDo newNftInfo = NftInfoDo.builder()
                .nftId(0L)
                .groupId(nftGroupDo.getId())
                .type(NftType.COMPOSITE_CARD.getType())
                .name(newName)
                .owner(bean.getUserAddress())
                .imageLink("")
                .imageData("")
                .score(sumScore)
                .rank(0)
                .created(false)
                .state(NftInfoState.INIT.getDesc())
                .createTime(currentTime)
                .updateTime(currentTime)
                .build();
        // TODO: 2022/1/12  debug
        log.info("nftMetaverse new nft info: {}", newNftInfo);
        nftInfoService.insert(newNftInfo);

        NftInfoDo newInsertNftInfo = nftInfoService.selectByObject(NftInfoDo.builder()
                .groupId(nftGroupDo.getId())
                .type(NftType.COMPOSITE_CARD.getType())
                .name(newName)
                .owner(bean.getUserAddress())
                .score(sumScore)
                .createTime(currentTime)
                .updateTime(currentTime)
                .build());
        NftCompositeCard newNftCompositeCard = NftCompositeCard.of(bean.getElementList());
        newNftCompositeCard.setInfoId(newInsertNftInfo.getId());
        newNftCompositeCard.setOccupation(bean.getOccupation());
        newNftCompositeCard.setCustomName(bean.getCustomName());
        newNftCompositeCard.setState(CardState.card_init.getCode());
        newNftCompositeCard.setOriginal(false);
        newNftCompositeCard.setSex((byte) bean.getSex());
        newNftCompositeCard.setCreateTime(currentTime);
        newNftCompositeCard.setUpdateTime(currentTime);
        // TODO: 2022/1/12  debug
        log.info("nftMetaverse new nft card info: {}", newNftCompositeCard);
        compositeCardMapper.insert(newNftCompositeCard);

        //元素排序 key=order,value=element nftId
        Map<Long, Long> orderMap = new TreeMap<>();
        bean.getElementList().forEach(p -> {
            long maskOrder = CardElementType.of(p.getEleName()).getMaskOrder();
            orderMap.put(maskOrder, p.getId());
        });

//        elementNftIds
        List<NftInfoDo> infoDos = nftInfoMapper.selectByIds(elementNftIds);
        if (CollectionUtils.isEmpty(infoDos)) {
            throw new BizException("element nft info id not exits : " + elementNftIds);
        }
        Map<Long, List<NftInfoDo>> nftInfoGroup = infoDos.stream()
                .collect(Collectors.groupingBy(NftInfoDo::getId));
        Map<Long, List<NftCompositeElement>> cardGroupMap = elementList.stream()
                .collect(Collectors.groupingBy(NftCompositeElement::getInfoId));

        Map<Integer, CreateCompositeCardBean.Layer> layerMap = new TreeMap<>();
        AtomicInteger count = new AtomicInteger(0);
        orderMap.forEach((key, value) -> {
            List<NftCompositeElement> nftElements = cardGroupMap.get(value);
            List<NftInfoDo> infoList = nftInfoGroup.get(value);
            if (CollectionUtils.isEmpty(nftElements) || CollectionUtils.isEmpty(infoList)) {
                log.error("nftMetaverse get nftElements is null {}, {}, {}",
                        value, cardGroupMap, nftInfoGroup);
                return;
            }
            NftCompositeElement compositeElement = nftElements.get(0);
            NftInfoDo info = infoList.get(0);
            layerMap.put(count.getAndIncrement(), CreateCompositeCardBean.Layer.builder()
                    .nft_id(compositeElement.getInfoId())
                    .name(info.getName())
                    .property(CardElementType.of(compositeElement.getType()).getDesc())
                    .score(compositeElement.getScore())
                    .build());
        });
        CreateCompositeCardBean createCompositeCardParam = CreateCompositeCardBean.builder()
                .group_id(bean.getGroupId())
                .group_name(nftGroupDo.getName())
                .sex(bean.getSex())
                .name(nftGroupDo.getId()
                        + "_" + newInsertNftInfo.getId()
                        + "_" + LocalDateTimeUtil.getSecondsByTime(LocalDateTime.now()))
                .occupation(bean.getOccupation())
                .original(0)
                .custom_name(bean.getCustomName())
                .layers(layerMap)
                .build();
        // TODO: 2022/1/13
        log.info("nftMetaverse create nft image param: {}", createCompositeCardParam);

        String paramValue = JacksonUtil.toJson(createCompositeCardParam);
        MutableTriple<ResponseEntity<String>, String, HttpEntity<String>> triple = httpClientHelper.getCreateImgResp(paramValue);
        ResponseEntity<String> resp = triple.getLeft();
        String url = triple.getMiddle();
//        HttpEntity<String> httpEntity = triple.getRight();
        String imageUrl = "";
        boolean hasResult = false;
        if (resp.getStatusCode() == HttpStatus.OK) {
            Map<String, String> map = JacksonUtil.readValue(resp.getBody(), Map.class);
            if ("success".equalsIgnoreCase(map.get("status"))) {
                imageUrl = map.get("url");
                nftInfoService.update(NftInfoDo.builder().
                        id(newInsertNftInfo.getId())
                        .imageLink(imageUrl).build());
                hasResult = true;
            }
        }
        if (!hasResult) {
            throw new BizException("create nft img is failed, resp: "
                    + resp + "， param: " + paramValue + ", url: " + url);
        }
        return Map.of("nftInfoId", newNftInfo.getId(),
                "image", imageUrl,
                "name", newName,
                "description", nftGroupDo.getEnDescription());
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
        List<NftGroupDo> nftGroups = nftGroupService.getListByEnabled(true);
        if ("element".equalsIgnoreCase(nftType)) {
            List<NftGroupDo> nftElementGroupList = nftGroups.stream()
                    .filter(p -> NftType.COMPOSITE_ELEMENT.getType().equalsIgnoreCase(p.getType()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(nftElementGroupList)) {
                log.error("nftMetaverse get nftElementGroupList is empty");
            }
            buildElementResource(userAddress, nftElementGroupList, elementMap);
        } else if ("split".equalsIgnoreCase(nftType)) {
            List<NftGroupDo> nftCardGroupList = nftGroups.stream()
                    .filter(p -> NftType.COMPOSITE_CARD.getType().equalsIgnoreCase(p.getType()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(nftCardGroupList)) {
                log.error("nftMetaverse get nftCardGroupList is empty");
            }
            buildCardResource(userAddress, nftCardGroupList, cardList);
        }
        NftSelfResourceVo resourceVo = NftSelfResourceVo.builder()
                .cardList(cardList)
                .elementMap(elementMap)
                .build();
        return resourceVo;
    }

    private void buildElementResource(String userAddress,
                                      List<NftGroupDo> nftElementGroupList,
                                      Map<String, Set<NftSelfResourceVo.ElementVo>> elementMap) {
        for (NftGroupDo groupDo : nftElementGroupList) {
            String nftMeta = groupDo.getNftMeta();
            String nftBody = groupDo.getNftBody();
            String payToken = groupDo.getPayToken();

            List<NftInfoDo> nftInfoDos = getNftListFromChain(userAddress, groupDo);
            List<Long> eleInfoIds = nftInfoDos.stream().map(NftInfoDo::getId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(eleInfoIds)) {
                log.error("nftMetaverse get eleInfoIds element is empty {},{}", userAddress, groupDo);
                continue;
            }

            QueryWrapper<NftCompositeElement> wrapper = new QueryWrapper<>();
            wrapper.lambda().in(NftCompositeElement::getInfoId, eleInfoIds);
            List<NftCompositeElement> compositeElements = compositeElementMapper.selectList(wrapper);
            if (CollectionUtils.isEmpty(compositeElements)) {
                log.error("nftMetaverse get compositeElements is empty {}", eleInfoIds);
                continue;
            }
            Map<Long, List<NftInfoDo>> infoMap = nftInfoDos.stream()
                    .collect(Collectors.groupingBy(NftInfoDo::getId));

            Map<String, Map<String, Long>> sumMap = new HashMap<>();
            Map<String, Map<String, Map<Long, Long>>> nftIdsMap = new HashMap<>();
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
                        .score(p.getScore())
                        .sum(0)
                        .groupId(infoDo.getGroupId())
                        .name(infoDo.getName())
                        .description(groupDo.getEnDescription())
                        .eleName(CardElementType.of(type).getDesc())
                        .nftMeta(nftMeta)
                        .nftBody(nftBody)
                        .payToken(payToken)
                        .build();
                elementMap.computeIfAbsent(type, k -> new HashSet<>());
                elementMap.get(type).add(elementVo);

                sumMap.computeIfAbsent(type, k -> new HashMap<>());
                Long tmpSum = sumMap.get(type).get(property);
                sumMap.get(type).put(property, Objects.isNull(tmpSum) ? 1 : tmpSum + 1);

                nftIdsMap.computeIfAbsent(type, k -> new HashMap<>());
                nftIdsMap.get(type).computeIfAbsent(property, k -> new HashMap<>());
                nftIdsMap.get(type).get(property).put(infoDo.getNftId(), infoDo.getId());
            });
            elementMap.forEach((key, value) -> {
                Map<String, Long> propertyMap = sumMap.get(key);
                Map<String, Map<Long, Long>> nftIdMap = nftIdsMap.get(key);
                value.forEach(p -> {
                    p.setSum(propertyMap.get(p.getProperty()));
                    p.setChainNftIds(nftIdMap.get(p.getProperty()));
                });
            });
        }
    }

    public void buildCardResource(String userAddress,
                                  List<NftGroupDo> nftCardGroupList,
                                  List<NftSelfResourceVo.CardVo> cardList) {
        for (NftGroupDo groupDo : nftCardGroupList) {
            String nftMeta = groupDo.getNftMeta();
            String nftBody = groupDo.getNftBody();
            String payToken = groupDo.getPayToken();

            List<NftInfoDo> nftInfoDos = getNftListFromChain(userAddress, groupDo);
            List<Long> cardInfoIds = nftInfoDos.stream().map(NftInfoDo::getId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(cardInfoIds)) {
                log.error("nftMetaverse get cardInfoIds card is empty {}, {}", userAddress, groupDo);
                continue;
            }

            QueryWrapper<NftCompositeCard> wrapper = new QueryWrapper<>();
            wrapper.lambda().in(NftCompositeCard::getInfoId, cardInfoIds);
            List<NftCompositeCard> compositeCards = compositeCardMapper.selectList(wrapper);
            if (CollectionUtils.isEmpty(compositeCards)) {
                log.error("nftMetaverse get compositeCards is empty {}", cardInfoIds);
                continue;
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
                        .name(infoDo.getName())
                        .description(groupDo.getEnDescription())
                        .groupId(infoDo.getGroupId())
                        .chainId(infoDo.getNftId())
                        .nftId(infoDo.getId())
                        .nftMeta(nftMeta)
                        .nftBody(nftBody)
                        .payToken(payToken)
                        .build();
                cardList.add(cardVo);
            });
        }
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
