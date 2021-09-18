package com.bixin.nft.scheduler;

import com.bixin.ido.server.config.StarConfig;
import com.bixin.ido.server.utils.JacksonUtil;
import com.bixin.ido.server.utils.LocalDateTimeUtil;
import com.bixin.nft.bean.DO.NftGroupDo;
import com.bixin.nft.bean.DO.NftInfoDo;
import com.bixin.nft.bean.DO.NftMarketDo;
import com.bixin.nft.bean.dto.ChainResourceDto;
import com.bixin.nft.bean.dto.NFTBoxDto;
import com.bixin.nft.core.service.ContractService;
import com.bixin.nft.core.service.NftGroupService;
import com.bixin.nft.core.service.NftInfoService;
import com.bixin.nft.core.service.NftMarketService;
import com.bixin.nft.enums.NftBoxType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private StarConfig starConfig;

    ObjectMapper mapper = new ObjectMapper();

    static final String separator = "::";
    static final String boxSuffix = separator + "NFTMarket" + separator + "BoxSelling";
    static final String nftSuffix = separator + "NFTMarket" + separator + "NFTSelling";


    //    @Scheduled(cron = "20 0/5 * * * ?")
    @Scheduled(cron = "0/10 * * * * ?")
    public void getNftMarketList() {
        String resource = contractService.getResource(starConfig.getNft().getMarket());
        ChainResourceDto chainResourceDto = JacksonUtil.readValue(resource, new TypeReference<ChainResourceDto>() {
        });
        if (Objects.isNull(chainResourceDto) || Objects.isNull(chainResourceDto.getResult())
                || Objects.isNull(chainResourceDto.getResult().getResources())) {
            log.warn("ScheduleNftMarket get chain resource is empty {}", chainResourceDto);
        }

        String nftKeyPrefix = starConfig.getNft().getMarket() + nftSuffix;
        String boxKeyPrefix = starConfig.getNft().getMarket() + boxSuffix;

        Map<NftGroupDo, List<NFTBoxDto>> nftMap = new HashMap<>();
        Map<NftGroupDo, List<NFTBoxDto>> boxMap = new HashMap<>();

        chainResourceDto.getResult().getResources().forEach((key, value) -> {
            try {
                MutablePair<String, String> triple = getTokens(key);
                String left = triple.getLeft();
                String right = triple.getRight();
                NftGroupDo.NftGroupDoBuilder builder = NftGroupDo.builder();

                NftBoxType type = null;
                if (key.startsWith(nftKeyPrefix)) {
                    builder.nftMeta(left).nftBody(right);
                    type = NftBoxType.NFT;
                } else if (key.startsWith(boxKeyPrefix)) {
                    builder.boxToken(left);
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
        log.info("nft: " + nftMap);
        log.info("box: " + boxMap);

        List<NftMarketDo> list = new ArrayList<>();
        Long currentTime = LocalDateTimeUtil.getMilliByTime(LocalDateTime.now());

        buildNft(nftMap, list, currentTime);
        buildBox(boxMap, list, currentTime);

        log.info("nftBox infos: " + list);

        nftMarketService.deleteAll();

        list.forEach(so -> nftMarketService.insert(so));

    }


    private MutablePair<String, String> getTokens(String tokens) {
        String regex = "\\<(.*)\\>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(tokens);
        while (matcher.find()) {
            String[] tokenArray = matcher.group(0).replaceAll("<|>", "").split(",");
            if (tokenArray.length == 3) {
                return new MutablePair<>(tokenArray[0], tokenArray[1]);
            } else if (tokenArray.length == 2) {
                return new MutablePair<>(tokenArray[0], "");
            }
        }
        return new MutablePair<>("", "");
    }

    private void buildNft(Map<NftGroupDo, List<NFTBoxDto>> nftMap, List<NftMarketDo> list, long currentTime) {
        nftMap.entrySet().forEach(entry -> {
            NftGroupDo groupDo = entry.getKey();
            List<NFTBoxDto> nftList = entry.getValue();
            NftGroupDo nftGroupDo = nftGroupService.selectByObject(groupDo);
            if(Objects.isNull(nftGroupDo)){
                log.error("ScheduleNftMarket buildNft and nftGroupDo is null");
                return;
            }
            nftList.forEach(p -> {
                p.getItems().forEach(so -> {
                    NftInfoDo nftParam = NftInfoDo.builder().nftId(so.getId()).groupId(nftGroupDo.getId()).build();
                    NftInfoDo nftInfo = nftInfoService.selectByObject(nftParam);
                    if(Objects.isNull(nftInfo)){
                        log.error("ScheduleNftMarket buildNft and nftInfo is null");
                        return;
                    }
                    NftMarketDo nft = NftMarketDo.builder()
                            .chainId(so.getId())
                            .nftBoxId(nftInfo.getId())
                            .groupId(nftGroupDo.getId())
                            .type(NftBoxType.NFT.getDesc())
                            .name(nftInfo.getName())
                            .owner(so.getSeller())
                            .address(starConfig.getNft().getMarket())
                            .sellPrice(so.getSelling_price())
                            .offerPrice(BigDecimal.valueOf(so.getBid_tokens().getValue()))
                            .icon(nftInfo.getImageLink())
                            .createTime(currentTime)
                            .updateTime(currentTime)
                            .build();
                    list.add(nft);
                });
            });
        });
    }

    private void buildBox(Map<NftGroupDo, List<NFTBoxDto>> boxMap, List<NftMarketDo> list, long currentTime) {
        boxMap.entrySet().forEach(entry -> {
            NftGroupDo groupDo = entry.getKey();
            List<NFTBoxDto> boxList = entry.getValue();
            NftGroupDo nftGroupDo = nftGroupService.selectByObject(groupDo);
            if(Objects.isNull(nftGroupDo)){
                log.error("ScheduleNftMarket buildBox and nftGroupDo is null");
                return;
            }
            boxList.forEach(p -> {
                p.getItems().forEach(so -> {
                    NftInfoDo nftParam = NftInfoDo.builder().nftId(so.getId()).groupId(nftGroupDo.getId()).build();
                    NftInfoDo nftInfo = nftInfoService.selectByObject(nftParam);
                    if(Objects.isNull(nftInfo)){
                        log.error("ScheduleNftMarket buildBox and nftInfo is null");
                        return;
                    }
                    NftMarketDo box = NftMarketDo.builder()
                            .chainId(so.getId())
                            .nftBoxId(nftInfo.getGroupId())
                            .groupId(nftGroupDo.getId())
                            .type(NftBoxType.NFT.getDesc())
                            .name(nftInfo.getName())
                            .owner(so.getSeller())
                            .address(starConfig.getNft().getMarket())
                            .sellPrice(so.getSelling_price())
                            .offerPrice(BigDecimal.valueOf(so.getBid_tokens().getValue()))
                            .icon(nftInfo.getImageLink())
                            .createTime(currentTime)
                            .updateTime(currentTime)
                            .build();
                    list.add(box);
                });
            });
        });
    }


}
