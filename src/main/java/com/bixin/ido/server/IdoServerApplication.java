package com.bixin.ido.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ido服务端
 */
@SpringBootApplication(scanBasePackages = {"com.bixin.ido.server"})
public class IdoServerApplication {

    public static void main(String[] args) {

        SpringApplication.run(IdoServerApplication.class, args);
    }

}
