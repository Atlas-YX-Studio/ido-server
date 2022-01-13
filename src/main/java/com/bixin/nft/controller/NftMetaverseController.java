package com.bixin.nft.controller;

import com.bixin.common.response.R;
import com.bixin.core.redis.RedisCache;
import com.bixin.nft.bean.bo.CompositeCardBean;
import com.bixin.nft.common.enums.CardElementType;
import com.bixin.nft.service.NftMetareverseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.bixin.common.constants.PathConstant.NFT_REQUEST_PATH_PREFIX;

/**
 * @author zhangcheng
 * create  2021/12/23
 */
@Slf4j
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
    public R compositeCard(@RequestBody CompositeCardBean bean) {
        if (StringUtils.isBlank(bean.getUserAddress()) || bean.getGroupId() <= 0
                || CollectionUtils.isEmpty(bean.getElementList())) {
            return R.failed("parameter is invalid");
        }
        boolean validElementType = true;
        for (CompositeCardBean.CustomCardElement cardElement : bean.getElementList()) {
            if (Objects.isNull(CardElementType.of(cardElement.getEleName()))) {
                validElementType = false;
                break;
            }
        }
        if (!validElementType) {
            return R.failed("parameter is invalid");
        }

        String key = bean.getUserAddress()
                + "_" + bean.getGroupId()
                + "_" + bean.getSex()
                + "_" + bean.getElementList().stream()
                .map(CompositeCardBean.CustomCardElement::getId)
                .collect(Collectors.toList());
        String requestId = UUID.randomUUID().toString().replaceAll("-", "");

        try {
            MutablePair<Long, String> pair = redisCache.tryGetDistributedLock(
                    key,
                    requestId,
                    lockExpiredTime,
                    lockNextExpiredTime,
                    () -> nftMetareverseService.compositeCard(bean)
            );
            return R.success(Map.of("id", pair.getLeft(),
                    "image", pair.getRight()));
        } catch (Exception e) {
            log.error("create nft image exception", e);
            return R.failed(e.getMessage());
        }

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
