package com.bixin.ido.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Data
@SuperBuilder
@EqualsAndHashCode()
@NoArgsConstructor
@AllArgsConstructor
public class SwapPathOutVO {

    // 支付数量
    private String payAmount;

    // 按照A计算成交均价
    private String avgPriceA;

    // 按照B计算成交均价
    private String avgPriceB;

    // 最大发送量
    private String maxSold;

    // 价格影响
    private String priceImpact;

    // 手续费
    private String feeAmount;

    // 路径
    private List<String> fullPath;

    public static SwapPathOutVO convertToVO(BigDecimal payAmount, BigDecimal avgPriceA, BigDecimal avgPriceB, BigDecimal maxSold, BigDecimal priceImpact, BigDecimal feeAmount, List<String> path) {
        return new SwapPathOutVO(
                payAmount.toPlainString(),
                avgPriceA.toPlainString(),
                avgPriceB.toPlainString(),
                maxSold.toPlainString(),
                priceImpact.toPlainString(),
                feeAmount.toPlainString(),
                path
        );
    }

    public List<String> getPath() {
        return fullPath.stream().map(x->x.split("::")[2]).collect(Collectors.toList());
    }

}
