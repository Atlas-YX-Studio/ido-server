package com.bixin.nft.controller;

import com.bixin.nft.core.service.NftInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @class: NftInfoController
 * @Description:  NFT信息记录表 Controller
 * @author: 系统
 * @created: 2021-09-15
 */
@RestController
@RequestMapping("/controller")
public class NftInfoController {

    @Autowired
    public NftInfoService nftInfoService;

}