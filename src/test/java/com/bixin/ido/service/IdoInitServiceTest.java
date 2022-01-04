package com.bixin.ido.service;

import com.bixin.IdoServerApplication;
import com.bixin.ido.biz.IdoContractBiz;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;
import java.math.BigInteger;

@ActiveProfiles("local")
@SpringBootTest(classes = IdoServerApplication.class)
public class IdoInitServiceTest {

    @Resource
    private IdoContractBiz idoContractBiz;

    @Test
    void init() {
        idoContractBiz.init();
    }

    @Test
    void createOffering() {
        idoContractBiz.createOffering();
    }

    @Test
    void stateChange() {
        idoContractBiz.stateChange((byte) 4);
    }

    @Test
    void initToken() {
        idoContractBiz.initToken("0x5b876a58b0e1cff855b6489cd8cf3bec::DummyToken::DUMMY");
    }

    @Test
    void mintToken() {
        idoContractBiz.mintToken("0x5b876a58b0e1cff855b6489cd8cf3bec::DummyToken::DUMMY", BigInteger.valueOf(1000000000000000000L));
    }

}
