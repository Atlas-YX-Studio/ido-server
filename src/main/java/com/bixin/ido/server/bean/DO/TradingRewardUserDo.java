package com.bixin.ido.server.bean.DO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

/**
* @Class: TradingRewardUserDo
* @Description: 用户交易挖矿收益表
* @author: 系统
* @created: 2021-11-09
*/
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TradingRewardUserDo implements Serializable {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 用户地址
     */
    private String address;

    /**
     * 锁定收益
     */
    private BigDecimal lockedReward;

    /**
     * 已释放收益
     */
    private BigDecimal freedReward;

    /**
     * 每日解锁收益
     */
    private BigDecimal unlockRewardPerDay;

    /**
     * 待结算收益
     */
    private BigDecimal pendingReward;

    /**
     * 下次解锁时间
     */
    private Long nextUnlockTime;

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