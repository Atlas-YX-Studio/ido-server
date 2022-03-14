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
import com.bixin.nft.bean.bo.CreateCompositeCardBeanV2;
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


    @Override
    public List<NftCompositeCard> getCompositeCard(long nftId) {
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("info_id", nftId);
//        columnMap.put("state", CardState.card_combining_success.getCode());
        return compositeCardMapper.selectByMap(columnMap);
    }

    @Override
    public List<NftCompositeCard> getCompositeCards(List<Long> nftIds) {
        QueryWrapper<NftCompositeCard> wrapper = new QueryWrapper<>();
        wrapper.lambda().in(NftCompositeCard::getInfoId, nftIds);
        return compositeCardMapper.selectList(wrapper);
    }

    @Override
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
    public Map<String, Object> compositeCardV2(CompositeCardBean bean) {
        // TODO: 2022/1/12  debug
        log.info("nftMetaverse create nft param: {}", bean);
        List<Long> nftInfoIds = bean.getElementList().stream().map(CompositeCardBean.CustomCardElement::getId).collect(Collectors.toList());
        List<NftInfoDo> infoDos = nftInfoMapper.selectByIds(nftInfoIds);
        if (CollectionUtils.isEmpty(infoDos)) {
            throw new BizException("element nft info id not exits : " + nftInfoIds);
        }

        Optional<NftInfoDo> maxInfoDo = infoDos.stream().filter(p -> NftType.COMPOSITE_ELEMENT.getType().equals(p.getType())).max(Comparator.comparing(NftInfoDo::getGroupId));
        Long maxGroupId = maxInfoDo.get().getGroupId();
        NftGroupDo nftGroupDo = nftGroupService.selectByObject(NftGroupDo.builder().elementId(maxGroupId).build());

        //name编号递增
        List<NftInfoDo> list = nftInfoService.listByObject(NftInfoDo.builder().groupId(nftGroupDo.getId()).build());
        Optional<NftInfoDo> maxNameInfo = list.stream().filter(p -> NftType.COMPOSITE_CARD.getType().equals(p.getType())).max(Comparator.comparing(NftInfoDo::getId));
        String[] nameArray = maxNameInfo.get().getName().split("#");
        String newName = nameArray[0].trim();
        if (nameArray.length == 2) {
            newName += " # " + (NumberUtils.toInt(nameArray[1].trim(), 0) + 1);
        } else {
            newName += " # 1";
        }

        List<NftCompositeElement> compositeElements = getCompositeElements(new HashSet<>(nftInfoIds));
        if (CollectionUtils.isEmpty(compositeElements)) {
            throw new BizException("element nft info id not exits : " + nftInfoIds);
        }
        BigDecimal sumScore = compositeElements.stream().map(NftCompositeElement::getScore).reduce(BigDecimal.ZERO, BigDecimal::add);

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
        nftInfoService.insert(newNftInfo);
        log.info("nftMetaverse new nft info: {}", newNftInfo);

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
        compositeCardMapper.insert(newNftCompositeCard);
        log.info("nftMetaverse new nft card info: {}", newNftCompositeCard);

        //元素排序 key=order,value=element nftId
        Map<Long, Long> orderMap = new TreeMap<>();
        bean.getElementList().forEach(p -> {
            long maskOrder = CardElementType.of(p.getEleName()).getMaskOrder();
            orderMap.put(maskOrder, p.getId());
        });

        Map<Long, List<NftInfoDo>> nftInfoGroup = infoDos.stream().collect(Collectors.groupingBy(NftInfoDo::getId));
        Map<Long, List<NftCompositeElement>> cardGroupMap = compositeElements.stream().collect(Collectors.groupingBy(NftCompositeElement::getInfoId));

        AtomicInteger count = new AtomicInteger(0);
        Map<Integer, CreateCompositeCardBeanV2.Layer> layerMap = new TreeMap<>();
        orderMap.forEach((key, value) -> {
            List<NftCompositeElement> _compositeElements = cardGroupMap.get(value);
            List<NftInfoDo> infoList = nftInfoGroup.get(value);
            if (CollectionUtils.isEmpty(_compositeElements) || CollectionUtils.isEmpty(infoList)) {
                log.error("nftMetaverse get _compositeElements is null {}, {}, {}", value, cardGroupMap, nftInfoGroup);
                return;
            }
            NftCompositeElement compositeElement = _compositeElements.get(0);
            NftInfoDo info = infoList.get(0);

            layerMap.put(count.getAndIncrement(), CreateCompositeCardBeanV2.Layer.builder()
                    //todo
                    //.element_group_id(info.getGroupId())
                    .element_group_id(10018L)
                    .name(StringUtils.substringBefore(info.getName(), "##").trim())
                    .property(CardElementType.of(compositeElement.getType()).getDesc())
                    .score(compositeElement.getScore())
                    .build());
        });
        CreateCompositeCardBeanV2 createCompositeCardParam = CreateCompositeCardBeanV2.builder()
                //todo
                //.group_id(nftGroupDo.getId())
                .group_id(10012L)
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
        MutableTriple<ResponseEntity<String>, String, HttpEntity<String>> triple = httpClientHelper.getCreateImgRespV2(paramValue);
        ResponseEntity<String> resp = triple.getLeft();
        String url = triple.getMiddle();
        String imageUrl = "";
        boolean hasResult = false;
        if (resp.getStatusCode() == HttpStatus.OK) {
            Map<String, String> map = JacksonUtil.readValue(resp.getBody(), Map.class);
            if ("success".equalsIgnoreCase(map.get("status"))) {
                nftInfoService.update(NftInfoDo.builder().id(newInsertNftInfo.getId()).imageLink(imageUrl).build());
                imageUrl = map.get("url");
                hasResult = true;
            }
        }
        if (!hasResult) {
            throw new BizException("create nft img is failed, resp: " + resp + "， param: " + paramValue + ", url: " + url);
        }
        Map<String, Object> result = Map.of("nftInfoId", newNftInfo.getId(), "image", imageUrl, "name", newName, "description", nftGroupDo.getEnDescription());
        log.info("nftMetaverse create nft image success {}", JacksonUtil.toJson(result));
        return result;
    }

    @Transactional
    public Map<String, Object> compositeCard(CompositeCardBean bean) {
        // TODO: 2022/1/12  debug
        log.info("nftMetaverse create nft param: {}", bean);
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
                    .name(StringUtils.substringBefore(info.getName(), "##").trim())
                    .property(CardElementType.of(compositeElement.getType()).getDesc())
                    .score(compositeElement.getScore())
                    .build());
        });
        CreateCompositeCardBean createCompositeCardParam = CreateCompositeCardBean.builder()
                .group_id(nftGroupDo.getId())
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
        Map<String, Object> result = Map.of("nftInfoId", newNftInfo.getId(),
                "image", imageUrl,
                "name", newName,
                "description", nftGroupDo.getEnDescription());
        log.info("nftMetaverse create nft image success {}", JacksonUtil.toJson(result));
        return result;
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
        Map<String, List<NftSelfResourceVo.ElementVo>> elementMap = new HashMap<>();
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
                                      Map<String, List<NftSelfResourceVo.ElementVo>> elementMap) {
        for (NftGroupDo groupDo : nftElementGroupList) {
            String nftMeta = groupDo.getNftMeta();
            String nftBody = groupDo.getNftBody();
            String payToken = groupDo.getPayToken();

            List<NftInfoDo> nftInfoDos = getNftListFromChain("element", userAddress, groupDo);
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
            Map<String, Map<String, NftSelfResourceVo.ElementVo>> repeatPropertyMap = new HashMap<>();
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

                sumMap.computeIfAbsent(type, k -> new HashMap<>());
                Long tmpSum = sumMap.get(type).get(property);
                long lastSum = Objects.isNull(tmpSum) ? 1 : tmpSum + 1;
                sumMap.get(type).put(property, lastSum);

                nftIdsMap.computeIfAbsent(type, k -> new HashMap<>());
                nftIdsMap.get(type).computeIfAbsent(property, k -> new HashMap<>());
                nftIdsMap.get(type).get(property).put(infoDo.getNftId(), infoDo.getId());

                repeatPropertyMap.computeIfAbsent(type, k -> new HashMap<>());
                NftSelfResourceVo.ElementVo tmpVo = repeatPropertyMap.get(type).put(property, elementVo);

                elementMap.computeIfAbsent(type, k -> new ArrayList<>());
                List<NftSelfResourceVo.ElementVo> tmpSet = elementMap.get(type);

                if (Objects.nonNull(tmpVo)) {
                    log.warn("nftMetaverse element property repeat {},{},{},{}",
                            userAddress, groupDo.getId(), groupDo.getElementId(), tmpVo);
                    tmpSet.remove(tmpVo);
                }
                elementVo.setSum(lastSum);
                elementVo.setChainNftIds(nftIdsMap.get(type).get(property));
                tmpSet.add(elementVo);
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

            List<NftInfoDo> nftInfoDos = getNftListFromChain("card", userAddress, groupDo);
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
                List<Long> eleNftIds = NftCompositeCard.getElementIds(p);
                List<NftCompositeElement> elements = getCompositeElements(new HashSet<>(eleNftIds));
                if (CollectionUtils.isEmpty(elements)) {
                    log.error("nftMetaverse get card elements  is empty {}", new HashSet<>(eleNftIds));
                    return;
                }
                BigDecimal sumScore = elements.stream()
                        .map(NftCompositeElement::getScore)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                NftInfoDo infoDo = infoDos.get(0);
                NftSelfResourceVo.CardVo cardVo = NftSelfResourceVo.CardVo.builder()
                        .original(p.getOriginal())
                        .customName(p.getCustomName())
                        .occupation(p.getOccupation())
                        .sex(p.getSex())
                        .image(infoDo.getImageLink())
                        .name(infoDo.getName())
                        .description(groupDo.getEnDescription())
                        .groupId(infoDo.getGroupId())
                        .chainId(infoDo.getNftId())
                        .nftId(infoDo.getId())
                        .score(sumScore)
                        .nftMeta(nftMeta)
                        .nftBody(nftBody)
                        .payToken(payToken)
                        .build();
                cardList.add(cardVo);
            });
        }
    }

    private List<NftInfoDo> getNftListFromChain(String nftType, String userAddress, NftGroupDo nftGroupDo) {
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
                    Map<String, Long> eleIdMap = new HashMap<>();
                    List<Long> eleChainIds = new ArrayList<>();
                    structValue.forEach(v -> {
                        Object[] info = v.toArray();
                        if ("id".equals(String.valueOf(info[0]))) {
                            Map<String, Object> valueMap = (Map<String, Object>) info[1];
                            nftId.setValue(Long.valueOf((String) valueMap.get("U64")));
                        }
                        if (nftType.contains("card") && "type_meta".equals(String.valueOf(info[0]))) {
                            List<JSONArray> cardElementIds = StarCoinJsonUtil.parseStructObj(info[1]);
                            for (JSONArray idArray : cardElementIds) {
                                if (String.valueOf(idArray.get(0)).endsWith("_id")) {
                                    Map<String, Object> idValueMap = (Map<String, Object>) idArray.get(1);
                                    long u64 = NumberUtils.toLong(String.valueOf(idValueMap.get("U64")), 0);
                                    if (u64 > 0) {
                                        eleChainIds.add(u64);
                                    }
                                    eleIdMap.put(String.valueOf(idArray.get(0)), u64);
                                }
                            }
                        }
                    });
                    log.info("nftMetaverse get chain nft info:{},{},{},{},{},{}",
                            userAddress, nftGroupDo.getId(), nftGroupDo.getElementId(),
                            nftId, eleChainIds, JacksonUtil.toJson(eleIdMap));

                    if (nftType.contains("card")) {
                        Map<String, Object> groupParam = new HashMap<>();
                        groupParam.put("groupId", nftGroupDo.getElementId());
                        groupParam.put("type", NftType.COMPOSITE_ELEMENT.getType());
                        groupParam.put("list", eleChainIds);
                        List<NftInfoDo> eleInfos = nftInfoMapper.selectByNftIds(groupParam);
                        if (CollectionUtils.isEmpty(eleInfos)) {
                            log.error("nftMetaverse get eleInfos is empty");
                            return;
                        }
                        log.info("nftMetaverse get eleInfos {}", JacksonUtil.toJson(eleInfos));
                        Map<Long, Long> eleIdmap = eleInfos.stream()
                                .collect(Collectors.toMap(NftInfoDo::getNftId, NftInfoDo::getId));
                        Map<String, Object> paramMap = new HashMap<>();
                        eleIdMap.forEach((column, var) -> {
                            Long eleNftId = eleIdmap.get(var);
                            paramMap.put(column, Objects.nonNull(eleNftId) ? eleNftId : 0);
                        });
//                        paramMap.put("state", CardState.card_combining_success.getCode());
                        log.info("nftMetaverse get newCards info {}", JacksonUtil.toJson(paramMap));
                        List<NftCompositeCard> newCards = compositeCardMapper.selectByMap(paramMap);
                        if (CollectionUtils.isEmpty(newCards)) {
                            log.error("nftMetaverse get newCards is empty");
                            return;
                        }
                        long nftInfoId = newCards.get(newCards.size() - 1).getInfoId();
                        NftInfoDo nftInfoDo = nftInfoService.selectById(nftInfoId);
                        if (ObjectUtils.isEmpty(nftInfoDo)) {
                            log.error("nftMetaverse NFTInfo不存在, groupId:{}，nftId:{}", nftGroupDo.getId(), nftInfoId);
                            return;
                        }
                        // 以链上为准，更新当前owner
                        boolean hasUpdate = false;
                        if (!StringUtils.equalsIgnoreCase(userAddress, nftInfoDo.getOwner())) {
                            nftInfoDo.setOwner(userAddress);
                            hasUpdate = true;
                        }
                        if (!nftInfoDo.getCreated()
                                || Objects.isNull(nftInfoDo.getNftId()) || nftInfoDo.getNftId() <= 0
                                || !NftInfoState.SUCCESS.getDesc().equals(nftInfoDo.getState())) {
                            nftInfoDo.setOwner(userAddress);
                            nftInfoDo.setNftId(nftId.getValue());
                            nftInfoDo.setCreated(true);
                            nftInfoDo.setState(NftInfoState.SUCCESS.getDesc());
                            hasUpdate = true;
                        }
                        if (hasUpdate) {
                            nftInfoService.update(nftInfoDo);
                        }
                        nftInfoDos.add(nftInfoDo);
                    } else {
                        Map<String, Object> groupParam = new HashMap<>();
                        groupParam.put("groupId", nftGroupDo.getId());
                        groupParam.put("list", Arrays.asList(nftId.getValue()));
                        List<NftInfoDo> eleInfos = nftInfoMapper.selectByNftIds(groupParam);
                        if (CollectionUtils.isEmpty(eleInfos)) {
                            log.error("nftMetaverse get eleInfos is empty");
                            return;
                        }
                        NftInfoDo infoDo = eleInfos.get(0);
                        // 以链上为准，更新当前owner
                        if (!StringUtils.equalsIgnoreCase(userAddress, infoDo.getOwner())) {
                            infoDo.setOwner(userAddress);
                            nftInfoService.update(infoDo);
                        }
                        nftInfoDos.add(infoDo);
                    }
                });
            }
        });
        return nftInfoDos;
    }


}
