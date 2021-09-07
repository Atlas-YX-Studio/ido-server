package com.bixin.ido.server.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhangcheng
 * create          2021-08-12 4:20 下午
 */
@Data
@Component
@ConfigurationProperties(prefix = "ido.star")
public class StarConfig {

    private StarDx dx = new StarDx();
    private starSwap swap = new starSwap();
    private Runner runner = new Runner();


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StarDx {

        private String resourceUrl;
        private String moduleName;

    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class starSwap {

        private String websocketHost;
        private String websocketPort;
        private String websocketContractAddress;
        private String contractAddress;
        private String lpPoolResourceName;

    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Runner {
        private SwapConsumer swapConsumer;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SwapConsumer {
        private int coreThreads;
        private int maxThreads;
    }

}
