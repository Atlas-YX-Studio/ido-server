package com.bixin.nft.service;

import com.bixin.IdoServerApplication;
import com.bixin.nft.service.impl.NftCompositeCardServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;

@ActiveProfiles("local")
@SpringBootTest(classes = IdoServerApplication.class)
class NftCompositeCardServiceTest {
    @Resource
    private NftCompositeCardServiceImpl nftCompositeCardService;

    @Test
    void createCompositeNFT() {
        nftCompositeCardService.createCompositeNFT(10012);
    }

    @Test
    void resolve_card() {
        assert nftCompositeCardService.resolve_card("0xa85291039ddad8845d5097624c81c3fd", "KikoCatCard04", 1L);
    }

    @Test
    void mintCustomCardNFT() {
        assert nftCompositeCardService.mintCustomCardNFT("0xa85291039ddad8845d5097624c81c3fd", 10004L);
    }
}