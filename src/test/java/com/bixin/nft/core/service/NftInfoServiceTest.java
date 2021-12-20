package com.bixin.nft.service;

import com.alibaba.fastjson.JSON;
import com.bixin.IdoServerApplication;
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
        log.info("nftList:" + JSON.toJSONString(nftInfoService.getUserNftList("0xa85291039ddad8845d5097624c81c3fd")));
    }

}
