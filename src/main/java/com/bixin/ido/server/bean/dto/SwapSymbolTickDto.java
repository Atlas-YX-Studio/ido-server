package com.bixin.ido.server.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SwapSymbolTickDto {

    private String token0;

    private String token1;

    private String direction0;

    private BigDecimal amount0;

    private BigDecimal amount1;

    private BigDecimal usdtExRate0;

    private BigDecimal usdtExRate1;

    private BigDecimal usdtAmount0;

    private BigDecimal usdtAmount1;

    private long swapTime;

}
