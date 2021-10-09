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
import org.apache.commons.lang3.tuple.MutableTriple;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
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

    private static final String separator = "::";
    private static final String boxSuffix = separator + "BoxSelling";
    private static final String nftSuffix = separator + "NFTSelling";

    //        @Scheduled(cron = "0/10 * * * * ?")
    @Scheduled(cron = "5 0/1 * * * ?")
    public void getNftMarketList() {
        String resource = contractService.getResource(starConfig.getNft().getMarket());
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

        nftMarketService.deleteAll();

        list.forEach(so -> nftMarketService.insert(so));

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
            nftList.forEach(p -> p.getItems().forEach(so -> {
                NftInfoDo nftParam = NftInfoDo.builder().nftId(so.getId()).groupId(nftGroupDo.getId()).build();
                NftInfoDo nftInfo = nftInfoService.selectByObject(nftParam);
                if (Objects.isNull(nftInfo)) {
                    log.error("ScheduleNftMarket buildNft and nftInfo is null");
                    return;
                }
                NftMarketDo nft = NftMarketDo.builder()
                        .chainId(so.getId())
                        .nftBoxId(nftInfo.getId())
                        .groupId(nftGroupDo.getId())
                        .type(NftBoxType.NFT.getDesc())
                        .name(nftGroupDo.getName())
                        .nftName(nftInfo.getName())
                        .owner(so.getSeller())
                        .payToken(payToken)
                        .address(starConfig.getNft().getMarket())
                        .sellPrice(so.getSelling_price())
                        .offerPrice(BigDecimal.valueOf(so.getBid_tokens().getValue()))
                        .icon(nftInfo.getImageLink())
                        .createTime(currentTime)
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
            boxList.forEach(p -> p.getItems().forEach(so -> {
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
                        .createTime(currentTime)
                        .updateTime(currentTime)
                        .build();
                list.add(box);
            }));
        });
    }


}
