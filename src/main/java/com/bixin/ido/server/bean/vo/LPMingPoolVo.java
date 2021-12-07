package com.bixin.ido.server.bean.vo;

import com.bixin.ido.server.bean.DO.LPMiningPoolDo;
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
public class LPMingPoolVo {

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
     * 权重
     */
    private Integer weight;

    /**
     * 年华收益率
     */
    private String apy;

    /**
     * 复利年化收益率
     */
    private String compoundApy;

    /**
     * 个人质押
     */
    private String userStakingAmount;

    /**
     * 待领取收益
     */
    private String userReward;

    /**
     * 总质押
     */
    private String totalStakingAmount;

    /**
     * 每日产出
     */
    private String dailyTotalOutput;

    /**
     * tokenA icon
     */
    private String tokenIconA;

    /**
     * tokenB icon
     */
    private String tokenIconB;

}