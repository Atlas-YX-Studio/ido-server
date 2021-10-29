package com.bixin.ido.server.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SwapSymbolMarketDto {

    private String token0;

    private String token1;

    private BigDecimal reserve0;

    private BigDecimal reserve1;

    private BigDecimal usdtExRate0;

    private BigDecimal usdtExRate1;

    private BigDecimal swapAmount;

    private long lastSwap;
}
