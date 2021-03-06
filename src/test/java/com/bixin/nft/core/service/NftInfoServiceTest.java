package com.bixin.nft.core.service;

import com.alibaba.fastjson.JSON;
import com.bixin.IdoServerApplication;
import com.bixin.nft.service.NftInfoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;

@Slf4j
@ActiveProfiles("local")
@SpringBootTest(classes = IdoServerApplication.class)
public class NftInfoServiceTest {

    @Resource
    private NftInfoService nftInfoService;

    @Test
    void ownerNftList() {
        log.info("nftList:" + JSON.toJSONString(nftInfoService.getUnSellNftList("0x16d2e435cebab5eabbfd16402d4b22ea")));
    }

    @Test
    void unStakingNftList() {
        log.info("nftList:" + JSON.toJSONString(nftInfoService.getUnStakingNftList("0x16d2e435cebab5eabbfd16402d4b22ea")));
    }

}
