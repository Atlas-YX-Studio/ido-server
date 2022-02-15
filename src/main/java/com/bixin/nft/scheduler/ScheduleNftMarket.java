package com.bixin.nft.scheduler;

import com.bixin.common.config.StarConfig;
import com.bixin.common.utils.JacksonUtil;
import com.bixin.common.utils.LocalDateTimeUtil;
import com.bixin.core.redis.RedisCache;
import com.bixin.nft.bean.DO.NftEventDo;
import com.bixin.nft.bean.DO.NftGroupDo;
import com.bixin.nft.bean.DO.NftInfoDo;
import com.bixin.nft.bean.DO.NftMarketDo;
import com.bixin.nft.bean.dto.ChainResourceDto;
import com.bixin.nft.bean.dto.NFTBoxDto;
import com.bixin.nft.common.enums.NftBoxType;
import com.bixin.nft.common.enums.NftEventType;
import com.bixin.nft.service.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author zhangcheng
 * create   2021/9/17
 */
@Slf4j
@Component
public class ScheduleNftMarket {

    @Resource
    ContractService contractService;
    @Resource
    NftGroupService nftGroupService;
    @Resource
    NftMarketService nftMarketService;
    @Resource
    NftInfoService nftInfoService;
    @Resource
    NftEventService nftEventService;
    @Resource
    private RedisCache redisCache;
    @Resource
    private StarConfig starConfig;

    ObjectMapper mapper = new ObjectMapper();

    private static final String separator = "::";
    private static final String boxSuffix = separator + "BoxSelling";
    private static final String nftSuffix = separator + "NFTSelling";

    private static final long PROCESSING_EXPIRE_TIME = 30 * 1000L;
    private static final long LOCK_EXPIRE_TIME = 0L;
    private static final String GET_NFT_MARKET_LOCK = "get_nft_market_lock";

    //        @Scheduled(cron = "0/10 * * * * ?")
    @Scheduled(fixedDelay = 10000)
    public void getNftMarketList() {
        redisCache.tryGetDistributedLock(
                GET_NFT_MARKET_LOCK,
                UUID.randomUUID().toString(),
                PROCESSING_EXPIRE_TIME,
                LOCK_EXPIRE_TIME,
                this::pullNftMarketList);
    }

