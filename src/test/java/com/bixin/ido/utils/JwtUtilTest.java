package com.bixin.ido.utils;

import com.bixin.common.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class JwtUtilTest {

    @Test
    void encode() {
        log.info(JwtUtil.encode());
    }

    @Test
    void decode() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJraWtvIiwiZXhwIjoxNjM2Njk0OTYzMzM4LCJpYXQiOjE2MzY2OTQzNjN9.VlFweQMRug-ck4dqnnbp-PyRmBD2AWMEK4eY3HT84_8";
        log.info("" + JwtUtil.decode(token));
    }
}