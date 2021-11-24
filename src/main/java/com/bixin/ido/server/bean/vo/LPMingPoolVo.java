package com.bixin.ido.server.bean.vo;

import com.bixin.ido.server.bean.DO.LPMiningPoolDo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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
public class LPMingPoolVo extends LPMiningPoolDo {

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
//    private String totalStakingAmount;

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