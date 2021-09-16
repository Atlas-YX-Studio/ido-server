package com.bixin.nft.core.service;

import com.bixin.IdoServerApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;

@ActiveProfiles("local")
@SpringBootTest(classes = IdoServerApplication.class)
class NftInitServiceTest {
    @Resource
    private NftInitService nftInitService;
    @Resource
    private ContractService contractService;

    @Test
    void createNft() {
        nftInitService.initNft();
    }

    @Test
    void checkTxt() {
        contractService.checkTxt("0xfa55d887d5769f065d7f21049a16845c6f1712e773cc9375db0aaf9f7b36ccc5");
    }

    @Test
    void callFunction() {
//        contractService.getResource();
    }
}