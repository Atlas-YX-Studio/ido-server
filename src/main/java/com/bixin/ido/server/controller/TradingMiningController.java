package com.bixin.ido.server.controller;

import com.bixin.ido.server.bean.vo.wrap.R;
import com.bixin.ido.server.constants.PathConstant;
import com.bixin.ido.server.service.ITradingMiningService;
import com.bixin.ido.server.service.ITradingRewardUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @class: TradingRewardUserController
 * @Description:  用户交易挖矿收益表 Controller
 * @author: 系统
 * @created: 2021-11-08
 */
@RestController
@RequestMapping(PathConstant.MINING_REQUEST_PATH_PREFIX + "/trading")
public class TradingMiningController {

    @Autowired
    private ITradingRewardUserService tradingRewardUserService;
    @Autowired
    private ITradingMiningService tradingMiningService;

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

}
