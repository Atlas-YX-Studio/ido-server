package com.bixin.ido.server.bean.DO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiquidityUserRecord {

    private Long id;

    private String userAddress;

    private String tokenCodeX;

    private String tokenCodeY;

    private Short direction;

    private BigDecimal amountX;

    private BigDecimal amountY;

    private BigDecimal reserveAmountX;

    private BigDecimal reserveAmountY;

    private Long liquidityTime;

    private Long createTime;

}