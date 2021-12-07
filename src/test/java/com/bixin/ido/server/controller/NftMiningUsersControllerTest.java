package com.bixin.ido.server.controller;

import com.bixin.IdoServerApplication;
import com.bixin.ido.server.service.NftStakingUsersService;
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
    @Resource
    private NftStakingUsersService nftStakingUsersService;

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
    void rewardHarvest() {
        nftMiningUsersController.rewardHarvest(userAddress);
    }

    @Test
    void reward() {
        log.info(nftMiningUsersController.fee().toString());
    }
}