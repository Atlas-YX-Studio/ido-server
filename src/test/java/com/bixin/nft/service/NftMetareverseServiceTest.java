package com.bixin.nft.service;

import com.bixin.IdoServerApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;

@Slf4j
@ActiveProfiles("local")
@SpringBootTest(classes = IdoServerApplication.class)
class NftMetareverseServiceTest {

    @Resource
    private NftMetareverseService nftMetareverseService;

    @Test
    void selfResource() {
        log.info(nftMetareverseService.selfResource("0x15b4f751AFfeE5Edb905fF4c3252f603", "split").toString());
    }
}