package com.bixin.nft.biz;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class NftImagesUploadBizTest {

    @Resource
    private NftImagesUploadBiz nftImagesUploadBiz;

    @Test
    @SneakyThrows
    void asyncProcess() {
        nftImagesUploadBiz.asyncProcess();
        Thread.sleep(10000);
    }
}