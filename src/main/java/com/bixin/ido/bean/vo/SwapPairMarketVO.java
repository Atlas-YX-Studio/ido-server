package com.bixin.ido.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwapPairMarketVO {

    private String token0;

    private String token1;

    private BigDecimal reserve0;

    private BigDecimal reserve1;

    private BigDecimal usdtPrice0;

    private BigDecimal usdtPrice1;

    private BigDecimal swapAmount;

    private long lastSwap;

}
