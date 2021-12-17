package com.bixin.nft.controller;

import com.bixin.common.response.P;
import com.bixin.common.response.R;
import com.bixin.common.constants.PathConstant;
import com.bixin.ido.service.IPlatformBuyBackService;
import com.bixin.ido.service.impl.PlatformBuyBackServiceImpl;
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
    public P list(@RequestParam(name = "groupId", defaultValue = "0") long groupId,
            @RequestParam(name = "currency", defaultValue = "all") String currency,
            @RequestParam(name = "sort", defaultValue = "0") int sort,
            @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(name = "pageSize", defaultValue = "20") int pageSize) {
        List<PlatformBuyBackServiceImpl.BuyBackOrder> orders = platformBuyBackService.getOrders(groupId, currency, sort, pageNum, pageSize);
        boolean hasNext = false;
        if (orders.size() > pageSize) {
            orders = orders.subList(0, pageSize);
            hasNext = true;
        }

        return P.success(orders, hasNext);
    }

    @GetMapping("/getOrder")
    public R getOrder(@RequestParam("id") Long id, @RequestParam("groupId") Long groupId, @RequestParam("currency") String currency) {
        return R.success(platformBuyBackService.getOrder(id, groupId, currency));
    }

}
