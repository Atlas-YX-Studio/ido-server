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

    private StarClient client = new StarClient();
    private StarDx dx = new StarDx();
    private starSwap swap = new starSwap();
    private Runner runner = new Runner();
    private Nft nft = new Nft();


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StarClient {

        private String url;
        private Integer chainId;

    }

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

        //starCoin-sdk 需要去掉合约地址的前缀 "0x"
        public String getWebsocketContractAddress() {
            if (this.websocketContractAddress.startsWith("0x")) {
                this.websocketContractAddress = websocketContractAddress.replaceAll(websocketContractAddress, "0x");
            }
            return this.websocketContractAddress;
        }

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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Nft{
        private String market;
        private String scripts;

    }

}
