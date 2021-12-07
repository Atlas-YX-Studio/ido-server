package com.bixin.ido.server.controller;


import com.bixin.ido.server.bean.vo.wrap.R;
import com.bixin.ido.server.config.StarConfig;
import com.bixin.ido.server.entity.NftStakingUsers;
import com.bixin.ido.server.service.NftMiningUsersService;
import com.bixin.ido.server.service.NftStakingUsersService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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
    private NftStakingUsersService nftStakingUsersService;
    @Resource
    private StarConfig starConfig;

    @GetMapping("/market")
    public R market(@RequestParam(required = false, value = "address") String address) {
        return R.success(nftMiningUsersService.market(address));
    }

    @GetMapping("/staking/list")
    public R stakingList(@RequestParam String address) {
        List<NftStakingUsers> nftStakingUsers = nftStakingUsersService.lambdaQuery()
                .eq(NftStakingUsers::getAddress, address)
                .orderByAsc(NftStakingUsers::getOrder)
                .list();
        return R.success(nftStakingUsers);
    }

    @PostMapping("/reward/harvest")
    public R rewardHarvest(@RequestParam String address) {
        return R.success(nftMiningUsersService.harvestReward(address));
    }

    @GetMapping("/fee")
    public R reward() {
        return R.success(starConfig.getMining().getNftMiningStcFee());
    }

}
