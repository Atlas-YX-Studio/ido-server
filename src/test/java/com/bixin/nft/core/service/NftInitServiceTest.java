package com.bixin.nft.core.service;

import com.bixin.IdoServerApplication;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;

@ActiveProfiles("local")
@SpringBootTest(classes = IdoServerApplication.class)
class NftInitServiceTest {
    @Resource
    private NftContractService nftInitService;
    @Resource
    private ContractService contractService;


    @Test
    void initNFTMarket() {
        nftInitService.initNFTMarket(new BigInteger("10"), new BigInteger("10"));
    }

    @Test
    @SneakyThrows
    void createNFT() {
        nftInitService.createNFT();
        Thread.sleep(10000);
    }

    @Test
    void initBuyBackNFT() {
        nftInitService.initBuyBackNFT(1L, "0x1::STC::STC");
    }

    @Test
    void buyBackNFT() {
        nftInitService.buyBackNFT(10000L, "0x1::STC::STC", BigDecimal.valueOf(0.01));
    }

    @Test
    void sellNFT() {
        assert nftInitService.sellNFT();
    }

    @Test
    void sellBox() {
        assert nftInitService.sellBox();
    }

}