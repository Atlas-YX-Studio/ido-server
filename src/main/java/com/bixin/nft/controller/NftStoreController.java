package com.bixin.nft.controller;

import com.bixin.ido.server.bean.vo.wrap.P;
import com.bixin.ido.server.bean.vo.wrap.R;
import com.bixin.nft.bean.DO.NftGroupDo;
import com.bixin.nft.bean.DO.NftMarketDo;
import com.bixin.nft.bean.vo.NftSelfSellingVo;
import com.bixin.nft.core.service.NftGroupService;
import com.bixin.nft.core.service.NftMarketService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.bixin.ido.server.constants.PathConstant.NFT_REQUEST_PATH_PREFIX;

/**
 * @author zhangcheng
 * create   2021/9/18
 */
@RestController
@RequestMapping(NFT_REQUEST_PATH_PREFIX + "/store")
public class NftStoreController {


    @Resource
    public NftMarketService nftMarketService;
    @Resource
    NftGroupService nftGroupService;

    @GetMapping("/selling")
    public R getALlByPage(@RequestParam(value = "userAddress", defaultValue = "") String userAddress) {

        if (StringUtils.isEmpty(userAddress)) {
            return R.failed("parameter is invalid");
        }
        NftMarketDo marketDo = NftMarketDo.builder().owner(userAddress).build();
        List<NftMarketDo> nftMarketDos = nftMarketService.listByObject(marketDo);

        if (CollectionUtils.isEmpty(nftMarketDos)) {
            return R.success(null);
        }

        Set<Long> groupIds = nftMarketDos.stream().map(p -> p.getGroupId()).collect(Collectors.toSet());
        Map<Long, NftGroupDo> map = new HashMap<>();
        groupIds.forEach(id -> {
            NftGroupDo nftGroupDo = nftGroupService.selectById(id);
            map.put(id, nftGroupDo);
        });

        List<NftSelfSellingVo> list = new ArrayList<>();
        for(NftMarketDo p: nftMarketDos){
            NftSelfSellingVo.NftSelfSellingVoBuilder builder = NftSelfSellingVo.builder();
            NftGroupDo nftGroupDo = map.get(p.getGroupId());
            if (Objects.nonNull(nftGroupDo)) {
                String boxToken = nftGroupDo.getBoxToken();
                String payToken = nftGroupDo.getPayToken();
                String nftMeta = nftGroupDo.getNftMeta();
                String nftBody = nftGroupDo.getNftBody();
                builder.boxToken(boxToken)
                        .payToken(payToken)
                        .nftMeta(nftMeta)
                        .nftBody(nftBody);
            }
            NftSelfSellingVo sellingVo = builder.build();
            BeanUtils.copyProperties(p, sellingVo);
            list.add(sellingVo);
        }

        return R.success(list);
    }
}
