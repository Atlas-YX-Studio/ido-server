package com.bixin.ido.controller;

import com.bixin.common.response.R;
import com.bixin.common.config.StarConfig;
import com.bixin.common.constants.PathConstant;
import com.bixin.core.redis.RedisCache;
import com.bixin.ido.service.ILPMiningService;
import com.bixin.ido.service.ITradingRewardUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @class: TradingRewardUserController
 * @Description:  用户交易挖矿收益表 Controller
 * @author: 系统
 * @created: 2021-11-08
 */
//@RestController
@RequestMapping(PathConstant.MINING_REQUEST_PATH_PREFIX + "/lp")
public class LPMiningController {

    @Autowired
    private ITradingRewardUserService tradingRewardUserService;
    @Autowired
    private ILPMiningService lpMiningService;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private StarConfig starConfig;

    @PostMapping("/reward/harvest")
    public R harvestCurrentReward(@RequestParam(value = "address") String address, @RequestParam(value = "poolId") Long poolId) {
        String hash = lpMiningService.harvestReward(address, poolId);
        return R.success(hash);
    }

    @GetMapping("/pool/list")
    public R poolList(@RequestParam(value = "address", required = false) String address) {
        return R.success(lpMiningService.poolList(address));
    }

    @GetMapping("/market")
    public R market() {
        return R.success(lpMiningService.market());
    }

    @GetMapping("/reward")
    public R reward(@RequestParam(value = "address") String address, @RequestParam(value = "poolId") Long poolId) {
        return R.success(lpMiningService.reward(address, poolId));
    }

}
