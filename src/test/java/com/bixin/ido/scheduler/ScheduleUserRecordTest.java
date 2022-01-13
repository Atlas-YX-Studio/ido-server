package com.bixin.ido.scheduler;

import com.bixin.IdoServerApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;

@ActiveProfiles("local")
@SpringBootTest(classes = IdoServerApplication.class)
class ScheduleUserRecordTest {

    @Resource
    private ScheduleUserRecord scheduleUserRecord;

    @Test
    void updateUserTokenAmount() {
        scheduleUserRecord.updateUserTokenAmount();
    }
}