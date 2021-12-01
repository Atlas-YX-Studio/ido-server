package com.bixin.ido.server.controller;


import com.bixin.ido.server.bean.vo.wrap.R;
import com.bixin.ido.server.service.NftMiningUsersService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 用户NFT挖矿表 前端控制器
 *
 * @author Xiang Feihan
 * @since 2021-11-26
 */
@RestController
@RequestMapping("/v1/mining/nft")
public class NftMiningUsersController {

    @Resource
    private NftMiningUsersService nftMiningUsersService;

    @GetMapping("/market")
    public R market(@RequestParam(required = false, value = "address") String address) {
        return R.success(nftMiningUsersService.market(address));
    }

    @PostMapping("/reward/harvest")
    public R rewardHarvest(@RequestParam(required = false, value = "address") String address) {
        return R.success(nftMiningUsersService.harvestReward(address));
    }

}
