package com.bixin.ido.server.controller;


import com.bixin.ido.server.bean.vo.wrap.R;
import com.bixin.ido.server.service.NftMiningUsersService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 用户NFT挖矿表 前端控制器
 *
 * @author Xiang Feihan
 * @since 2021-11-26
 */
@RestController
@RequestMapping("/nft-mining-users")
public class NftMiningUsersController {

    @Resource
    private NftMiningUsersService nftMiningUsersService;

    @GetMapping("/market")
    public R market(@RequestParam(required = false, value = "address") String address) {
        return R.success(nftMiningUsersService.market(address));
    }

}
