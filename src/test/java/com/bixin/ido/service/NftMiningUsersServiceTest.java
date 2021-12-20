package com.bixin.ido.server.service;

import com.bixin.IdoServerApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;

@Slf4j
@ActiveProfiles("local")
@SpringBootTest(classes = IdoServerApplication.class)
class NftMiningUsersServiceTest {

    @Resource
    private NftMiningUsersService nftMiningUsersService;

    @Test
    void computeReward() {
        nftMiningUsersService.computeReward(0L);
    }
}