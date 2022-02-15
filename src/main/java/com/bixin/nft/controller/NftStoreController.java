package com.bixin.nft.controller;

import com.bixin.common.response.P;
import com.bixin.common.response.R;
import com.bixin.nft.bean.DO.NftCompositeCard;
import com.bixin.nft.bean.DO.NftGroupDo;
import com.bixin.nft.bean.DO.NftMarketDo;
import com.bixin.nft.bean.DO.TradingRecordDo;
import com.bixin.nft.bean.vo.NftSelfSellingVo;
import com.bixin.nft.common.enums.NftBoxType;
import com.bixin.nft.service.NftGroupService;
import com.bixin.nft.service.NftMarketService;
import com.bixin.nft.service.NftMetareverseService;
import com.bixin.nft.service.TradingRecordService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.bixin.common.constants.PathConstant.NFT_REQUEST_PATH_PREFIX;

/**
 * @author zhangcheng
 * create   2021/9/18
 */
@RestController
@RequestMapping(NFT_REQUEST_PATH_PREFIX + "/store")
public class NftStoreController {

    @Resource
    NftMarketService nftMarketService;
    @Resource
    NftGroupService nftGroupService;
    @Resource
    TradingRecordService tradingRecordService;
    @Resource
    NftMetareverseService metareverseService;

    @GetMapping("/selling")
    public R getALlByPage(@RequestParam(value = "userAddress", defaultValue = "") String userAddress) {

        if (StringUtils.isEmpty(userAddress)) {
            return R.failed("parameter is invalid");
        }

        List<Map<String, Object>> maps = nftMarketService.selectScoreByOwner(userAddress);
        if (CollectionUtils.isEmpty(maps)) {
            return R.success(null);
        }

        List<NftSelfSellingVo> list = new ArrayList<>();
        maps.stream().forEach(p -> list.add(NftSelfSellingVo.of(p)));

        List<Long> nftIds = list.stream()
                .filter(p -> NftBoxType.COMPOSITE_CARD.getDesc().equalsIgnoreCase(p.getType()))
                .map(NftSelfSellingVo::getNftBoxId).collect(Collectors.toList());
        Map<Long, List<NftCompositeCard>> cardIdMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(nftIds)) {
            List<NftCompositeCard> compositeCards = metareverseService.getCompositeCards(nftIds);
            cardIdMap = compositeCards.stream()
                    .collect(Collectors.groupingBy(NftCompositeCard::getInfoId));
        }

        Set<Long> groupIds = list.stream().map(NftMarketDo::getGroupId).collect(Collectors.toSet());
        Map<Long, NftGroupDo> map = new HashMap<>();
        groupIds.forEach(id -> {
            NftGroupDo nftGroupDo = nftGroupService.selectById(id);
            map.put(id, nftGroupDo);
        });

        for (NftSelfSellingVo p : list) {
            NftGroupDo nftGroupDo = map.get(p.getGroupId());
            if (Objects.nonNull(nftGroupDo)) {
                String boxToken = nftGroupDo.getBoxToken();
                String nftMeta = nftGroupDo.getNftMeta();
                String nftBody = nftGroupDo.getNftBody();
                p.setBoxToken(boxToken);
                p.setNftMeta(nftMeta);
                p.setNftBody(nftBody);
            }
            List<NftCompositeCard> nftCompositeCards = cardIdMap.get(p.getNftBoxId());
            if (!CollectionUtils.isEmpty(nftCompositeCards)) {
                NftCompositeCard card = nftCompositeCards.get(0);
                p.setOccupation(card.getOccupation());
                p.setCustomName(card.getCustomName());
                p.setSex(card.getSex());
            }


        }

        return R.success(list);
    }

    @GetMapping("/records")
    public P getRecords(@RequestParam(value = "address") String address,
                        @RequestParam(value = "direction", defaultValue = "all") String direction,
                        @RequestParam(value = "pageSize", defaultValue = "20") long pageSize,
                        @RequestParam(value = "pageNum", defaultValue = "1") long pageNum) {
        if (pageNum < 0 || pageSize <= 0
                || StringUtils.isBlank(address) || StringUtils.isEmpty(direction)) {
            return P.failed("parameter is invalid");
        }
        List<TradingRecordDo> tradingRecordDos = tradingRecordService.selectByPage(true, pageSize + 1, pageNum, address, direction);
        if (CollectionUtils.isEmpty(tradingRecordDos)) {
            return P.success(null, false);
        }

        boolean hasNext = false;
        if (tradingRecordDos.size() > pageSize) {
            tradingRecordDos = tradingRecordDos.subList(0, tradingRecordDos.size() - 1);
            hasNext = true;
        }
        return P.success(tradingRecordDos, hasNext);
    }

}
