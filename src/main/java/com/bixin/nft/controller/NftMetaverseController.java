package com.bixin.nft.controller;

import com.bixin.common.response.R;
import com.bixin.nft.bean.bo.CompositeCardBean;
import com.bixin.nft.service.NftMetareverseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
    public R compositeCard(CompositeCardBean bean){
        if(StringUtils.isBlank(bean.getUserAddress())||bean.getElementIds().size()==0){
            return R.failed("parameter is invalid");
        }


        return R.success();
    }

}
