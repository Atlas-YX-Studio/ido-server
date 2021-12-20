package com.bixin.nft.controller;

import com.bixin.nft.service.NftKikoCatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @class: NftKikoCatController
 * @Description:  NFT Kiko猫信息表 Controller
 * @author: 系统
 * @created: 2021-09-15
 */
@RestController
@RequestMapping("/controller")
public class NftKikoCatController {

    @Autowired
    public NftKikoCatService nftKikoCatService;

}