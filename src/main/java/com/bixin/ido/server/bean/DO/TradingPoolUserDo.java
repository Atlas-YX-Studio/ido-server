package com.bixin.ido.server.bean.DO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

/**
* @Class: TradingPoolUserDo
* @Description: 用户交易挖矿表
* @author: 系统
* @created: 2021-11-09
*/
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TradingPoolUserDo implements Serializable {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 用户地址
     */
    private String address;

    /**
     * 矿池id
     */
    private Long poolId;

    /**
     * 当前交易额
     */
    private BigDecimal currentTradingAmount;

    /**
     * 累计交易额
     */
    private BigDecimal totalTradingAmount;

    /**
     * 当前收益
     */
    private BigDecimal currentReward;

    /**
     * 累计收益
     */
    private BigDecimal totalReward;

    /**
     * 待结算交易额
     */
    private BigDecimal pendingTradingReward;

    /**
     * 待结算收益
     */
    private BigDecimal pendingReward;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;

    private static final long serialVersionUID = 1L;
}