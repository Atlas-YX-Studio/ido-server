package com.bixin.ido.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;
import com.bixin.core.client.ChainClientHelper;
import com.bixin.ido.service.IPlatformBuyBackService;
import com.bixin.common.utils.StarCoinJsonUtil;
import com.bixin.nft.bean.DO.NftGroupDo;
import com.bixin.nft.bean.DO.NftInfoDo;
import com.bixin.nft.service.NftGroupService;
import com.bixin.nft.service.NftInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class PlatformBuyBackServiceImpl implements IPlatformBuyBackService {

    @Resource
    private ChainClientHelper chainClientHelper;

    @Resource
    private NftGroupService nftGroupService;

    @Resource
    private NftInfoService nftInfoService;

    private Map<String, NftInfoDo> nftInfoMap;

    // Map<groupId, Map<currency, List<BuyBackOrder>>>
    private Map<Long, Map<String, List<BuyBackOrder>>> orderMap;

    @PostConstruct
    public void init() {

        this.refreshNftInfo();
        this.refreshOrders();

    }

    @Scheduled(cron = "* 0/10 * * * ?")
    public void refreshNftInfo() {
        try {
            List<NftInfoDo> nftInfoDos = nftInfoService.listByObject(new NftInfoDo());
            if (CollectionUtils.isEmpty(nftInfoDos)) {
                log.warn("platform buy back refresh nft info is empty");
                return;
            }
            nftInfoMap = nftInfoDos.stream().filter(x -> x.getNftId() != 0).collect(Collectors.toMap(x -> toInfoKey(x.getGroupId(), x.getNftId()), y -> y));
        } catch (Exception e) {
            log.error("refreshNftInfo exception", e);
        }
    }

    private String toInfoKey(Long groupId, Long nftId) {
        return String.format("%s_%s", groupId, nftId);
    }

    @Scheduled(cron = "0/5 * * * * ?")
    public void refreshOrders() {
        Map<Long, Map<String, List<BuyBackOrder>>> tempOrderMap = Maps.newHashMap();

        List<NftGroupDo> nftGroups = nftGroupService.getListByEnabled(true);

        nftGroups.forEach(group -> {
            List<BuyBackOrder> chainBuyBackList = getChainBuyBackList(group);
            if (tempOrderMap.containsKey(group.getId())) {
                tempOrderMap.get(group.getId()).put(group.getPayToken(), chainBuyBackList);
            } else {
                tempOrderMap.put(group.getId(), Map.of(group.getPayToken(), chainBuyBackList));
            }
        });
        if (!CollectionUtils.isEmpty(tempOrderMap) || CollectionUtils.isEmpty(this.orderMap)) {
            this.orderMap = tempOrderMap;
        }
    }

    private List<BuyBackOrder> getChainBuyBackList(NftGroupDo groupDo) {
        List<BuyBackOrder> orders = Lists.newArrayList();
        try {
            MutableTriple<ResponseEntity<String>, String, HttpEntity<Map<String, Object>>> triple = chainClientHelper.getBuyBackListResp(groupDo.getNftMeta(), groupDo.getNftBody(), groupDo.getPayToken());
            ResponseEntity<String> resp = triple.getLeft();
            String url = triple.getMiddle();
            HttpEntity<Map<String, Object>> httpEntity = triple.getRight();

            if (resp.getStatusCode() == HttpStatus.OK) {
                List<JSONArray> values = StarCoinJsonUtil.parseRpcResult(resp);
                if (CollectionUtils.isEmpty(values)) {
                    log.error("getChainBuyBackList result is empty {}, {}, {}",
                            JSON.toJSONString(resp), url, JSON.toJSONString(httpEntity));
                }
                values.forEach(rs -> {
                    Object[] stcResult = rs.toArray();
                    if ("items".equalsIgnoreCase(String.valueOf(stcResult[0]))) {
                        List<JSONObject> vector = StarCoinJsonUtil.parseVectorObj(stcResult[1]);
                        vector.forEach(el -> {
                            BuyBackOrder order = new BuyBackOrder();
                            List<JSONArray> structValue = StarCoinJsonUtil.parseStructObj(el);
                            structValue.forEach(v -> {
                                Object[] buyBackInfo = v.toArray();
                                if ("id".equalsIgnoreCase(String.valueOf(buyBackInfo[0]))) {
                                    @SuppressWarnings("unchecked")
                                    Map<String, Object> valueMap = (Map<String, Object>) buyBackInfo[1];
                                    order.nftId = Long.valueOf((String) valueMap.get("U64"));
                                } else if ("pay_tokens".equalsIgnoreCase(String.valueOf(buyBackInfo[0]))) {
                                    List<JSONArray> payTokenValue = StarCoinJsonUtil.parseStructObj(buyBackInfo[1]);
                                    payTokenValue.forEach(payToken -> {
                                        Object[] payTokenInfo = payToken.toArray();
                                        if ("value".equalsIgnoreCase(String.valueOf(payTokenInfo[0]))) {
                                            @SuppressWarnings("unchecked")
                                            Map<String, Object> payTokenMap = (Map<String, Object>) payTokenInfo[1];
                                            order.buyPrice = new BigDecimal((String) payTokenMap.get("U128")).movePointLeft(9);
                                        }
                                    });
                                }
                            });
                            NftInfoDo nftInfo = nftInfoMap.get(toInfoKey(groupDo.getId(), order.nftId));
                            if (Objects.nonNull(nftInfo)) {
                                order.id = nftInfo.getId();
                                order.groupId = nftInfo.getGroupId();
                                order.nftType = nftInfo.getType();
                                order.address = groupDo.getCreator();
                                order.metaData = groupDo.getNftMeta();
                                order.name = nftInfo.getName();
                                order.fullCurrency = groupDo.getPayToken();
                                order.icon = nftInfo.getImageLink();
                                order.score = nftInfo.getScore();
                            }
                            orders.add(order);
                        });
                    }
                });
            } else {
                log.error("getChainBuyBackList get remote result {}", JSON.toJSONString(resp));
            }
        } catch (Exception e) {
            log.error("getChainBuyBackList get remote chain exception meta={}, body={}, payToken={}", groupDo.getNftMeta(), groupDo.getNftBody(), groupDo.getPayToken(), e);
        }

        return orders;
    }

    @Override
    public List<BuyBackOrder> getOrders(Long groupId, String nftType, String currency, String sortRule, int sort, int pageNum, int pageSize) {
        Comparator<BuyBackOrder> comparator = Comparator.comparing(o -> false);
        if ("price".equalsIgnoreCase(sortRule.trim())) {
            comparator = Comparator.comparing(o -> o.buyPrice);
        } else if ("rarity".equalsIgnoreCase(sortRule.trim())) {
            comparator = Comparator.comparing(o -> o.score);
        }

        if (sort == 1 && "ctime".equalsIgnoreCase(sortRule.trim())) {
            comparator = Comparator.comparing(o -> true);
        } else if (sort == 1) {
            comparator = comparator.reversed();
        }


        Stream<BuyBackOrder> buyBackOrderStream;
        if (Objects.equals(0L, groupId) && StringUtils.equalsIgnoreCase("all", currency)) {
            buyBackOrderStream= orderMap.values().stream().flatMap(x -> x.values().stream().flatMap(Collection::stream));
        } else if (Objects.equals(0L, groupId)) {
            buyBackOrderStream = orderMap.values().stream().flatMap(x -> x.getOrDefault(currency, List.of()).stream());
        } else if (StringUtils.equalsIgnoreCase("all", currency)) {
            buyBackOrderStream = orderMap.getOrDefault(groupId, Map.of()).values().stream().flatMap(Collection::stream);
        } else {
            buyBackOrderStream = orderMap.getOrDefault(groupId, Map.of()).getOrDefault(currency, List.of()).stream();
        }

        if (!StringUtils.equalsIgnoreCase("all", nftType)) {
            buyBackOrderStream = buyBackOrderStream.filter(x -> StringUtils.equalsIgnoreCase(x.nftType, nftType));
        }

        List<BuyBackOrder> list = buyBackOrderStream.sorted(comparator).collect(Collectors.toList());

        int start = pageSize * Math.max((pageNum - 1), 0);
//        int end = Math.min(list.size(), start + pageSize);
        if (start >= list.size()) {
            return List.of();
        }
        return list.subList(start, list.size());
    }

    @Override
    public BuyBackOrder getOrder(Long id, Long groupId, String currency) {
        return this.orderMap.getOrDefault(groupId, Map.of()).getOrDefault(currency, List.of()).stream().filter(x -> Objects.equals(id, x.id)).findFirst().orElse(null);
    }

    public static class BuyBackOrder {
        public Long id;
        public Long nftId;
        public Long groupId;
        public String nftType;
        public String address;
        public String metaData;
        public String name;
        public BigDecimal buyPrice;
        public String fullCurrency;
        public String icon;
        public BigDecimal score;

        public String getCurrency() {
            if (StringUtils.isBlank(this.fullCurrency)) {
                return "";
            }
            return this.fullCurrency.split("::")[2];
        }
    }

}
