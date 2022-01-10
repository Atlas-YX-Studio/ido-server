package com.bixin.ido.controller;

import com.bixin.IdoServerApplication;
import com.bixin.common.response.R;
import com.bixin.nft.controller.NftMetaverseController;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;

/**
 * @author zhangcheng
 * create  2022/1/11
 */
@Slf4j
@ActiveProfiles("local")
@SpringBootTest(classes = IdoServerApplication.class)
public class MetaControllerTest {

    @Resource
    NftMetaverseController metaverseController;

    @Test
    void testSelfResource() {
        R all = metaverseController.selfResource("0xa85291039ddad8845d5097624c81c3fd", "all");
        log.info("self resource info: {}", all);

    }
}
