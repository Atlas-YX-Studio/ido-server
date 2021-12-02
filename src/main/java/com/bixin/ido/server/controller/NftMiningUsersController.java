package com.bixin.ido.server.controller;


import com.bixin.ido.server.bean.vo.wrap.R;
import com.bixin.ido.server.service.NftMiningUsersService;
import com.bixin.nft.core.service.NftInfoService;
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
    @Resource
    private NftInfoService nftInfoService;

    @GetMapping("/market")
    public R market(@RequestParam(required = false, value = "address") String address) {
        return R.success(nftMiningUsersService.market(address));
    }

    @GetMapping("/unstaking/list")
    public R unStakingList(@RequestParam(required = false, value = "address") String address) {
        return R.success(nftInfoService.getUserNftList(address));
    }

    @PostMapping("/reward/harvest")
    public R rewardHarvest(@RequestParam(required = false, value = "address") String address) {
        return R.success(nftMiningUsersService.harvestReward(address));
    }

}
