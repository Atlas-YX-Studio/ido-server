package com.bixin.ido.service;

import com.bixin.IdoServerApplication;
import com.bixin.ido.biz.IdoContractBiz;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;

@ActiveProfiles("local")
@SpringBootTest(classes = IdoServerApplication.class)
public class IdoInitServiceTest {

    @Resource
    private IdoContractBiz idoContractBiz;

    @Test
    void createOffering() {
        idoContractBiz.createOffering();
    }

    @Test
    void stateChange() {
        idoContractBiz.stateChange((byte) 2);
    }

}
