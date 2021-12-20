package com.bixin.ido.bean.DO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

/**
* @Class: LPMiningPoolDo
* @Description: lp挖矿矿池表
* @author: 系统
* @created: 2021-11-18
*/
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LPMiningPoolDo implements Serializable {
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
     * 总质押量
     */
    private BigDecimal totalStakingAmount;

    /**
     * 权重
     */
    private Integer weight;

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