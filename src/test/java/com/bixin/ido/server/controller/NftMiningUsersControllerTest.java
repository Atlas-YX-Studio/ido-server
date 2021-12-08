package com.bixin.ido.server.controller;

import com.bixin.IdoServerApplication;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;

@Slf4j
@ActiveProfiles("local")
@SpringBootTest(classes = IdoServerApplication.class)
class NftMiningUsersControllerTest {

    @Resource
    private NftMiningUsersController nftMiningUsersController;

    private static final String userAddress = "0x3Cf910D97c98947277BE21D6b7625cfe";

    @Test
    void market() {
        nftMiningUsersController.market("");
    }

    @Test
    void stakingList() {
        log.info(nftMiningUsersController.stakingList(userAddress).toString());
    }

    @Test
    @SneakyThrows
    void rewardHarvest() {
        log.info(nftMiningUsersController.rewardHarvest(userAddress).toString());
        Thread.sleep(30000L);
    }

    @Test
    void reward() {
        log.info(nftMiningUsersController.fee().toString());
    }
}