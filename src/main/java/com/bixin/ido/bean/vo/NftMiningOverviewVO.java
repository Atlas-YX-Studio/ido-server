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
public class NftMiningOverviewVO {

    /**
     * 每日产出
     */
    private String dailyTotalOutput;

    /**
     * 待领取收益
     */
    private String currentReward;

    /**
     * 总算力
     */
    private String totalScore;

    /**
     * 用户算力
     */
    private String userScore;

    /**
     * 平均年化收益率
     */
    private String avgApr;

    /**
     * 用户年化收益率
     */
    private String userApr;

}
