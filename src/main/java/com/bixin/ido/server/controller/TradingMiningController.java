package com.bixin.ido.server.controller;

import com.bixin.ido.server.bean.vo.wrap.R;
import com.bixin.ido.server.constants.PathConstant;
import com.bixin.ido.server.service.ITradingMiningService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(PathConstant.MINING_REQUEST_PATH_PREFIX + "/trading")
public class TradingMiningController {

    @Resource
    private ITradingMiningService tradingMiningService;

    @GetMapping("/pool/list")
    public R poolList(@RequestParam("address") String address) {
        return R.success(tradingMiningService.poolList(address));
    }

    @GetMapping("/market")
    public R market(@RequestParam("address") String address) {
        return R.success(tradingMiningService.market(address));
    }

}
