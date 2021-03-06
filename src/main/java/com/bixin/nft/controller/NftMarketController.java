package com.bixin.nft.controller;

import com.bixin.common.response.P;
import com.bixin.nft.bean.DO.NftCompositeCard;
import com.bixin.nft.bean.DO.NftCompositeElement;
import com.bixin.nft.bean.DO.NftGroupDo;
import com.bixin.nft.bean.vo.NftSelfSellingVo;
import com.bixin.nft.common.enums.NftBoxType;
import com.bixin.nft.common.enums.NftType;
import com.bixin.nft.service.NftCompositeCardService;
import com.bixin.nft.service.NftGroupService;
import com.bixin.nft.service.NftMarketService;
import com.bixin.nft.service.NftMetareverseService;
import lombok.extern.slf4j.Slf4j;
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
 * @class: NftMarketController
 * @Description: NFT/box市场销售列表 Controller
 * @author: 系统
 * @created: 2021-09-17
 */
@Slf4j
@RestController
@RequestMapping(NFT_REQUEST_PATH_PREFIX + "/market")
public class NftMarketController {

    @Resource
    NftMarketService nftMarketService;
    @Resource
    NftGroupService nftGroupService;
    @Resource
    NftCompositeCardService compositeCardService;
    @Resource
    NftMetareverseService metareverseService;


    @GetMapping("/getALL")
    public P getALlByPage(@RequestParam(value = "groupId", defaultValue = "0") long groupId,
                          @RequestParam(value = "nftType", defaultValue = "") String nftType,
                          @RequestParam(value = "sort", defaultValue = "0") int sort,
                          @RequestParam(value = "sortRule", defaultValue = "") String sortRule,
                          @RequestParam(value = "pageSize", defaultValue = "20") long pageSize,
                          @RequestParam(value = "pageNum", defaultValue = "0") long pageNum) {

        if (groupId < 0 || sort < 0 || pageNum < 0 || pageSize <= 0
                || StringUtils.isBlank(sortRule)) {
            return P.failed("parameter is invalid");
        }
        List<String> nftTypes = List.of();
        if (NftBoxType.NFT.getDesc().equals(nftType)) {
            nftTypes = List.of(NftBoxType.NFT.getDesc(), NftBoxType.COMPOSITE_CARD.getDesc());
        } else if (StringUtils.isNotBlank(nftType)) {
            nftTypes = List.of(nftType);
        }
        List<Map<String, Object>> maps = nftMarketService.selectByPage(true, pageSize + 1, pageNum, sort, groupId, sortRule, nftTypes);
        if (CollectionUtils.isEmpty(maps)) {
            return P.success(null, false);
        }

        boolean hasNext = false;
        if (maps.size() > pageSize) {
            maps = maps.subList(0, maps.size() - 1);
            hasNext = true;
        }

        List<NftSelfSellingVo> list = new ArrayList<>();
        maps.forEach(p -> {
            NftSelfSellingVo sellingVo = NftSelfSellingVo.of(p);
            if (NftBoxType.NFT.getDesc().equals(nftType)) {
                // 原生NFT，包括普通NFT+原生可合成卡牌
                if (NftBoxType.NFT.getDesc().equals(sellingVo.getType())) {
                    list.add(sellingVo);
                } else if (NftBoxType.COMPOSITE_CARD.getDesc().equals(sellingVo.getType()) && sellingVo.getOriginal()) {
                    list.add(sellingVo);
                }
            } else if (NftBoxType.COMPOSITE_CARD.getDesc().equals(nftType)) {
                // 组合NFT，只包括分解后重新合成的NFT
                if (!sellingVo.getOriginal()) {
                    list.add(sellingVo);
                }
            } else {
                list.add(sellingVo);
            }
        });

        Set<Long> groupIds = list.stream().map(NftSelfSellingVo::getGroupId).collect(Collectors.toSet());
        Map<Long, NftGroupDo> map = new HashMap<>();
        groupIds.forEach(id -> map.put(id, nftGroupService.selectById(id)));

        List<Long> cardNftIds = list.stream().filter(p -> NftBoxType.COMPOSITE_CARD.getDesc().equalsIgnoreCase(p.getType()))
                .map(NftSelfSellingVo::getNftBoxId).collect(Collectors.toList());
        Set<Long> eleNftIds = list.stream().filter(p -> NftBoxType.COMPOSITE_ELEMENT.getDesc().equalsIgnoreCase(p.getType()))
                .map(NftSelfSellingVo::getNftBoxId).collect(Collectors.toSet());

        List<NftCompositeElement> compositeElements = new ArrayList<>();
        if (!CollectionUtils.isEmpty(cardNftIds)) {
            List<NftCompositeCard> compositeCards = metareverseService.getCompositeCards(cardNftIds);
            if (!CollectionUtils.isEmpty(compositeCards)) {
                for (NftCompositeCard p : compositeCards) {
                    List<Long> elementIds = NftCompositeCard.getElementIds(p);
                    List<NftCompositeElement> elementList = metareverseService.getCompositeElements(new HashSet<>(elementIds));
                    if (CollectionUtils.isEmpty(elementList)) {
                        log.warn("NftCompositeElement 不存在，nftIds = {}", elementIds);
                        continue;
                    }
                    compositeElements.addAll(elementList);
                }
            }
        }
        if (!CollectionUtils.isEmpty(eleNftIds)) {
            compositeElements = metareverseService.getCompositeElements(eleNftIds);
        }
        Map<Long, List<NftCompositeElement>> eleNftMap = compositeElements.stream()
                .collect(Collectors.groupingBy(NftCompositeElement::getInfoId));

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
            NftType type = NftType.of(p.getType());
            if (Objects.isNull(type)) {
                type = NftType.NORMAL;
            }
            p.setNftType(type);

            Long nftBoxId = p.getNftBoxId();
            if (!CollectionUtils.isEmpty(eleNftMap) && Objects.nonNull(eleNftMap.get(nftBoxId))) {
                p.setCompositeElements(eleNftMap.get(nftBoxId));
            }
        }

        return P.success(list, hasNext);
    }

}