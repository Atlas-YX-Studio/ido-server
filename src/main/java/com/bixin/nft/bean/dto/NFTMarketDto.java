package com.bixin.nft.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhangcheng
 * create   2021/9/17
 */
public class NFTMarketDto {


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NFTDto {

        private List<Item> items;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        static class Item {
            private Long id;
            private String seller;
            private BigDecimal selling_price;
            private BidToken bid_tokens;

            @Data
            @Builder
            @NoArgsConstructor
            @AllArgsConstructor
            static class BidToken {
                private Long value;
            }

        }

    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoxDto {

        private List<NFTDto.Item> items;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Item {
            private Long id;
            private String seller;
            private BigDecimal selling_price;
            private NFTDto.Item.BidToken bid_tokens;

            @Data
            @Builder
            @NoArgsConstructor
            @AllArgsConstructor
            static class BidToken {
                private Long value;
            }

        }
    }


}
