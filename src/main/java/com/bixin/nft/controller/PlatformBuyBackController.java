package com.bixin.nft.controller;

import com.bixin.ido.server.bean.vo.wrap.R;
import com.bixin.ido.server.constants.PathConstant;
import com.bixin.ido.server.service.IPlatformBuyBackService;
import com.bixin.ido.server.service.impl.PlatformBuyBackServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping(PathConstant.NFT_REQUEST_PATH_PREFIX +"/platBuyBack")
public class PlatformBuyBackController {

    @Resource
    private IPlatformBuyBackService platformBuyBackService;

    @GetMapping("/getALL")
    public R list(@RequestParam(name = "groupId", defaultValue = "0") long groupId,
            @RequestParam(name = "currency", defaultValue = "all") String currency,
            @RequestParam(name = "sort", defaultValue = "0") int sort,
            @RequestParam(name = "pageSize", defaultValue = "20") long pageSize,
            @RequestParam(name = "nextId", defaultValue = "0") long nextId) {
        List<PlatformBuyBackServiceImpl.BuyBackOrder> orders = platformBuyBackService.getOrders(groupId, currency, sort, pageSize, nextId);
        return R.success(orders);
    }

}
