package com.bixin.ido.server.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode()
@NoArgsConstructor
@AllArgsConstructor
public class TradingMiningOverviewVO {

    /**
     * 总交易额
     */
    private String totalTradingAmount;

    /**
     * 当前交易额
     */
    private String currentTradingAmount;

    /**
     * 我的交易额
     */
    private String userCurrentTradingAmount;

    /**
     * 每日产出
     */
    private String dailyTotalOutput;

    /**
     * 每日收益
     */
    private String dailyUserReward;

}
