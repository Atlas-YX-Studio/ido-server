package com.bixin.nft.biz;

import com.bixin.IdoServerApplication;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.MutablePair;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;

@Slf4j
@ActiveProfiles("local")
@SpringBootTest(classes = IdoServerApplication.class)
class NftImagesUploadBizTest {

    @Resource
    private NftImagesUploadBiz nftImagesUploadBiz;

    @Test
    @SneakyThrows
    void asyncProcess() {
        nftImagesUploadBiz.asyncProcess();
        Thread.sleep(10000);
    }


    @Test
    void split() {
        MutablePair<String, String> pair = nftImagesUploadBiz.splitImage("data:img/jpeg;base64,/9j/4AAQSkZJRgABAQEASABIAAD/2wBDA");
    }
}