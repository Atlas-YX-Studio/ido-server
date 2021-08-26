package com.bixin.ido.server.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhangcheng
 * create          2021-08-12 4:20 下午
 */
@Getter
@Component
@ConfigurationProperties(prefix = "ido.star")
public class StarConfig {

    private StarDx dx = new StarDx();
    private starSwap swap = new starSwap();
    private Runner runner = new Runner();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StarDx {

        private String resourceUrl;
        private String moduleName;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class starSwap {

        private String websocketHost;
        private String websocketPort;
        private String websocketContractAddress;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Runner {
        private SwapConsumer swapConsumer;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SwapConsumer {
        private int coreThreads;
        private int maxThreads;
    }

}
