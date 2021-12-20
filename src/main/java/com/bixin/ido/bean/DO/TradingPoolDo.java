package com.bixin.ido.bean.DO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

/**
* @Class: TradingPoolDo
* @Description: 交易挖矿矿池表
* @author: 系统
* @created: 2021-11-05
*/
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TradingPoolDo implements Serializable {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 交易对名
     */
    private String pairName;

    /**
     * 币种A
     */
    private String tokenA;

    /**
     * 币种B
     */
    private String tokenB;

    /**
     * 奖励分配倍数
     */
    private Integer allocationMultiple;

    /**
     * 当前交易额
     */
    private BigDecimal currentTradingAmount;

    /**
     * 累计交易额
     */
    private BigDecimal totalTradingAmount;

    /**
     * 已分配奖励
     */
    private BigDecimal allocatedRewardAmount;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 权重
     */
    private Integer weight;

    private static final long serialVersionUID = 1L;
}