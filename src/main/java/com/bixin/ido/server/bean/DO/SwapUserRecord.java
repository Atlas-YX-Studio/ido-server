package com.bixin.ido.server.bean.DO;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;


@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class SwapUserRecord extends BaseDO{
    private Long id;

    private String userAddress;

    private String tokenCodeX;

    private String tokenCodeY;

    private BigDecimal tokenInX;

    private BigDecimal tokenInY;

    private BigDecimal tokenOutX;

    private BigDecimal tokenOutY;

    private BigDecimal reserveAmountX;

    private BigDecimal reserveAmountY;

    private Long swapTime;

    private Long createTime;

}