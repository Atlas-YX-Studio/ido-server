package com.bixin.nft.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * nft出售事件
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NftSellEventtDto {
    // nft id
    private Long id;
    // 出售者
    private String seller;
    // 出售价格
    private BigDecimal selling_price;
    // 支付token
    private Pay_token_code pay_token_code;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class Pay_token_code {
        private String addr;
        private String module_name;
        private String name;
    }

}
