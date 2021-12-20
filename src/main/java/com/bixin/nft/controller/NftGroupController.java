package com.bixin.nft.controller;

import com.bixin.nft.service.NftGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @class: NftGroupController
 * @Description:  NFT分组表 Controller
 * @author: 系统
 * @created: 2021-09-15
 */
@RestController
@RequestMapping("/controller")
public class NftGroupController {

    @Autowired
    public NftGroupService nftGroupService;

}