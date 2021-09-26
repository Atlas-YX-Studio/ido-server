package com.bixin.nft.controller;

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
class NftInfoControllerTest {
    @Resource
    private NftInfoController nftInfoController;

    @Test
    void groupList() {
        log.info("groupList:" + JSON.toJSONString(nftInfoController.groupList()));
    }
}