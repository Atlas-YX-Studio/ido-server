package com.bixin.ido.server.bean.DO;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class LiquidityUserRecord extends BaseDO{
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