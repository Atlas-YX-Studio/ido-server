package com.bixin.ido.server.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode()
@NoArgsConstructor
@AllArgsConstructor
public class CoinStatsInfoVO {

    /**
     * 币种名称
     */
    private String name;

    private String icon;

    /**
     * 价格
     */
    private String price;

    /**
     * 24小时价格变化
     */
    private String rate;

    /**
     * 24小时交易额
     */
    private String amount;

    /**
     * 流动性
     */
    private String liquidity;

    public static CoinStatsInfoVO convertToVO(String name, String icon, BigDecimal price, BigDecimal rate, BigDecimal amount, BigDecimal liquidity) {
        return new CoinStatsInfoVO(
                name,
                icon,
                price.toPlainString(),
                rate.toPlainString(),
                amount.toPlainString(),
                liquidity.toPlainString()
        );
    }
}
