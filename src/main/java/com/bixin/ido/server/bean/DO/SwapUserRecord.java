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
public class SwapUserRecord {
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