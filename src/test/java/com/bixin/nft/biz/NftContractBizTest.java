package com.bixin.nft.biz;

import com.bixin.IdoServerApplication;
import com.bixin.nft.service.ContractService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;

@Slf4j
@ActiveProfiles("local")
@SpringBootTest(classes = IdoServerApplication.class)
class NftContractBizTest {
    @Resource
    private NftContractBiz nftContractBiz;
    @Resource
    private ContractService contractService;


    @Test
    void initNFTMarket() {
        nftContractBiz.initNFTMarket(new BigInteger("10"), new BigInteger("10"));
    }

    @Test
    @SneakyThrows
    void createNFT() {
        nftContractBiz.createNFT();
    }

    @Test
    void initBuyBackNFT() {
        nftContractBiz.initBuyBackNFT(1L, "0x1::STC::STC");
    }

    @Test
    void buyBackNFT() {
        nftContractBiz.buyBackNFT(10000L, "0x1::STC::STC", BigDecimal.valueOf(0.01));
    }

    @Test
    void buyFromOffering() {
        assert nftContractBiz.buyFromOffering();
    }

    @Test
    void open_box() {
        assert nftContractBiz.open_box("0xa85291039ddad8845d5097624c81c3fd", "KikoCatCard04");
    }

    @Test
    void sellNFT() {
        assert nftContractBiz.sellNFT();
    }

    @Test
    void sellBox() {
        assert nftContractBiz.sellBox();
    }

    @Test
    void initNFTMining() {
        nftContractBiz.initNFTMining(10016L);
    }

    @Test
    void getNftCountId() {
        log.info("nftCounterId:{}", nftContractBiz.getNftCounterId("0x69f1e543a3bef043b63bed825fcd2cf6::KikoCatElement07::KikoCatMeta"));
    }
}