    private void pullNftMarketList() {
        String resource = contractService.listResource(starConfig.getNft().getMarket());
        ChainResourceDto chainResourceDto = JacksonUtil.readValue(resource, new TypeReference<ChainResourceDto>() {
        });
        if (Objects.isNull(chainResourceDto) || Objects.isNull(chainResourceDto.getResult())
                || Objects.isNull(chainResourceDto.getResult().getResources())) {
            log.warn("ScheduleNftMarket get chain resource is empty {}", chainResourceDto);
        }

        String nftKeyPrefix = starConfig.getNft().getMarket() + separator + starConfig.getNft().getMarketModule() + nftSuffix;
        String boxKeyPrefix = starConfig.getNft().getMarket() + separator + starConfig.getNft().getMarketModule() + boxSuffix;

        Map<NftGroupDo, List<NFTBoxDto>> nftMap = new HashMap<>();
        Map<NftGroupDo, List<NFTBoxDto>> boxMap = new HashMap<>();

        chainResourceDto.getResult().getResources().forEach((key, value) -> {
            try {
                MutableTriple<String, String, String> triple = getTokens(key);
                String left = triple.getLeft();
                String middle = triple.getMiddle();
                String right = triple.getRight();
                NftGroupDo.NftGroupDoBuilder builder = NftGroupDo.builder();

                NftBoxType type = null;
                if (key.startsWith(nftKeyPrefix)) {
                    builder.nftMeta(left).nftBody(middle).payToken(right);
                    type = NftBoxType.NFT;
                } else if (key.startsWith(boxKeyPrefix)) {
                    builder.boxToken(left).payToken(middle);
                    type = NftBoxType.BOX;
                }
                if (Objects.nonNull(type)) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> map = (Map<String, Object>) value;
                    if (!CollectionUtils.isEmpty(map) && map.containsKey("json")) {
                        NFTBoxDto nftBoxDto = JacksonUtil.readValue(JacksonUtil.toJson(map.get("json")), new TypeReference<>() {
                        });
                        if (CollectionUtils.isEmpty(nftBoxDto.getItems())) {
                            return;
                        }
                        if (NftBoxType.NFT == type) {
                            nftMap.computeIfAbsent(builder.build(), k -> new ArrayList<>());
                            nftMap.get(builder.build()).add(nftBoxDto);
                        }
                        if (NftBoxType.BOX == type) {
                            boxMap.computeIfAbsent(builder.build(), k -> new ArrayList<>());
                            boxMap.get(builder.build()).add(nftBoxDto);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("ScheduleNftMarket exception {}, {}", key, value, e);
            }
        });
        log.info("nft market: " + nftMap);
        log.info("box market: " + boxMap);

        List<NftMarketDo> list = new ArrayList<>();
        Long currentTime = LocalDateTimeUtil.getMilliByTime(LocalDateTime.now());

        buildNft(nftMap, list, currentTime);
        buildBox(boxMap, list, currentTime);

        log.info("nftBox market infos: " + list);

        validAndUpdate(list);

    }

    @Transactional
    public void validAndUpdate(List<NftMarketDo> newList) {
        if (CollectionUtils.isEmpty(newList)) {
            nftMarketService.deleteAll();
            return;
        }
        List<NftMarketDo> oldList = nftMarketService.listByObject(new NftMarketDo());
        if (CollectionUtils.isEmpty(oldList)) {
            newList.forEach(p -> nftMarketService.insert(p));
            return;
        }

        Map<Long, Map<String, List<NftMarketDo>>> oldGroup = oldList.stream().collect(
                Collectors.groupingBy(NftMarketDo::getGroupId, Collectors.groupingBy(NftMarketDo::getType)));
        Map<Long, Map<String, List<NftMarketDo>>> newGroup = newList.stream().collect(
                Collectors.groupingBy(NftMarketDo::getGroupId, Collectors.groupingBy(NftMarketDo::getType)));

        List<Long> delGroupIds = new ArrayList<>();
        Map<Long, List<String>> delTypes = new HashMap<>();
        List<Long> delIds = new ArrayList<>();
        List<NftMarketDo> updateList = new ArrayList<>();
        List<NftMarketDo> insertList = new ArrayList<>();

        oldGroup.entrySet().forEach(entry -> {
            Long oldGroupId = entry.getKey();
            Map<String, List<NftMarketDo>> oldTypeMap = entry.getValue();
            Map<String, List<NftMarketDo>> newTypeMap = newGroup.get(oldGroupId);
            if (Objects.isNull(newTypeMap)) {
                delGroupIds.add(oldGroupId);
            } else {
                oldTypeMap.entrySet().forEach(te -> {
                    String oldType = te.getKey();
                    List<NftMarketDo> oldNfts = te.getValue();
                    List<NftMarketDo> newNfts = newTypeMap.get(oldType);
                    if (CollectionUtils.isEmpty(newNfts)) {
                        delTypes.computeIfAbsent(oldGroupId, k -> new ArrayList<>());
                        delTypes.get(oldGroupId).add(oldType);
                    } else {
                        Map<Long, List<NftMarketDo>> oldIdMap = oldNfts.stream().collect(Collectors.groupingBy(NftMarketDo::getChainId));
                        Map<Long, List<NftMarketDo>> newIdMap = newNfts.stream().collect(Collectors.groupingBy(NftMarketDo::getChainId));
                        oldIdMap.entrySet().forEach(ne -> {
                            Long oldChainId = ne.getKey();
                            NftMarketDo oldNft = ne.getValue().get(0);
                            List<NftMarketDo> newNftList = newIdMap.get(oldChainId);
                            if (CollectionUtils.isEmpty(newNftList)) {
                                delIds.add(oldNft.getId());
                            } else {
                                NftMarketDo newNft = newNftList.get(0);
                                newNft.setId(oldNft.getId());
                                updateList.add(newNft);
                            }
                        });
                    }
                });
            }
        });
        newGroup.entrySet().forEach(entry -> {
            Long newGroupId = entry.getKey();
            Map<String, List<NftMarketDo>> newTypeMap = entry.getValue();
            Map<String, List<NftMarketDo>> oldTypeMap = oldGroup.get(newGroupId);
            if (Objects.isNull(oldTypeMap)) {
                newTypeMap.values().forEach(p -> insertList.addAll(p));
            } else {
                newTypeMap.entrySet().forEach(te -> {
                    String newType = te.getKey();
                    List<NftMarketDo> newNfts = te.getValue();
                    List<NftMarketDo> oldNfts = oldTypeMap.get(newType);
                    if (CollectionUtils.isEmpty(oldNfts)) {
                        insertList.addAll(newNfts);
                    } else {
                        Map<Long, List<NftMarketDo>> newIdMap = newNfts.stream().collect(Collectors.groupingBy(NftMarketDo::getChainId));
                        Map<Long, List<NftMarketDo>> oldIdMap = oldNfts.stream().collect(Collectors.groupingBy(NftMarketDo::getChainId));
                        newIdMap.entrySet().forEach(ne -> {
                            Long newChainId = ne.getKey();
                            List<NftMarketDo> oldNftList = oldIdMap.get(newChainId);
                            if (CollectionUtils.isEmpty(oldNftList)) {
                                insertList.addAll(ne.getValue());
                            }
                        });
                    }
                });
            }
        });
        if (!CollectionUtils.isEmpty(delGroupIds)) {
            nftMarketService.deleteAllByGroupIds(delGroupIds);
        }
        if (!CollectionUtils.isEmpty(delIds)) {
            nftMarketService.deleteAllByIds(delIds);
        }
        delTypes.forEach((groupId, type) -> {
            nftMarketService.deleteAllByGroupIdTypes(new HashMap<>() {{
                put(groupId, type);
            }});
        });
        updateList.forEach(p -> nftMarketService.update(p));
        insertList.forEach(p -> nftMarketService.insert(p));

    }


    private MutableTriple<String, String, String> getTokens(String tokens) {
        String regex = "\\<(.*)\\>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(tokens);
        while (matcher.find()) {
            String[] tokenArray = matcher.group(0).replaceAll("<|>", "").split(",");
            if (tokenArray.length == 3) {
                return new MutableTriple<>(tokenArray[0].trim(), tokenArray[1].trim(), tokenArray[2].trim());
            } else if (tokenArray.length == 2) {
                return new MutableTriple<>(tokenArray[0].trim(), tokenArray[1].trim(), "");
            }
        }
        return new MutableTriple<>("", "", "");
    }

    private void buildNft(Map<NftGroupDo, List<NFTBoxDto>> nftMap, List<NftMarketDo> list, long currentTime) {
        nftMap.entrySet().forEach(entry -> {
            NftGroupDo groupDo = entry.getKey();
            List<NFTBoxDto> nftList = entry.getValue();
            String payToken = groupDo.getPayToken();
            groupDo.setPayToken(null);
            NftGroupDo nftGroupDo = nftGroupService.selectByObject(groupDo);
            if (Objects.isNull(nftGroupDo)) {
                log.error("ScheduleNftMarket buildNft and nftGroupDo is null");
                return;
            }
            // disabled的不上架
            if (!nftGroupDo.getEnabled()) {
                return;
            }
            String nftType = NftBoxType.NFT.getDesc();
            if (nftGroupDo.getNftMeta().contains("KikoCatCard") && nftGroupDo.getNftBody().contains("KikoCatCard")) {
                nftType = NftBoxType.COMPOSITE_CARD.getDesc();
            } else if (nftGroupDo.getNftMeta().contains("KikoCatElement") && nftGroupDo.getNftBody().contains("KikoCatElement")) {
                nftType = NftBoxType.COMPOSITE_ELEMENT.getDesc();
            }
            String finalNftType = nftType;

            nftList.forEach(p -> p.getItems().forEach(so -> {
                NftInfoDo nftParam = NftInfoDo.builder().nftId(so.getId()).groupId(nftGroupDo.getId()).build();
                NftInfoDo nftInfo = nftInfoService.selectByObject(nftParam);
                if (Objects.isNull(nftInfo)) {
                    log.error("ScheduleNftMarket buildNft and nftInfo is null");
                    return;
                }
                long sellingTime = System.currentTimeMillis();
                List<NftEventDo> nftEventDos = nftEventService.getALlByPage(nftInfo.getId(), NftEventType.NFT_SELL_EVENT.getDesc(), 1, 0);
                if (!CollectionUtils.isEmpty(nftEventDos)) {
                    sellingTime = nftEventDos.get(0).getCreateTime();
                }
                NftMarketDo nft = NftMarketDo.builder()
                        .chainId(so.getId())
                        .nftBoxId(nftInfo.getId())
                        .groupId(nftGroupDo.getId())
                        .type(finalNftType)
                        .name(nftGroupDo.getName())
                        .nftName(nftInfo.getName())
                        .owner(so.getSeller())
                        .payToken(payToken)
                        .address(starConfig.getNft().getMarket())
                        .sellPrice(so.getSelling_price())
                        .offerPrice(BigDecimal.valueOf(so.getBid_tokens().getValue()))
                        .icon(nftInfo.getImageLink())
                        .createTime(sellingTime)
                        .updateTime(currentTime)
                        .build();
                list.add(nft);
            }));
        });
    }

    private void buildBox(Map<NftGroupDo, List<NFTBoxDto>> boxMap, List<NftMarketDo> list, long currentTime) {
        boxMap.entrySet().forEach(entry -> {
            NftGroupDo groupDo = entry.getKey();
            List<NFTBoxDto> boxList = entry.getValue();
            String payToken = groupDo.getPayToken();
            groupDo.setPayToken(null);
            NftGroupDo nftGroupDo = nftGroupService.selectByObject(groupDo);
            if (Objects.isNull(nftGroupDo)) {
                log.error("ScheduleNftMarket buildBox and nftGroupDo is null");
                return;
            }
            // disabled的不上架
            if (!nftGroupDo.getEnabled()) {
                return;
            }
            boxList.forEach(p -> p.getItems().forEach(so -> {
                long sellingTime = System.currentTimeMillis();
                List<NftEventDo> nftEventDos = nftEventService.getALlByBoxId(so.getId(), NftEventType.BOX_SELL_EVENT.getDesc(), 1, 0);
                if (!CollectionUtils.isEmpty(nftEventDos)) {
                    sellingTime = nftEventDos.get(0).getCreateTime();
                }
                NftMarketDo box = NftMarketDo.builder()
                        .chainId(so.getId())
                        .nftBoxId(0L)
                        .groupId(nftGroupDo.getId())
                        .type(NftBoxType.BOX.getDesc())
                        .name(nftGroupDo.getName())
                        .owner(so.getSeller())
//                        .nftName(null)
                        .payToken(payToken)
                        .address(starConfig.getNft().getMarket())
                        .sellPrice(so.getSelling_price())
                        .offerPrice(BigDecimal.valueOf(so.getBid_tokens().getValue()))
                        .icon(nftGroupDo.getBoxTokenLogo())
                        .createTime(sellingTime)
                        .updateTime(currentTime)
                        .build();
                list.add(box);
            }));
        });
    }


}
