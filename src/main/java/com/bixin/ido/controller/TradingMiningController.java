package com.bixin.ido.controller;

import com.bixin.common.response.R;
import com.bixin.common.config.StarConfig;
import com.bixin.common.constants.CommonConstant;
import com.bixin.common.constants.PathConstant;
import com.bixin.core.redis.RedisCache;
import com.bixin.ido.service.ITradingMiningService;
import com.bixin.ido.service.ITradingRewardUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * @class: TradingRewardUserController
 * @Description:  用户交易挖矿收益表 Controller
 * @author: 系统
 * @created: 2021-11-08
 */
//@RestController
@RequestMapping(PathConstant.MINING_REQUEST_PATH_PREFIX + "/trading")
public class TradingMiningController {

    @Autowired
    private ITradingRewardUserService tradingRewardUserService;
    @Autowired
    private ITradingMiningService tradingMiningService;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private StarConfig starConfig;

    @PostMapping("/currentReward/harvest")
    public R harvestCurrentReward(@RequestParam("address") String address) {
        String hash = tradingMiningService.harvestCurrentReward(address);
        return R.success(hash);
    }

    @PostMapping("/freedReward/harvest")
    public R harvestFreedReward(@RequestParam("address") String address) {
        String hash = tradingMiningService.harvestFreedReward(address);
        return R.success();
    }

    @GetMapping("/pool/list")
    public R poolList(@RequestParam(value = "address", required = false) String address) {
        return R.success(tradingMiningService.poolList(address));
    }

    @GetMapping("/market")
    public R market(@RequestParam(value = "address", required = false) String address) {
        return R.success(tradingMiningService.market(address));
    }

    @GetMapping("/reward")
    public R reward(@RequestParam(value = "address", required = false) String address) {
        return R.success(tradingMiningService.reward(address));
    }

    @GetMapping("/fee")
    public R reward() {
        BigDecimal stcFeePrice = (BigDecimal) redisCache.getValue(CommonConstant.STC_FEE_PRICE_KEY);
        BigDecimal kikoFee = starConfig.getMining().getStcFee().subtract(stcFeePrice);
        return R.success(kikoFee);
    }

}
