package com.bixin.nft.controller;

import com.bixin.common.response.R;
import com.bixin.core.redis.RedisCache;
import com.bixin.nft.bean.bo.CompositeCardBean;
import com.bixin.nft.service.NftMetareverseService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.bixin.common.constants.PathConstant.NFT_REQUEST_PATH_PREFIX;

/**
 * @author zhangcheng
 * create  2021/12/23
 */
@RestController
@RequestMapping(NFT_REQUEST_PATH_PREFIX + "/meta")
public class NftMetaverseController {

    @Resource
    NftMetareverseService nftMetareverseService;
    @Resource
    RedisCache redisCache;

    static final long lockExpiredTime = 3000L;
    static final long lockNextExpiredTime = 0L;

    @GetMapping("/occupationGroup")
    public R getOccupationGroup() {
        return R.success(nftMetareverseService.getSumByOccupationGroup());
    }

    @PostMapping("/compositeCard")
    public R compositeCard(CompositeCardBean bean) {
        if (StringUtils.isBlank(bean.getUserAddress()) || bean.getGroupId() <= 0
                || CollectionUtils.isEmpty(bean.getElementList())) {
            return R.failed("parameter is invalid");
        }

        String key = bean.getUserAddress()
                + "_" + bean.getGroupId()
                + "_" + bean.getSex()
                + "_" + bean.getElementList().stream()
                .map(CompositeCardBean.CustomCardElement::getId)
                .collect(Collectors.toList());
        String requestId = UUID.randomUUID().toString().replaceAll("-", "");

        String image = redisCache.tryGetDistributedLock(
                key,
                requestId,
                lockExpiredTime,
                lockNextExpiredTime,
                () -> nftMetareverseService.compositeCard(bean)
        );

        return R.success(image);
    }

    @GetMapping("/analysisCard")
    public R analysisCard(@RequestParam(value = "userAddress", defaultValue = "") String userAddress,
                          @RequestParam(value = "cardId", defaultValue = "0") long cardId) {
        if (StringUtils.isBlank(userAddress) || cardId <= 0) {
            return R.failed("parameter is invalid");
        }


        return R.success();
    }

    @GetMapping("/selfResource")
    public R selfResource(@RequestParam(value = "userAddress", defaultValue = "") String userAddress,
                          @RequestParam(value = "nftType", defaultValue = "All") String nftType) {
        if (StringUtils.isBlank(userAddress) || StringUtils.isBlank(nftType)) {
            return R.failed("parameter is invalid");
        }
        return R.success(nftMetareverseService.selfResource(userAddress, nftType));
    }

}
