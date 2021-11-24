package com.bixin.ido.server.bean.DO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

/**
* @Class: LPMiningPoolUserDo
* @Description: 用户lp挖矿表
* @author: 系统
* @created: 2021-11-18
*/
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LPMiningPoolUserDo implements Serializable {
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
     * 质押量
     */
    private BigDecimal stakingAmount;

    /**
     * 收益
     */
    private BigDecimal reward;

    /**
     * 冻结收益
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