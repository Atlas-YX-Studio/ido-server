package com.bixin.nft.controller;

import com.bixin.nft.core.service.NftMarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @class: NftMarketController
 * @Description:  NFT/box市场销售列表 Controller
 * @author: 系统
 * @created: 2021-09-17
 */
@RestController
@RequestMapping("/controller")
public class NftMarketController {

    @Autowired
    public NftMarketService nftMarketService;

}