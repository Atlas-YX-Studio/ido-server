package com.bixin.nft.biz;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NftImagesUploadBizTest {

    @Resource
    private NftImagesUploadBiz nftImagesUploadBiz;

    @Test
    void asyncProcess() {
        nftImagesUploadBiz.asyncProcess();
    }
}