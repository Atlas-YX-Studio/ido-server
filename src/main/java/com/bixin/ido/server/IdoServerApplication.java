package com.bixin.ido.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * ido服务端
 */
@Slf4j
@EnableScheduling
@EnableConfigurationProperties
@SpringBootApplication(scanBasePackages = {"com.bixin.ido.server"})
public class IdoServerApplication {

    public static void main(String[] args) {

        SpringApplication.run(IdoServerApplication.class, args);

        log.info("IdoServerApplication running ...");

    }

}
