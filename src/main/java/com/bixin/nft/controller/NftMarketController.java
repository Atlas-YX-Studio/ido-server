package com.bixin.nft.controller;

import com.bixin.common.response.P;
import com.bixin.nft.bean.DO.NftGroupDo;
import com.bixin.nft.bean.vo.NftSelfSellingVo;
import com.bixin.nft.service.NftGroupService;
import com.bixin.nft.service.NftMarketService;
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
@RestController
@RequestMapping(NFT_REQUEST_PATH_PREFIX + "/market")
public class NftMarketController {

    @Resource
    NftMarketService nftMarketService;
    @Resource
    NftGroupService nftGroupService;

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
        List<Map<String, Object>> maps = nftMarketService.selectByPage(true, pageSize + 1, pageNum, sort, groupId, sortRule, nftType);
        if (CollectionUtils.isEmpty(maps)) {
            return P.success(null, false);
        }

        boolean hasNext = false;
        if (maps.size() > pageSize) {
            maps = maps.subList(0, maps.size() - 1);
            hasNext = true;
        }

        List<NftSelfSellingVo> list = new ArrayList<>();
        maps.stream().forEach(p -> list.add(NftSelfSellingVo.of(p)));

        Set<Long> groupIds = list.stream().map(p -> p.getGroupId()).collect(Collectors.toSet());
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
        }

        return P.success(list, hasNext);
    }

}