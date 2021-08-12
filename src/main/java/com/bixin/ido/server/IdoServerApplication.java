package com.bixin.ido.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * ido服务端
 */
@EnableScheduling
@EnableConfigurationProperties
@SpringBootApplication(scanBasePackages = {"com.bixin.ido.server"})
public class IdoServerApplication {

    public static void main(String[] args) {

        SpringApplication.run(IdoServerApplication.class, args);
    }

}
