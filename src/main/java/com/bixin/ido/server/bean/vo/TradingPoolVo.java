package com.bixin.ido.server.bean.vo;

import com.bixin.ido.server.bean.DO.TradingPoolDo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * @class: TradingPoolVO
 * @Description: 交易挖矿矿池表 实体类
 * @author: 系统
 * @created: 2021-11-05
 */
@Data
@SuperBuilder
@EqualsAndHashCode()
@NoArgsConstructor
@AllArgsConstructor
public class TradingPoolVo extends TradingPoolDo {

    /**
     * 年华收益率
     */
    private BigDecimal apy;

    /**
     * 我的交易额
     */
    private BigDecimal tradingAmount;

    /**
     * 当前收益
     */
    private BigDecimal currentReward;

    /**
     * 累计收益
     */
    private BigDecimal totalReward;

    /**
     * tokenA icon
     */
    private String tokenIconA;

    /**
     * tokenB icon
     */
    private String tokenIconB;

}