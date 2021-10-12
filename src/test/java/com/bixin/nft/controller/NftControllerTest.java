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
class NftControllerTest {
    @Resource
    private NftInfoController nftInfoController;

    private final static String SECRET_KEY = "766dF569970B22B29152eB326dad1b1E";

    @Test
    void groupList() {
        log.info("groupList:" + JSON.toJSONString(nftInfoController.groupList()));
    }

    @Test
    void offeringList() {
        log.info("groupList:" + JSON.toJSONString(nftInfoController.offeringList(10, 2)));
    }

}