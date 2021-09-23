package com.bixin.nft.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 取消事件
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NftOffLineEventtDto {

    // nft id
    private Long id;
    // 售卖者
    private String seller;
    // 售价
    private BigDecimal selling_price;
    // 出价者
    private String bider;
    // 出价
    private BigDecimal bid_price;
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
