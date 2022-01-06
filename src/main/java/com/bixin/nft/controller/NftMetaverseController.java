package com.bixin.nft.controller;

import com.bixin.common.response.R;
import com.bixin.nft.bean.bo.CompositeCardBean;
import com.bixin.nft.service.NftMetareverseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/occupationGroup")
    public R getOccupationGroup() {
        return R.success(nftMetareverseService.getSumByOccupationGroup());
    }

    @PostMapping("/compositeCard")
    public R compositeCard(CompositeCardBean bean) {
        if (StringUtils.isBlank(bean.getUserAddress()) || bean.getElementList().size() == 0) {
            return R.failed("parameter is invalid");
        }
        List<Map<String, Object>> group = nftMetareverseService.getSumByOccupationGroup();

        return R.success(group);
    }


    @GetMapping("/analysisCard")
    public R analysisCard(@RequestParam(value = "userAddress", defaultValue = "") String userAddress,
                          @RequestParam(value = "cardId", defaultValue = "0") long cardId) {
        if (StringUtils.isBlank(userAddress) || cardId <= 0) {
            return R.failed("parameter is invalid");
        }


        return R.success();
    }

}
