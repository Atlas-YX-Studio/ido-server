package com.bixin.ido.server.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class JwtUtilTest {

    @Test
    void encode() {
        log.info(JwtUtil.encode());
    }

    @Test
    void decode() {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJraWtvIiwiZXhwIjoxNjM0NzIzMzIyLCJpYXQiOjE2MzQ3MjI3MjJ9.JCdyXT7zLoG9ksiyrCvfcffR-kIaGEdstEbFubuw4dc";
        log.info("" + JwtUtil.decode(token));
    }
}