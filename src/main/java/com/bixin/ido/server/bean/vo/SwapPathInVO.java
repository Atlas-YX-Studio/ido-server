package com.bixin.ido.server.bean.vo;

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
public class SwapPathInVO {

    // 兑换数量
    private String exchangeAmount;

    // 按照A计算成交均价
    private String avgPriceA;

    // 按照B计算成交均价
    private String avgPriceB;

    // 最小接收量
    private String minReceived;

    // 价格影响
    private String priceImpact;

    // 手续费
    private String feeAmount;

    // 路径
    private List<String> fullPath;

    public static SwapPathInVO convertToVO(BigDecimal exchangeAmount, BigDecimal avgPriceA, BigDecimal avgPriceB, BigDecimal minReceived, BigDecimal priceImpact, BigDecimal feeAmount, List<String> path) {
        return new SwapPathInVO(
                exchangeAmount.toPlainString(),
                avgPriceA.toPlainString(),
                avgPriceB.toPlainString(),
                minReceived.toPlainString(),
                priceImpact.toPlainString(),
                feeAmount.toPlainString(),
                path
        );
    }

    public List<String> getPath() {
        return fullPath.stream().map(x->x.split("::")[2]).collect(Collectors.toList());
    }
}
