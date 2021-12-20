package com.bixin.ido.bean.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SwapBo {
    private String tokenA;
    private String tokenB;
    // 数量
    private BigDecimal tokenAmount;
    // 滑点容差
    private BigDecimal slippageTolerance;
    // 多跳模式
    private boolean multiMode;
}
